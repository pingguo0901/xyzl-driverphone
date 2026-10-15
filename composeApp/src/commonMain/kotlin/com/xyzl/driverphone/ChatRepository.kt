package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
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
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    /** 附件上传用的 Storage 桶 */
    val bucket: String = "se_chat-direct",
)

/** 聊天 Tab 的一行（《聊天 Tab（私聊列表）接线表》） */
data class ChatListItem(
    val threadId: String,
    val threadType: String,
    val peerUserId: String?,
    val name: String,
    val avatarUrl: String?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unread: Int,
    val isPinned: Boolean,
) {
    val official: Boolean get() = threadType == "official"
}

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

    /**
     * 聊天 Tab 列表：我在参与的会话（business / support / direct / official）
     * 排序：is_pinned desc, last_message_at desc；未读=该会话里别人发的 is_read=false 条数
     */
    suspend fun list(userId: String): List<ChatListItem> {
        val rows = try {
            supabase.postgrest.from("se_chat_threads")
                .select(
                    Columns.list(
                        "id", "thread_type", "participants", "account_id",
                        "last_message", "last_message_at", "is_pinned",
                    ),
                ) {
                    order("is_pinned", Order.DESCENDING)
                    order("last_message_at", Order.DESCENDING)
                    limit(100)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }

        val mine = rows.filter { it.participantIds().contains(userId) }
        if (mine.isEmpty()) return emptyList()

        val peerIds = mine
            .filter { it.str("thread_type") != "official" }
            .flatMap { it.participantIds() }
            .filter { it != userId }
            .distinct()
        val users: Map<String, JsonObject> = if (peerIds.isEmpty()) emptyMap() else try {
            supabase.postgrest.from("se_users")
                .select(Columns.list("id", "full_name", "avatar_url")) { filter { isIn("id", peerIds) } }
                .decodeList<JsonObject>()
                .mapNotNull { row -> row.str("id")?.let { it to row } }
                .toMap()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyMap()
        }

        val accountIds = mine.mapNotNull { it.str("account_id") }.distinct()
        val accounts: Map<String, JsonObject> = if (accountIds.isEmpty()) emptyMap() else try {
            supabase.postgrest.from("se_accounts")
                .select(Columns.list("id", "display_name")) { filter { isIn("id", accountIds) } }
                .decodeList<JsonObject>()
                .mapNotNull { row -> row.str("id")?.let { it to row } }
                .toMap()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyMap()
        }

        // 未读：三张消息表各查一次（is_read=false 且不是我发的）
        val idsByTable = mine.groupBy { tableFor(it.str("thread_type")) }
        val unread = HashMap<String, Int>()
        for ((table, group) in idsByTable) {
            val ids = group.mapNotNull { it.str("id") }
            unread.putAll(unreadByThread(table, ids, userId))
        }

        return mine.mapNotNull { row ->
            val id = row.str("id") ?: return@mapNotNull null
            val type = row.str("thread_type").orEmpty()
            val peerId = row.participantIds().firstOrNull { it != userId }
            val official = type == "official"
            ChatListItem(
                threadId = id,
                threadType = type,
                peerUserId = peerId,
                name = if (official) {
                    accounts[row.str("account_id").orEmpty()]?.str("display_name") ?: "官方通知"
                } else {
                    users[peerId]?.str("full_name") ?: "用户"
                },
                avatarUrl = if (official) null else peerId?.let { users[it]?.str("avatar_url") },
                lastMessage = row.str("last_message"),
                lastMessageAt = row.str("last_message_at"),
                unread = unread[id] ?: 0,
                isPinned = row.bool("is_pinned"),
            )
        }
    }

    private suspend fun unreadByThread(table: String, threadIds: List<String>, userId: String): Map<String, Int> {
        if (threadIds.isEmpty()) return emptyMap()
        return try {
            supabase.postgrest.from(table)
                .select(Columns.list("thread_id", "sender_user_id")) {
                    filter {
                        isIn("thread_id", threadIds)
                        eq("is_read", false)
                    }
                    limit(2000)
                }
                .decodeList<JsonObject>()
                .filter { it.str("sender_user_id") != userId }
                .groupingBy { it.str("thread_id").orEmpty() }
                .eachCount()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /** 列表点进来：直接用 thread_id 开会话 */
    suspend fun openById(threadId: String, userId: String): Pair<ChatThreadInfo, List<ChatLine>>? {
        val row = try {
            supabase.postgrest.from("se_chat_threads")
                .select(Columns.list("id", "thread_type", "participants", "account_id", "is_muted", "is_pinned")) {
                    filter { eq("id", threadId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: return null

        val type = row.str("thread_type").orEmpty()
        val peerId = row.participantIds().firstOrNull { it != userId }
        val peer = if (peerId == null) null else try {
            supabase.postgrest.from("se_users")
                .select(Columns.list("id", "full_name", "avatar_url")) { filter { eq("id", peerId) } }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        val accountName = if (type == "official") {
            row.str("account_id")?.let { accountId ->
                try {
                    supabase.postgrest.from("se_accounts")
                        .select(Columns.list("display_name")) { filter { eq("id", accountId) } }
                        .decodeList<JsonObject>()
                        .firstOrNull()
                        ?.str("display_name")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            }
        } else {
            null
        }

        val info = ChatThreadInfo(
            id = row.str("id").orEmpty(),
            threadType = type,
            table = tableFor(type),
            peerUserId = peerId,
            peerName = accountName ?: peer?.str("full_name") ?: "对话",
            peerAvatar = peer?.str("avatar_url"),
            isMuted = row.bool("is_muted"),
            isPinned = row.bool("is_pinned"),
            bucket = bucketFor(type),
        )
        return info to messages(info, userId)
    }

    /** 打开与某个人的会话：先按对方名字找会话，再拉消息 */
    suspend fun open(userId: String, peerName: String): Pair<ChatThreadInfo, List<ChatLine>>? {
        val threads = try {
            supabase.postgrest.from("se_chat_threads")
                .select(Columns.list("id", "thread_type", "participants", "last_message_at", "is_muted", "is_pinned")) {
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
            bucket = bucketFor(chosenRow.str("thread_type")),
            peerUserId = peerId,
            peerName = peerId?.let { users[it]?.str("full_name") } ?: peerName,
            peerAvatar = peerId?.let { users[it]?.str("avatar_url") },
            isMuted = chosenRow.bool("is_muted"),
            isPinned = chosenRow.bool("is_pinned"),
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

    // ------------------------------------------------------------ 发送（附件先上 Storage，再把路径写进消息表）
    suspend fun sendImage(info: ChatThreadInfo, userId: String, uri: String): Boolean {
        val path = upload(info, uri) ?: return false
        return insert(
            info,
            baseValues(info, userId, "image") { put("attachment_url", path) },
        )
    }

    suspend fun sendFile(info: ChatThreadInfo, userId: String, uri: String, name: String): Boolean {
        val bytes = readFileBytes(uri) ?: return false
        val path = uploadBytes(info, bytes, extOf(uri)) ?: return false
        return insert(
            info,
            baseValues(info, userId, "file") {
                put("attachment_url", path)
                put("file_name", name)
                put("file_size", bytes.size.toLong())
            },
        )
    }

    suspend fun sendVoice(info: ChatThreadInfo, userId: String, uri: String, seconds: Int): Boolean {
        val path = upload(info, uri) ?: return false
        return insert(
            info,
            baseValues(info, userId, "voice") {
                put("voice_url", path)
                put("voice_duration_sec", seconds)
            },
        )
    }

    suspend fun sendContactCard(info: ChatThreadInfo, userId: String, contactUserId: String?, title: String): Boolean = insert(
        info,
        baseValues(info, userId, "contact_card") {
            if (contactUserId != null) put("contact_card_user_id", contactUserId)
            put("content", title)
        },
    )

    /** 通话：本轮只记一条 missed（真通话以后接 RTC） */
    suspend fun logMissedCall(userId: String, calleeUserId: String?, video: Boolean): Boolean = try {
        supabase.postgrest.from("se_call_records").insert(
            buildJsonObject {
                put("caller_user_id", userId)
                if (calleeUserId != null) put("callee_user_id", calleeUserId)
                put("call_type", if (video) "video" else "voice")
                put("status", "missed")
                put("started_at", "now")
            },
        )
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    private suspend fun upload(info: ChatThreadInfo, uri: String): String? {
        val bytes = readFileBytes(uri) ?: return null
        return uploadBytes(info, bytes, extOf(uri))
    }

    private suspend fun uploadBytes(info: ChatThreadInfo, bytes: ByteArray, ext: String): String? = try {
        val path = info.threadType + "/" + info.id + "/" + uniqueStamp() + "." + ext
        supabase.storage.from(info.bucket).upload(path, bytes)
        path
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
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

    /** 消息免打扰：is_muted（顺带写 muted_until） */
    suspend fun setMuted(threadId: String, muted: Boolean): Boolean = updateThread(threadId) {
        put("is_muted", muted)
        if (muted) put("muted_until", "now") else put("muted_until", null as String?)
    }

    /** 置顶聊天：is_pinned + pinned_at */
    suspend fun setPinned(threadId: String, pinned: Boolean): Boolean = updateThread(threadId) {
        put("is_pinned", pinned)
        if (pinned) put("pinned_at", "now") else put("pinned_at", null as String?)
    }

    /** 清空当前聊天记录：只对该用户隐藏（user_cleared_at = now()），不删服务器数据 */
    suspend fun clearForUser(threadId: String): Boolean = updateThread(threadId) {
        put("user_cleared_at", "now")
    }

    private suspend fun updateThread(
        threadId: String,
        values: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit,
    ): Boolean = try {
        supabase.postgrest.from("se_chat_threads")
            .update(buildJsonObject(values)) { filter { eq("id", threadId) } }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

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

/** 会话类型 → Storage 桶 */
private fun bucketFor(threadType: String?): String = when (threadType) {
    "business" -> "se_chat-business"
    "support" -> "se_chat-support"
    else -> "se_chat-direct"
}

private fun extOf(uri: String): String {
    val name = uri.substringAfterLast('/')
    val ext = name.substringAfterLast('.', "")
    return if (ext.length in 2..5) ext.lowercase() else "bin"
}

private fun uniqueStamp(): String = "m" + kotlin.random.Random.nextLong().toString(16).removePrefix("-")

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
