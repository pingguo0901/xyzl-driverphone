package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** 登录设备（se_user_devices） */
data class DeviceRow(
    val id: String,
    val name: String,
    val osName: String?,
    val deviceType: String?,
    val lastLoginAt: String?,
    val isActive: Boolean,
)

/** 我的邀请码（se_invitation_codes） */
data class InviteInfo(
    val code: String,
    val shareLink: String?,
    val inviteType: String,
)

/** 邀请码类型（库里 invite_type 是枚举） */
private const val INVITE_PERSONAL = "personal_to_customer"
private const val INVITE_UNIVERSAL = "universal"
private const val INVITE_DRIVER_CUSTOMER = "company_to_customer"
private const val INVITE_COMPANY_DRIVER = "company_to_driver"
private const val INVITE_PERSONAL_FALLBACK = "platform_to_customer"
private const val INVITE_DRIVER = "company_to_customer"

/**
 * 登录设备 / 三方绑定 / 邀请码
 *
 * - 设备：se_user_devices where user_id = 我（is_active 分已登录 / 已退出）
 * - 三方绑定：se_user_oauth_bindings（provider = apple / facebook / wechat）
 * - 邀请码：个人 → 我的 account_id + personal_to_customer；司机 → 公司 account_id + company_to_customer
 */
class SecurityRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    // ---------------------------------------------------------- 登录设备

    /**
     * 登录 / 注册成功后把当前设备写进 se_user_devices。
     * 唯一索引是 partial index（WHERE deleted_at IS NULL），ON CONFLICT 带不了条件 → 先查后写。
     * 返回 null = 成功；失败只回文案，绝不阻断登录。
     */
    suspend fun recordCurrentDevice(userId: String): String? = try {
        val deviceId = DeviceInfo.deviceId()
        val existing = supabase.postgrest.from("se_user_devices")
            .select(Columns.list("id")) {
                filter {
                    eq("user_id", userId)
                    eq("device_id", deviceId)
                }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()?.s("id")

        if (existing != null) {
            supabase.postgrest.from("se_user_devices").update(
                buildJsonObject {
                    put("device_name", DeviceInfo.deviceName())
                    put("device_model", DeviceInfo.deviceModel())
                    put("device_type", DeviceInfo.deviceType())
                    put("os_name", DeviceInfo.osName())
                    put("os_version", DeviceInfo.osVersion())
                    put("app_version", DeviceInfo.appVersion())
                    put("last_login_at", "now")
                    put("is_active", true)
                    put("deleted_at", JsonNull)
                },
            ) { filter { eq("id", existing) } }
        } else {
            supabase.postgrest.from("se_user_devices").insert(
                buildJsonObject {
                    put("user_id", userId)
                    put("device_id", deviceId)
                    put("device_name", DeviceInfo.deviceName())
                    put("device_model", DeviceInfo.deviceModel())
                    put("device_type", DeviceInfo.deviceType())
                    put("os_name", DeviceInfo.osName())
                    put("os_version", DeviceInfo.osVersion())
                    put("app_version", DeviceInfo.appVersion())
                    put("last_login_at", "now")
                    put("is_active", true)
                },
            )
        }
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        "设备信息写入失败：" + (e.message ?: "未知错误").take(120)
    }

    suspend fun devices(userId: String): List<DeviceRow> {
        val rows = safe {
            supabase.postgrest.from("se_user_devices")
                .select(Columns.list("id", "device_name", "os_name", "device_type", "last_login_at", "is_active")) {
                    filter { eq("user_id", userId) }
                    order("last_login_at", Order.DESCENDING)
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        return rows.mapNotNull { r ->
            val id = r.s("id") ?: return@mapNotNull null
            DeviceRow(
                id = id,
                name = r.s("device_name") ?: "未知设备",
                osName = r.s("os_name"),
                deviceType = r.s("device_type"),
                lastLoginAt = r.s("last_login_at"),
                isActive = r.boolOf("is_active") ?: false,
            )
        }
    }

    suspend fun activeDevices(userId: String): List<DeviceRow> = devices(userId).filter { it.isActive }

    suspend fun loggedOutDevices(userId: String): List<DeviceRow> = devices(userId).filter { !it.isActive }

    /** 退出设备：is_active = false + logged_out_at = now() */
    suspend fun logoutDevice(deviceId: String): String? = try {
        supabase.postgrest.from("se_user_devices").update(
            buildJsonObject {
                put("is_active", false)
                put("logged_out_at", "now")
            },
        ) { filter { eq("id", deviceId) } }
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        if (e.message?.contains("row-level security", true) == true) "没有权限退出这台设备" else "退出失败，稍后重试"
    }

    /** 删除设备记录（软删） */
    suspend fun removeDevice(deviceId: String): String? = try {
        supabase.postgrest.from("se_user_devices").update(
            buildJsonObject { put("deleted_at", "now") },
        ) { filter { eq("id", deviceId) } }
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        "删除失败，稍后重试"
    }

    // ---------------------------------------------------------- 三方绑定

    /** 已绑定的 provider 集合（表不存在时返回空集合） */
    suspend fun boundProviders(userId: String): Set<String> {
        val rows = safe {
            supabase.postgrest.from("se_user_oauth_bindings")
                .select(Columns.list("provider")) {
                    filter { eq("user_id", userId) }
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        return rows.mapNotNull { it.s("provider") }.toSet()
    }

    /** 绑定一行（OAuth 接入后调用）：provider + provider_user_id + bound_at */
    suspend fun bindProvider(userId: String, provider: String, providerUserId: String): String? = try {
        supabase.postgrest.from("se_user_oauth_bindings").insert(
            buildJsonObject {
                put("user_id", userId)
                put("provider", provider)
                put("provider_user_id", providerUserId)
                put("bound_at", "now")
            },
        )
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        "绑定失败：" + (e.message ?: "未知错误").take(100)
    }

    // ---------------------------------------------------------- 邀请码

    /** 我的账户 id（个人账号 / 所属公司账号都在这张表） */
    suspend fun accountId(userId: String): String? = safe {
        supabase.postgrest.from("se_account_members")
            .select(Columns.list("account_id")) { filter { eq("user_id", userId) }; limit(1) }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.s("account_id")
    }

    /**
     * 邀请码列表（按角色）：
     * driver → 所属公司的 company_to_customer（1 条）
     * 其它（personal）→ 自己账户的 personal_to_customer + company_to_driver（2 条）
     */
    suspend fun myInvites(userId: String, role: String?): List<InviteInfo> {
        val accountId = accountId(userId) ?: return emptyList()
        // 新规则：一个账号只维护 1 条码（归属账号，与角色无关）。
        // 优先读 universal；老数据没有 universal 时退回任意一条（取最新）。
        val invite = byAccount(accountId, INVITE_UNIVERSAL) ?: anyByAccount(accountId)
        return listOfNotNull(invite)
    }

    /** 该账号最新的任意一条邀请码（兼容旧类型的码） */
    private suspend fun anyByAccount(accountId: String): InviteInfo? {
        val rows = safe {
            supabase.postgrest.from("se_invitation_codes")
                .select(Columns.list("invite_code", "share_link", "invite_type")) {
                    filter { eq("account_id", accountId) }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        val row = rows.firstOrNull() ?: return null
        val code = row.s("invite_code") ?: return null
        return InviteInfo(code, row.s("share_link"), row.s("invite_type") ?: INVITE_UNIVERSAL)
    }

    /**
     * （旧）单条邀请码：driver → 公司客户码；其它 → 个人客户码
     */
    suspend fun myInvite(userId: String, role: String?): InviteInfo? {
        val accountId = accountId(userId)
        return if (role == "driver") {
            byAccount(accountId, INVITE_DRIVER)
        } else {
            byAccount(accountId, INVITE_PERSONAL)
                ?: byAccount(accountId, INVITE_PERSONAL_FALLBACK)
                ?: byAccount(null, INVITE_PERSONAL_FALLBACK)
        }
    }

    private suspend fun byAccount(accountId: String?, inviteType: String): InviteInfo? {
        val rows = safe {
            supabase.postgrest.from("se_invitation_codes")
                .select(Columns.list("invite_code", "share_link", "invite_type")) {
                    filter {
                        if (accountId != null) eq("account_id", accountId)
                        eq("invite_type", inviteType)
                    }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        val row = rows.firstOrNull() ?: return null
        val code = row.s("invite_code") ?: return null
        return InviteInfo(
            code = code,
            shareLink = row.s("share_link"),
            inviteType = row.s("invite_type") ?: inviteType,
        )
    }

    // ---------------------------------------------------------- 工具

    private inline fun <T> safe(block: () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}

private fun JsonObject.s(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }

private fun JsonObject.boolOf(key: String): Boolean? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull()
