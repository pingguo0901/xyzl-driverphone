package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
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
        val user: UserInfo = supabase.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
        } ?: throw IllegalStateException("注册失败：Supabase 未返回用户（若后台开了邮箱确认，请先关闭）")
        user.id
    }

    // ---------------------------------------------------------- 登录 / 退出
    suspend fun signIn(email: String, password: String): Result<Unit> = apiCall {
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

    // ---------------------------------------------------------- 一步注册（注册页用这个）
    /** signUp → register_user → 补齐档案字段 → 上传 KYC 证件照 */
    suspend fun registerFull(payload: RegisterPayload): Result<RegisterResult> = apiCall {
        val userId = signUp(payload.email, payload.password).getOrElse { throw it }

        val rpc = registerUser(
            userId = userId,
            email = payload.email,
            fullName = payload.fullName,
            phone = payload.phone,
            role = payload.role,
            inviteCode = payload.inviteCode,
        ).getOrElse { throw it }
        val accountId = (rpc as? JsonObject)?.get("account_id")?.jsonPrimitive?.contentOrNull

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

        RegisterResult(userId = userId, accountId = accountId, kycFailures = failures)
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
    /** 发送重置验证码到邮箱 */
    suspend fun sendPasswordResetCode(email: String): Result<Unit> = apiCall {
        supabase.auth.resetPasswordForEmail(email.trim())
    }

    /** 校验邮箱收到的验证码（Recovery 类型） */
    suspend fun verifyRecoveryCode(email: String, code: String): Result<Unit> = apiCall {
        supabase.auth.verifyEmailOtp(OtpType.Email.RECOVERY, email.trim(), code.trim())
    }

    /** 设置新密码（需已通过验证码，处于 recovery 会话） */
    suspend fun updatePassword(newPassword: String): Result<Unit> = apiCall {
        supabase.auth.updateUser {
            this.password = newPassword
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

/** 空字符串 → NULL（宁可空着也别写空串） */
private fun JsonObjectBuilder.putBlankAsNull(key: String, value: String?) {
    if (value.isNullOrBlank()) put(key, JsonNull) else put(key, value)
}
