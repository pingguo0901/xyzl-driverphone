package com.xyzl.driverphone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * 铃铛红点：全局未读通知数（首页/行程/聊天/财务/我的 标题栏共用）
 * 绑定当前登录用户后 refresh()，标记已读或清空后再 refresh()
 */
object NotificationBadgeStore {
    private var boundUserId: String? = null

    var count by mutableStateOf(0)
        private set

    fun bind(userId: String?) {
        boundUserId = userId
    }

    suspend fun refresh() {
        val id = boundUserId
        if (id == null) {
            count = 0
            return
        }
        count = try {
            NotificationRepository().unreadCount(id)
        } catch (e: Exception) {
            count
        }
    }
}

/**
 * 通知（《通知页面接线表》）
 *
 * Tab：全部 / 未读(is_read=false) / 已读(is_read=true) / 官方(type='official')
 * 列表：where user_id = 我 order by created_at desc
 * 点开：is_read = true, read_at = now()
 * type 值：system / official / order / payment
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    /** system / official / order / payment */
    val type: String?,
    val isRead: Boolean,
    val createdAt: String?,
) {
    val official: Boolean get() = type == "official"
    val unread: Boolean get() = !isRead
}

class NotificationRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    suspend fun list(userId: String): List<NotificationItem> {
        val rows = try {
            supabase.postgrest.from("se_notifications")
                .select(Columns.list("id", "title", "body", "type", "is_read", "created_at")) {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                    limit(200)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows.mapNotNull { row ->
            val id = row.str("id") ?: return@mapNotNull null
            NotificationItem(
                id = id,
                title = row.str("title").orEmpty(),
                body = row.str("body").orEmpty(),
                type = row.str("type"),
                isRead = row.bool("is_read"),
                createdAt = row.str("created_at"),
            )
        }
    }

    suspend fun unreadCount(userId: String): Int = list(userId).count { it.unread }

    /** 点开一条 → 已读 + read_at */
    suspend fun markRead(id: String): Boolean = runUpdate {
        supabase.postgrest.from("se_notifications")
            .update(readValues()) { filter { eq("id", id) } }
    }

    /** 全部标记已读 */
    suspend fun markAllRead(userId: String): Boolean = runUpdate {
        supabase.postgrest.from("se_notifications")
            .update(readValues()) { filter { eq("user_id", userId) } }
    }

    /** 删除（软删：只写 deleted_at，不真删数据） */
    suspend fun delete(ids: List<String>): Boolean {
        if (ids.isEmpty()) return true
        return runUpdate {
            supabase.postgrest.from("se_notifications")
                .update(buildJsonObject { put("deleted_at", "now") }) { filter { isIn("id", ids) } }
        }
    }

    /** 清空（软删我的全部通知） */
    suspend fun clear(userId: String): Boolean = runUpdate {
        supabase.postgrest.from("se_notifications")
            .update(buildJsonObject { put("deleted_at", "now") }) { filter { eq("user_id", userId) } }
    }

    private fun readValues(): JsonObject = buildJsonObject {
        put("is_read", true)
        // 'now' 是 PostgreSQL 认的 timestamptz 字面量
        put("read_at", "now")
    }

    private suspend fun runUpdate(block: suspend () -> Unit): Boolean = try {
        block()
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }
}
