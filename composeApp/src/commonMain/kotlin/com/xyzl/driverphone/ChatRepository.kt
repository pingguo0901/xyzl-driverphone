package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/**
 * 聊天窗（《聊天窗页面接线表》）
 *
 * 会话：se_chat_threads（participants jsonb 里找对方 user_id，thread_type 决定用哪张消息表）
 *   business → se_business_chat_messages / support → se_support_chat_messages / direct → se_direct_chat_messages
 * 名字头像：se_users.full_name / avatar_url
 * 消息字段：content / created_at / delivery_status / voice_url+voice_duration_sec / lat+lng / attachment_url / file_name+file_size / contact_card_user_id
 * 发送：往对应消息表 INSERT 一行
 */

/** 一条会话（含对方信息与要读的消息表） */
data class ChatThreadInfo(
    val id: String,
    val threadType: String,
    val table: String,
    val peerUserId: String?,
    val peerName: String,
    val peerAvatar: String?,
)

/** 一条消息 */
data class ChatLine(
    val id: String,
    val mine: Boolean,
    val type: String,
    val content: String?,
    val attachmentUrl: String?,
    val voiceUrl: String?,
    val voiceSeconds: Int?,
    val lat: Double?,
    val lng: Double?,
    val fileName: String?,
    val fileSize: Long?,
    val contactCardUserId: String?,
    val deliveryStatus: String?,
    val createdAt: String?,
)

class ChatRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    /** 打开与某个人的会话：先按对方名字找会话，再拉消息 */
    suspend fun open(userId: String, peerName: String): Pair<ChatThreadInfo, List<ChatLine>>? {
        val threads = try {
            supabase.postgrest.from("se_chat_threads")
                .select(Columns.list("id", "thread_type", "participants", "last_message_at")) {
                    order("last_message_at", Order.DESCENDING)
                    limit(200)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }

        val mine = threads.filter { it.participantIds().contains(userId) }
        if (mine.isEmpty()) return null

        val peerIds = mine.flatMap { it.participantIds() }.filter { it != userId }.distinct()
        val users: Map<String, JsonObject> = if (peerIds.isEmpty()) {
            emptyMap()
        } else {
            try {
                supabase.postgrest.from("se_users")
                    .select(Columns.list("id", "full_name", "avatar_url")) {
                        filter { isIn("id", peerIds) }
                    }
                    .decodeList<JsonObject>()
                    .mapNotNull { row ->
                        val id = row.str("id") ?: return@mapNotNull null
                        id to row
                    }
                    .toMap()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyMap()
            }
        }

        val chosen = mine.firstOrNull { row ->
            val peer = row.participantIds().firstOrNull { it != userId }
            peer != null && users[peer]?.str("full_name") == peerName
        } ?: mine.firstOrNull()

        val chosenRow = chosen ?: return null
        val peerId = chosenRow.participantIds().firstOrNull { it != userId }
        val info = ChatThreadInfo(
            id = chosenRow.str("id").orEmpty(),
            threadType = chosenRow.str("thread_type").orEmpty(),
            table = tableFor(chosenRow.str("thread_type")),
            peerUserId = peerId,
            peerName = peerId?.let { users[it]?.str("full_name") } ?: peerName,
            peerAvatar = peerId?.let { users[it]?.str("avatar_url") },
        )
        return info to messages(info, userId)
    }

    suspend fun messages(info: ChatThreadInfo, userId: String): List<ChatLine> = try {
        supabase.postgrest.from(info.table)
            .select(columnsFor(info.table)) {
                filter { eq("thread_id", info.id) }
                order("created_at", Order.ASCENDING)
                limit(300)
            }
            .decodeList<JsonObject>()
            .mapNotNull { row -> toLine(row, userId) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        emptyList()
    }

    // ------------------------------------------------------------ 发送
    suspend fun sendText(info: ChatThreadInfo, userId: String, text: String): Boolean = insert(
        info,
        baseValues(info, userId, "text") { put("content", text) },
    )

    suspend fun sendLocation(info: ChatThreadInfo, userId: String, lat: Double, lng: Double, label: String): Boolean = insert(
        info,
        baseValues(info, userId, "location") {
            put("content", label)
            put("lat", lat)
            put("lng", lng)
        },
    )

    /** 对方发来的消息标记已读 */
    suspend fun markThreadRead(info: ChatThreadInfo): Boolean = try {
        supabase.postgrest.from(info.table)
            .update(buildJsonObject { put("is_read", true); put("read_at", "now") }) {
                filter {
                    eq("thread_id", info.id)
                    eq("is_read", false)
                }
            }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    // ------------------------------------------------------------ 内部
    private fun baseValues(
        info: ChatThreadInfo,
        userId: String,
        type: String,
        extra: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit,
    ): JsonObject = buildJsonObject {
        put("thread_id", info.id)
        put("sender_user_id", userId)
        put("message_type", type)
        put("delivery_status", "sent")
        put("is_read", false)
        extra()
    }

    private suspend fun insert(info: ChatThreadInfo, values: JsonObject): Boolean = try {
        supabase.postgrest.from(info.table).insert(values)
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    private fun toLine(row: JsonObject, userId: String): ChatLine? {
        val id = row.str("id") ?: return null
        return ChatLine(
            id = id,
            mine = row.str("sender_user_id") == userId,
            type = row.str("message_type") ?: "text",
            content = row.str("content"),
            attachmentUrl = row.str("attachment_url"),
            voiceUrl = row.str("voice_url"),
            voiceSeconds = row.intOrNull("voice_duration_sec"),
            lat = row.dblOrNull("lat"),
            lng = row.dblOrNull("lng"),
            fileName = row.str("file_name"),
            fileSize = row.longOrNull("file_size"),
            contactCardUserId = row.str("contact_card_user_id"),
            deliveryStatus = row.str("delivery_status"),
            createdAt = row.str("created_at"),
        )
    }
}

/** 会话类型 → 消息表 */
private fun tableFor(threadType: String?): String = when (threadType) {
    "business" -> "se_business_chat_messages"
    "support" -> "se_support_chat_messages"
    else -> "se_direct_chat_messages"
}

/** 各消息表的列不完全一样：support 表没有 lat / lng */
private fun columnsFor(table: String): Columns = if (table == "se_support_chat_messages") {
    Columns.list(
        "id", "sender_user_id", "message_type", "content", "attachment_url",
        "voice_url", "voice_duration_sec", "file_name", "file_size",
        "contact_card_user_id", "delivery_status", "created_at",
    )
} else {
    Columns.list(
        "id", "sender_user_id", "message_type", "content", "attachment_url",
        "voice_url", "voice_duration_sec", "file_name", "file_size",
        "contact_card_user_id", "delivery_status", "lat", "lng", "created_at",
    )
}

private fun JsonObject.participantIds(): List<String> {
    val element = this["participants"] ?: return emptyList()
    val array = element as? JsonArray ?: return emptyList()
    return array.mapNotNull { item ->
        when (item) {
            is JsonPrimitive -> item.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }
            is JsonObject -> item.str("user_id") ?: item.str("id")
            else -> null
        }
    }
}

private fun JsonObject.intOrNull(key: String): Int? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()?.toInt()

private fun JsonObject.dblOrNull(key: String): Double? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()

private fun JsonObject.longOrNull(key: String): Long? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()?.toLong()

/** 时间戳（UTC ISO）→ 马来西亚 UTC+8 的 [年,月,日,时,分] */
private fun shiftedParts(iso: String?): IntArray? {
    if (iso == null || iso.length < 16) return null
    val year = iso.substring(0, 4).toIntOrNull() ?: return null
    val month = iso.substring(5, 7).toIntOrNull() ?: return null
    val day = iso.substring(8, 10).toIntOrNull() ?: return null
    val hour = iso.substring(11, 13).toIntOrNull() ?: return null
    val minute = iso.substring(14, 16).toIntOrNull() ?: return null

    val shifted = hour * 60 + minute + 8 * 60
    val carryDays = shifted / (24 * 60)
    val localMinutes = shifted % (24 * 60)

    var y = year
    var m = month
    var d = day + carryDays
    val dim = daysInMonth(y, m)
    if (d > dim) {
        d -= dim
        m += 1
        if (m > 12) {
            m = 1
            y += 1
        }
    }
    return intArrayOf(y, m, d, localMinutes / 60, localMinutes % 60)
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 31
}

private fun two(value: Int): String = if (value < 10) "0" + value else value.toString()

/** "22:30" */
fun formatClock(iso: String?): String {
    val p = shiftedParts(iso) ?: return ""
    return two(p[3]) + ":" + two(p[4])
}

/** "2026-10-15" */
fun formatDate(iso: String?): String {
    val p = shiftedParts(iso) ?: return ""
    return p[0].toString() + "-" + two(p[1]) + "-" + two(p[2])
}
