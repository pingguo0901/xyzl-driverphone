package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** KYC 证件照桶（私有桶） */
private const val BUCKET_KYC = "se_kyc-drivers"

/**
 * 注册页收集的全部资料 —— 每个字段落哪张表见《注册页接线表》。
 * 昵称 → se_users.full_name；姓名 → 司机档案 full_name（个人则 se_personal_profiles.legal_name）
 * 性别/生日/门牌及街道/花园/邮编/城市/州属 → 档案表
 * 手机号/邮箱/微信号 → se_users
 * 创建密码 → 只给 Supabase Auth；确认密码 → 只在前端校验
 * 身份证/驾照/护照 → Storage se_kyc-drivers + se_driver_documents.file_url
 */
data class RegisterPayload(
    val email: String,
    val password: String,
    val nickname: String,
    val fullName: String,
    val gender: String,
    val birthday: String,
    val street: String,
    val garden: String,
    val postcode: String,
    val city: String,
    val state: String,
    val phone: String,
    val wechatId: String,
    val inviteCode: String? = null,
    val icUri: String? = null,
    val licenseUri: String? = null,
    val passportUri: String? = null,
) {
    /** 司机端：填了邀请码 = 公司司机(driver)；没填 = 个人(personal) */
    val role: String
        get() = if (inviteCode.isNullOrBlank()) UserRole.PERSONAL.code else UserRole.DRIVER.code
}

/** 注册结果 */
data class RegisterResult(
    val userId: String,
    val accountId: String?,
    /** 证件照没传上去的原因（注册本身已成功，这些只是附件失败） */
    val kycFailures: List<String> = emptyList(),
)

class AuthRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    // ---------------------------------------------------------- 注册：只建 Auth 账号
    suspend fun signUp(email: String, password: String): Result<String> = apiCall {
        ensureAuthUser(email, password)
    }

    /**
     * 拿到（必要时创建）Auth 账号的 userId —— 幂等，能自愈：
     * ① 先 signUp；
     * ② 没拿到用户（邮箱已注册 / 上次注册只建了 Auth 没写业务表 / 返回体没有 user）→ 用同样的邮箱+密码登录一次；
     * ③ 已经登录且邮箱对得上 → 直接用当前会话。
     * 只有三种都失败才报错，绝不因为 Auth 层的小状况把用户卡死在注册页。
     */
    private suspend fun ensureAuthUser(email: String, password: String): String {
        val target = email.trim()
        var signUpError: Throwable? = null
        val created = try {
            supabase.auth.signUpWith(Email) {
                this.email = target
                this.password = password
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            signUpError = e
            null
        }
        created?.id?.let { return it }

        supabase.auth.currentUserOrNull()
            ?.takeIf { it.email?.equals(target, ignoreCase = true) == true }
            ?.let { return it.id }

        try {
            supabase.auth.signInWith(Email) {
                this.email = target
                this.password = password
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(friendlySignUpError(signUpError, e), e)
        }
        val signedIn = supabase.auth.currentUserOrNull()
        if (signedIn != null && signedIn.email?.equals(target, ignoreCase = true) == true) return signedIn.id
        throw IllegalStateException("注册失败：拿不到登录状态，请重试")
    }

    /** 这个用户在业务表 se_users 里有没有档案（用来判断是不是「注册写到一半」） */
    private suspend fun userRowExists(userId: String): Boolean = try {
        supabase.postgrest.from("se_users")
            .select(Columns.list("id")) { filter { eq("id", userId) } }
            .decodeList<JsonObject>()
            .isNotEmpty()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    /** 已有档案时补拿 account_id（自己开的号自己就是 owner） */
    private suspend fun ownerAccountId(userId: String): String? = try {
        supabase.postgrest.from("se_accounts")
            .select(Columns.list("id")) { filter { eq("owner_user_id", userId) } }
            .decodeList<JsonObject>()
            .firstOrNull()?.get("id")?.jsonPrimitive?.contentOrNull
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    // ---------------------------------------------------------- 登录 / 退出
    suspend fun signIn(email: String, password: String): Result<Unit> = try {
        supabase.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
        // 登录成功 → 记录当前设备（登录设备管理页才有数据）
        supabase.auth.currentUserOrNull()?.id?.let { recordDevice(it) }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception(friendlyLoginError(e)))
    }

    private suspend fun signInRaw(email: String, password: String): Result<Unit> = apiCall {
        supabase.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
    }

    suspend fun signOut(): Result<Unit> = apiCall { supabase.auth.signOut() }

    fun getCurrentUser(): UserInfo? = supabase.auth.currentUserOrNull()

    fun observeAuthState(): Flow<AuthState> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val userId = status.session.user?.id.orEmpty()
                    AuthState.Authenticated(
                        userId = userId,
                        email = status.session.user?.email,
                        role = getUserRole(userId),
                    )
                }
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                is SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.RefreshFailure -> AuthState.Error("登录状态刷新失败，请重新登录")
            }
        }

    // ---------------------------------------------------------- RPC：register_user
    suspend fun registerUser(
        userId: String,
        email: String,
        fullName: String,
        phone: String,
        role: String,
        inviteCode: String?,
    ): Result<JsonElement> = apiCall {
        val response: JsonElement = supabase.postgrest.rpc(
            function = "register_user",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_email", email.trim())
                put("p_full_name", fullName)
                put("p_phone", phone)
                put("p_role", role)
                if (inviteCode.isNullOrBlank()) put("p_invite_code", JsonNull)
                else put("p_invite_code", inviteCode.trim())
            },
        ).decodeAs<JsonElement>()
        val success = (response as? JsonObject)
            ?.get("success")
            ?.let { (it as? JsonPrimitive)?.booleanOrNull }
            ?: false
        if (!success) throw IllegalStateException("注册资料写入失败：" + response.toString())
        response
    }

    // ---------------------------------------------------------- RPC：register_driver（注册页真正该用的那个）
    /**
     * 注册页唯一写档案入口：把注册页所有必填字段【一次性】交给 register_driver，单事务落库。
     * register_driver 按「有没有邀请码」自己判断角色 ——
     *   无码 → personal（写 se_personal_profiles），有码 → driver（写 se_driver_profiles），
     * 性别 / 生日 / 门牌及街道 / 花园 / 邮编 / 城市 / 州属 全部由 RPC 直接写进去。
     * 失败一律 RAISE EXCEPTION（不会返回 success:false）→ 这里再兜一层，原文抛给注册页显示。
     */
    suspend fun registerDriver(
        userId: String,
        payload: RegisterPayload,
    ): Result<JsonElement> = apiCall {
        val response: JsonElement = supabase.postgrest.rpc(
            function = "register_driver",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_email", payload.email.trim())
                put("p_nickname", payload.nickname)
                put("p_full_name", payload.fullName)
                putBlankAsNull("p_gender", payload.gender)
                putBlankAsNull("p_birthday", payload.birthday)   // date 列：空串会报错，必须传 null
                putBlankAsNull("p_addr_line1", payload.street)
                putBlankAsNull("p_garden", payload.garden)
                putBlankAsNull("p_postcode", payload.postcode)
                putBlankAsNull("p_city", payload.city)
                putBlankAsNull("p_state", payload.state)
                putBlankAsNull("p_phone", payload.phone)
                putBlankAsNull("p_wechat_id", payload.wechatId)
                val code = payload.inviteCode
                if (code.isNullOrBlank()) put("p_invite_code", JsonNull)
                else put("p_invite_code", code.trim())
            },
        ).decodeAs<JsonElement>()
        val success = (response as? JsonObject)
            ?.get("success")
            ?.let { (it as? JsonPrimitive)?.booleanOrNull }
            ?: false
        if (!success) throw IllegalStateException("注册资料写入失败：" + response.toString())
        response
    }

    // ---------------------------------------------------------- 一步注册（注册页用这个）
    /** signUp → register_user → 补齐档案字段 → 上传 KYC 证件照 */
    suspend fun registerFull(payload: RegisterPayload): Result<RegisterResult> = apiCall {
        try {
            finishRegistration(payload)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // 业务表没写完就不把人放进首页：退掉会话，停在注册页重试（重试会自动补完）
            try {
                supabase.auth.signOut()
            } catch (ignored: Exception) {
            }
            throw e
        }
    }

    private suspend fun finishRegistration(payload: RegisterPayload): RegisterResult {
        val userId = ensureAuthUser(payload.email, payload.password)

        // 业务档案已经存在（上次写到一半 / 重复提交）→ 不再重复建号建档案，只把字段补齐
        val hasProfile = userRowExists(userId)
        // 注册页必填字段一次性交给 register_driver（单事务写进 se_personal_profiles / se_driver_profiles，
        // 不再依赖"建号后补一次 update"——那一步会被取消/超时打断，字段就留 NULL）
        val rpc: JsonElement? = if (hasProfile) null else registerDriver(
            userId = userId,
            payload = payload,
        ).getOrElse { throw it }
        val accountId = (rpc as? JsonObject)?.get("account_id")?.jsonPrimitive?.contentOrNull
            ?: ownerAccountId(userId)

        // ① se_users：昵称 / 手机 / 邮箱 / 微信号（性别生日也存一份）
        supabase.postgrest.from("se_users").update(
            buildJsonObject {
                put("full_name", payload.nickname)
                putBlankAsNull("phone", payload.phone)
                put("email", payload.email.trim())
                putBlankAsNull("wechat_id", payload.wechatId)
                putBlankAsNull("gender", payload.gender)
                putBlankAsNull("birthday", payload.birthday)
            }
        ) { filter { eq("id", userId) } }

        // ② 档案表：姓名 + 性别/生日/地址
        val isPersonal = payload.role == UserRole.PERSONAL.code
        val table = if (isPersonal) "se_personal_profiles" else "se_driver_profiles"
        val nameColumn = if (isPersonal) "legal_name" else "full_name"
        supabase.postgrest.from(table).update(
            buildJsonObject {
                put(nameColumn, payload.fullName)
                putBlankAsNull("gender", payload.gender)
                putBlankAsNull("birthday", payload.birthday)
                putBlankAsNull("addr_line1", payload.street)
                putBlankAsNull("garden", payload.garden)
                putBlankAsNull("postcode", payload.postcode)
                putBlankAsNull("city", payload.city)
                putBlankAsNull("state", payload.state)
            }
        ) { filter { eq("user_id", userId) } }

        // ③ KYC 证件照 → Storage + se_driver_documents
        val failures = uploadKycDocuments(
            userId = userId,
            role = payload.role,
            accountId = accountId,
            icUri = payload.icUri,
            licenseUri = payload.licenseUri,
            passportUri = payload.passportUri,
        )

        // 注册成功 → 同样记录当前设备
        recordDevice(userId)

        // 会话先于档案到位：刷新一次，让登录态重新取 primary_role（不刷新会带着 null）
        try {
            supabase.auth.refreshCurrentSession()
        } catch (e: CancellationException) {
            throw e
        } catch (ignored: Exception) {
        }

        return RegisterResult(userId = userId, accountId = accountId, kycFailures = failures)
    }

    // ---------------------------------------------------------- KYC 证件照
    private suspend fun uploadKycDocuments(
        userId: String,
        role: String,
        accountId: String?,
        icUri: String?,
        licenseUri: String?,
        passportUri: String?,
    ): List<String> {
        val files = listOf(
            Triple("ic", icUri, "ic.jpg"),
            Triple("license", licenseUri, "license.jpg"),
            Triple("passport", passportUri, "passport.jpg"),
        )
        val failures = mutableListOf<String>()
        val uploaded = mutableListOf<Pair<String, String>>()

        for ((type, uri, name) in files) {
            if (uri.isNullOrBlank()) {
                failures += type + "：未选择文件"
                continue
            }
            try {
                val bytes = readUriBytes(uri)
                if (bytes == null || bytes.isEmpty()) {
                    failures += type + "：读不到本地文件"
                    continue
                }
                val path = userId + "/" + name
                supabase.storage.from(BUCKET_KYC).upload(path, bytes) { upsert = true }
                uploaded += type to path
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failures += type + "：" + (e.message ?: "上传失败")
            }
        }

        // 只有司机有 se_driver_profiles，才写 se_driver_documents
        if (role == UserRole.DRIVER.code && accountId != null && uploaded.isNotEmpty()) {
            try {
                val driverId = supabase.postgrest.from("se_driver_profiles")
                    .select(Columns.list("id")) { filter { eq("user_id", userId) } }
                    .decodeList<JsonObject>()
                    .firstOrNull()?.get("id")?.jsonPrimitive?.contentOrNull

                if (driverId == null) {
                    failures += "找不到司机档案，证件照没登记"
                } else {
                    for ((type, path) in uploaded) {
                        supabase.postgrest.from("se_driver_documents").insert(
                            buildJsonObject {
                                put("account_id", accountId)
                                put("driver_id", driverId)
                                put("type", type)
                                put("file_url", path)
                                put("status", "pending")
                                put("uploaded_by_user_id", userId)
                            }
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failures += "证件照登记失败：" + (e.message ?: "未知错误")
            }
        }

        return failures
    }

    // ---------------------------------------------------------- 登录后拿角色（决定进哪个首页）
    suspend fun getUserRole(userId: String): String? = try {
        supabase.postgrest.from("se_users")
            .select(Columns.list("primary_role")) { filter { eq("id", userId) } }
            .decodeList<JsonObject>()
            .firstOrNull()?.get("primary_role")?.jsonPrimitive?.contentOrNull
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    // ---------------------------------------------------------- 忘记密码（全走 Supabase Auth）
    /** 发送重置密码邮件：邮件里的链接点开直接回到 App（deep link），再设置新密码 */
    suspend fun sendPasswordResetEmail(email: String, redirectTo: String): Result<Unit> = apiCall {
        supabase.auth.resetPasswordForEmail(email.trim(), redirectTo)
    }

    /** 设置新密码（需已通过验证码，处于 recovery 会话） */
    suspend fun updatePassword(newPassword: String): Result<Unit> = apiCall {
        supabase.auth.updateUser {
            this.password = newPassword
        }
    }

    /**
     * 校验原密码：用邮箱 + 原密码试一次登录；成功说明原密码对。
     * 失败返回 false（密码错 / 邮箱空 / 网络问题都算失败）
     */
    suspend fun verifyPassword(email: String?, password: String): Boolean {
        if (email.isNullOrBlank() || password.isBlank()) return false
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    /** 记录登录设备；失败不影响登录/注册主流程 */
    private suspend fun recordDevice(userId: String) {
        try {
            SecurityRepository(supabase).recordCurrentDevice(userId)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    // ---------------------------------------------------------- 统一异常包装
    private suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * 认证/登录失败一律给用户看短句，不暴露底层错误码与原始报文。
 * Auth 找不到该用户 / 密码不匹配 → 「账号与密码错误」
 */
fun friendlySignUpError(signUpError: Throwable?, signInError: Throwable): String {
    val su = (signUpError?.message ?: "").lowercase()
    if (su.contains("already") || su.contains("exists")) {
        return "该邮箱已注册，但密码对不上。如果你就是本人，请直接登录；忘记密码可以点「忘记密码」。"
    }
    return friendlyLoginError(signInError)
}

fun friendlyLoginError(e: Throwable): String {
    val m = (e.message ?: "").lowercase()
    return when {
        m.contains("email not confirmed") || m.contains("not confirmed") -> "邮箱还没验证，请先到邮箱点验证链接"
        m.contains("too many") || m.contains("rate limit") -> "尝试太频繁，请稍后再试"
        m.contains("network") || m.contains("timeout") || m.contains("unable to resolve") ||
            m.contains("connect") || m.contains("offline") -> "网络异常，请检查网络后重试"
        else -> "账号与密码错误"
    }
}

/** 空字符串 → NULL（宁可空着也别写空串） */
private fun JsonObjectBuilder.putBlankAsNull(key: String, value: String?) {
    if (value.isNullOrBlank()) put(key, JsonNull) else put(key, value)
}
