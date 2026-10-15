package com.xyzl.driverphone

import com.xyzl.driverphone.screens.currentTimeParts
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** Storage 桶：头像 */
private const val BUCKET_AVATARS = "se_avatars"

/** Storage 桶：KYC 证件（与注册时同一桶） */
private const val BUCKET_KYC_DOCS = "se_kyc-drivers"

/** 找不到 account 时的兜底 system 账户 */
private const val SYSTEM_ACCOUNT = "00000000-0000-0000-0000-000000000001"

/** 个人信息（se_users） */
data class MyInfo(
    val fullName: String = "",
    val avatarUrl: String? = null,
    val displayId: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val wechatId: String? = null,
)

/** 隐私信息（se_driver_profiles） */
data class MyPrivacy(
    val profileId: String? = null,
    val fullName: String = "",
    val gender: String? = null,
    val birthday: String? = null,
    val street: String = "",
    val garden: String = "",
    val postcode: String = "",
    val city: String = "",
    val state: String = "",
)

/** KYC 一行（se_driver_documents：type + status + expiry_date） */
data class KycRow(
    val type: String,
    val status: String?,
    val expiryDate: String?,
    /** 有效 = status=approved 且未过期（前端算出来的，不是库里存的） */
    val valid: Boolean,
)

/** 扫到的用户 */
data class ScannedUser(
    val userId: String,
    val name: String,
    val avatarUrl: String?,
)

/** 我的个人二维码 */
data class MyQr(
    /** 二维码编码的内容（真实 token，没有则退回个人链接） */
    val content: String,
    val imageUrl: String?,
    val token: String?,
)

/**
 * 个人二维码 / 扫描加好友 / 个人信息 / 隐私信息 / KYC
 *
 * - 二维码：se_user_qr_codes where user_id=我 and qr_category='profile'
 * - 头像昵称：se_users.avatar_url / full_name
 * - 扫到 token → se_user_qr_codes.qr_token → user_id → se_users
 * - 加好友：se_friend_connections INSERT(status='pending')
 * - 个人信息：se_users.display_id / phone / email / wechat_id
 * - 隐私信息：se_driver_profiles（addr_line1 / garden / postcode / city / state）
 * - KYC：se_driver_documents where driver_id=我（type = ic / license / passport）
 */
class ProfileEditRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    /** 我的账户 id（se_account_members） */
    suspend fun accountId(userId: String): String? = runCatchingNull {
        supabase.postgrest.from("se_account_members")
            .select(Columns.list("account_id")) { filter { eq("user_id", userId) }; limit(1) }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.s("account_id")
    }

    // ---------------------------------------------------------- 个人信息

    suspend fun myInfo(userId: String): MyInfo? {
        val row = runCatchingNull {
            supabase.postgrest.from("se_users")
                .select(Columns.list("full_name", "avatar_url", "display_id", "phone", "email", "wechat_id")) {
                    filter { eq("id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } ?: return null
        return MyInfo(
            fullName = row.s("full_name").orEmpty(),
            avatarUrl = row.s("avatar_url"),
            displayId = row.s("display_id"),
            phone = row.s("phone"),
            email = row.s("email"),
            wechatId = row.s("wechat_id"),
        )
    }

    /** 保存个人信息：se_users UPDATE（display_id / phone / email / wechat_id / full_name / avatar_url） */
    suspend fun saveInfo(
        userId: String,
        fullName: String,
        displayId: String?,
        phone: String?,
        email: String?,
        wechatId: String?,
        avatarUrl: String?,
    ): String? = try {
        supabase.postgrest.from("se_users").update(
            buildJsonObject {
                put("full_name", fullName)
                put("display_id", displayId)
                put("phone", phone)
                put("email", email)
                put("wechat_id", wechatId)
                if (!avatarUrl.isNullOrBlank()) put("avatar_url", avatarUrl)
            },
        ) { filter { eq("id", userId) } }
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        friendly(e.message)
    }

    /** 上传头像到 se_avatars，返回公开 URL；失败 null */
    suspend fun uploadAvatar(userId: String, uri: String): String? = try {
        val bytes = readUriBytes(uri)
        if (bytes == null || bytes.isEmpty()) {
            null
        } else {
            val ext = if (uri.contains(".png", true)) "png" else "jpg"
            val (date, time) = currentTimeParts()
            val stamp = date.replace("-", "") + time.replace(":", "")
            val path = userId + "/avatar-" + stamp + "." + ext
            supabase.storage.from(BUCKET_AVATARS).upload(path, bytes) { upsert = true }
            Supabase.URL + "/storage/v1/object/public/" + BUCKET_AVATARS + "/" + path
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    // ---------------------------------------------------------- 隐私信息

    suspend fun myPrivacy(userId: String, accountId: String?): MyPrivacy? {
        val row = driverProfile(userId, accountId) ?: return null
        return MyPrivacy(
            profileId = row.s("id"),
            fullName = row.s("full_name").orEmpty(),
            gender = row.s("gender"),
            birthday = row.s("birthday"),
            street = row.s("addr_line1").orEmpty(),
            garden = row.s("garden").orEmpty(),
            postcode = row.s("postcode").orEmpty(),
            city = row.s("city").orEmpty(),
            state = row.s("state").orEmpty(),
        )
    }

    /** 司机档案行（先按 user_id 找，没有再按 owner_id=account_id 找） */
    private suspend fun driverProfile(userId: String, accountId: String?): JsonObject? {
        val byUser = runCatchingNull {
            supabase.postgrest.from("se_driver_profiles")
                .select(Columns.list("id", "full_name", "gender", "birthday", "addr_line1", "garden", "postcode", "city", "state")) {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        }
        if (byUser != null) return byUser
        val owner = accountId ?: return null
        return runCatchingNull {
            supabase.postgrest.from("se_driver_profiles")
                .select(Columns.list("id", "full_name", "gender", "birthday", "addr_line1", "garden", "postcode", "city", "state")) {
                    filter { eq("owner_id", owner) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        }
    }

    suspend fun savePrivacy(
        profileId: String,
        fullName: String,
        gender: String?,
        birthday: String?,
        street: String,
        garden: String,
        postcode: String,
        city: String,
        state: String,
    ): String? = try {
        supabase.postgrest.from("se_driver_profiles").update(
            buildJsonObject {
                put("full_name", fullName)
                put("gender", gender)
                put("birthday", birthday)
                put("addr_line1", street)
                put("garden", garden)
                put("postcode", postcode)
                put("city", city)
                put("state", state)
            },
        ) { filter { eq("id", profileId) } }
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        friendly(e.message)
    }

    // ---------------------------------------------------------- KYC

    /** se_driver_documents：driver_id 可能是档案 id，也可能是 user_id，两个都比对 */
    suspend fun kycDocs(userId: String, profileId: String?): List<KycRow> {
        val ids = listOfNotNull(profileId, userId).distinct()
        val rows = runCatchingNull {
            supabase.postgrest.from("se_driver_documents")
                .select(Columns.list("type", "status", "expiry_date")) {
                    filter { isIn("driver_id", ids) }
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        val today = todayIso()
        return rows.map { r ->
            val status = r.s("status")
            val expiry = r.s("expiry_date")?.take(10)
            val valid = status == "approved" && (expiry == null || expiry >= today)
            KycRow(type = r.s("type").orEmpty(), status = status, expiryDate = expiry, valid = valid)
        }
    }

    /**
     * 更新 KYC 证件（「更新 KYC 认证」页的确认更新）：
     * 1) 上传到 Storage se_kyc-drivers
     * 2) se_driver_documents 有这条 (driver_id + type) 就更新，没有就插入；status 回到 pending
     * 返回 null = 成功，否则错误文案
     */
    suspend fun submitDocument(
        userId: String,
        accountId: String?,
        profileId: String?,
        type: String,
        uri: String,
    ): String? {
        val bytes = try {
            readUriBytes(uri)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        if (bytes == null || bytes.isEmpty()) return "读不到你选的文件，换一张试试"

        val ext = when {
            uri.contains(".png", true) -> "png"
            uri.contains(".pdf", true) -> "pdf"
            else -> "jpg"
        }
        val (date, time) = currentTimeParts()
        val stamp = date.replace("-", "") + time.replace(":", "")
        val path = userId + "/" + type + "-" + stamp + "." + ext
        try {
            supabase.storage.from(BUCKET_KYC_DOCS).upload(path, bytes) { upsert = true }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return "上传失败：" + (e.message ?: "未知错误").take(100)
        }

        val driverId = profileId ?: return "找不到你的司机档案，证件没法登记"
        val existingId = runCatchingNull {
            supabase.postgrest.from("se_driver_documents")
                .select(Columns.list("id")) {
                    filter {
                        eq("driver_id", driverId)
                        eq("type", type)
                    }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?.s("id")
        }

        return try {
            if (existingId != null) {
                supabase.postgrest.from("se_driver_documents").update(
                    buildJsonObject {
                        put("file_url", path)
                        put("status", "pending")
                        put("reject_reason", null)
                        put("uploaded_by_user_id", userId)
                    },
                ) { filter { eq("id", existingId) } }
            } else {
                supabase.postgrest.from("se_driver_documents").insert(
                    buildJsonObject {
                        put("account_id", accountId ?: SYSTEM_ACCOUNT)
                        put("driver_id", driverId)
                        put("type", type)
                        put("file_url", path)
                        put("status", "pending")
                        put("uploaded_by_user_id", userId)
                    },
                )
            }
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            friendly(e.message)
        }
    }

    // ---------------------------------------------------------- 二维码

    /** 我的个人二维码：qr_category='profile' */
    suspend fun myQr(userId: String): MyQr {
        val row = runCatchingNull {
            supabase.postgrest.from("se_user_qr_codes")
                .select(Columns.list("qr_token", "qr_image_url")) {
                    filter {
                        eq("user_id", userId)
                        eq("qr_category", "profile")
                    }
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        }
        val token = row?.s("qr_token")
        val image = row?.s("qr_image_url")
        val content = when {
            !token.isNullOrBlank() -> ProfileQrLinkPrefix + token
            else -> ProfileLinkPrefix + userId
        }
        return MyQr(content = content, imageUrl = image, token = token)
    }

    /**
     * 扫描结果解析：
     * 1) 内容是 token 或 …/q/<token> → 查 se_user_qr_codes.qr_token → user_id
     * 2) 内容里带 user id（…/u/<uuid>）→ 直接用
     * 3) 内容本身就是 uuid
     */
    suspend fun resolveScan(raw: String): ScannedUser? {
        val text = raw.trim()
        if (text.isEmpty()) return null

        val token = when {
            text.contains("/q/") -> text.substringAfterLast("/q/").substringBefore("?").trim()
            else -> text
        }
        var userId = runCatchingNull {
            supabase.postgrest.from("se_user_qr_codes")
                .select(Columns.list("user_id")) {
                    filter { eq("qr_token", token) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?.s("user_id")
        }

        if (userId == null) {
            val candidate = when {
                text.contains("/u/") -> text.substringAfterLast("/u/").substringBefore("?").substringBefore("/").trim()
                UUID_RE.matches(text) -> text
                else -> null
            }
            if (candidate != null) userId = candidate
        }

        val id = userId ?: return null
        val user = runCatchingNull {
            supabase.postgrest.from("se_users")
                .select(Columns.list("full_name", "avatar_url")) {
                    filter { eq("id", id) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } ?: return null

        return ScannedUser(
            userId = id,
            name = user.s("full_name") ?: "用户",
            avatarUrl = user.s("avatar_url"),
        )
    }

    /** 加好友：se_friend_connections INSERT(status='pending')；返回 null=成功，否则错误文案 */
    suspend fun addFriend(myUserId: String, myAccountId: String?, otherUserId: String): String? {
        if (myUserId == otherUserId) return "这是你自己的二维码"
        val existing = runCatchingNull {
            supabase.postgrest.from("se_friend_connections")
                .select(Columns.list("id", "status")) {
                    filter {
                        eq("user_a_id", myUserId)
                        eq("user_b_id", otherUserId)
                    }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } ?: runCatchingNull {
            supabase.postgrest.from("se_friend_connections")
                .select(Columns.list("id", "status")) {
                    filter {
                        eq("user_a_id", otherUserId)
                        eq("user_b_id", myUserId)
                    }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        }
        if (existing != null) {
            return when (existing.s("status")) {
                "accepted" -> "你们已经是好友"
                "blocked" -> "无法添加对方为好友"
                "pending" -> "已发送过好友申请，等待对方确认"
                else -> null
            }
        }
        return try {
            supabase.postgrest.from("se_friend_connections").insert(
                buildJsonObject {
                    put("account_id", myAccountId ?: SYSTEM_ACCOUNT)
                    put("user_a_id", myUserId)
                    put("user_b_id", otherUserId)
                    put("status", "pending")
                    put("requested_by_user_id", myUserId)
                    put("requested_at", "now")
                },
            )
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            friendly(e.message)
        }
    }

    // ---------------------------------------------------------- 工具

    private inline fun <T> runCatchingNull(block: () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private fun friendly(message: String?): String {
        val m = message.orEmpty()
        return when {
            m.contains("duplicate key", true) && m.contains("display_id", true) -> "这个 ID 已被别人用了，换一个吧"
            m.contains("duplicate key", true) -> "已存在相同记录"
            m.contains("violates not-null", true) -> "有必填字段没填"
            m.contains("permission denied", true) || m.contains("row-level security", true) -> "没有权限改这条数据"
            m.isBlank() -> "保存失败，稍后重试"
            else -> "保存失败：" + m.take(120)
        }
    }
}

/** 个人二维码链接前缀（token 形式） */
const val ProfileQrLinkPrefix = "https://xyzl.my/q/"

/** 个人链接前缀（用 user id 兜底） */
const val ProfileLinkPrefix = "https://xyzl.my/u/"

private val UUID_RE = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

private fun JsonObject.s(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }

private fun todayIso(): String = currentTimeParts().first
