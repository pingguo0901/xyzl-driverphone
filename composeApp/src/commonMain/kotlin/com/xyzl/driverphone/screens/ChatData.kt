package com.xyzl.driverphone.screens

import androidx.compose.runtime.mutableStateListOf

/** 消息状态：时钟=未发出（我方没网）；1绿勾=已发送未送达；2绿勾=已送达；2蓝勾=已读 */
enum class ChatTick { Sending, Sent, Delivered, Read }

sealed class ChatBody {
    data class Text(val text: String) : ChatBody()
    data class Album(val uris: List<String>) : ChatBody()
    data class File(val name: String) : ChatBody()
    data class Location(val name: String, val coords: String?) : ChatBody()
    data class Card(val title: String, val subtitle: String) : ChatBody()
    data class Voice(val path: String?, val seconds: Int) : ChatBody()
}

data class ChatMessage(
    val id: Long,
    val mine: Boolean,
    val senderName: String,
    val body: ChatBody,
    val time: String,
    val date: String,
    val tick: ChatTick,
)

/** 联系人类型：司机 / 客户 / 公司员工 / 公司 */
enum class PersonKind(val label: String) {
    Driver("司机"),
    Customer("客户"),
    Staff("公司员工"),
    Company("公司"),
}

data class ChatPerson(
    val name: String,
    val kind: PersonKind,
    val id: String,
    val company: String? = null,
    val rating: String? = null,
    val goodRate: String? = null,
    val recentReviews: List<String> = emptyList(),
)

val chatPeople = listOf(
    ChatPerson("调度 · 陈师傅", PersonKind.Staff, "XY-S-0231", "星域臻旅 · 调度中心"),
    ChatPerson("张先生", PersonKind.Customer, "XY-C-10245"),
    ChatPerson("李女士", PersonKind.Customer, "XY-C-10246"),
    ChatPerson("林女士", PersonKind.Customer, "XY-C-10247"),
    ChatPerson("客服小星", PersonKind.Staff, "XY-S-0102", "星域臻旅 · 客服中心"),
    ChatPerson("王先生", PersonKind.Customer, "XY-C-10248"),
    ChatPerson("星域臻旅", PersonKind.Company, "XY-0001", "星域臻旅", "4.9", "96%"),
    ChatPerson("我自己", PersonKind.Driver, "XY-D-008812", "星域臻旅", "4.9", "96%"),
)

fun personOf(name: String): ChatPerson =
    chatPeople.firstOrNull { it.name == name } ?: ChatPerson(name, PersonKind.Customer, "XY-C-00000")

/** 最近评价该司机的 3 条记录（客户详情页用） */
val myRecentReviews = listOf(
    "2026-10-11 · 陈太太 ★★★★★ 司机很准时，车也干净",
    "2026-10-09 · Mr. Tan ★★★★★ 帮忙搬行李，态度好",
    "2026-10-05 · 李小姐 ★★★★ 路线熟悉，等待略久",
)

/** 聊天消息仓库（内存态，够真实交互用） */
object ChatStore {
    val messages = mutableStateListOf<ChatMessage>()

    private var seq = 0L

    fun now(): Pair<String, String> {
        val t = currentTimeParts()
        return t.first to t.second
    }

    fun add(
        mine: Boolean,
        senderName: String,
        body: ChatBody,
        tick: ChatTick = if (mine) ChatTick.Delivered else ChatTick.Read,
    ): ChatMessage {
        val (date, time) = now()
        seq += 1
        val msg = ChatMessage(seq, mine, senderName, body, time, date, tick)
        messages.add(msg)
        return msg
    }

    fun clearAll() {
        messages.clear()
    }

    init {
        add(false, "张先生", ChatBody.Text("好的，我 5 分钟后到楼下"), ChatTick.Read)
        add(true, "我", ChatBody.Text("我在 3 号门等你"), ChatTick.Read)
        add(false, "张先生", ChatBody.Voice(null, 4), ChatTick.Read)
        add(true, "我", ChatBody.Location("吉隆坡国际机场 3 号门", "2.7456,101.7099"), ChatTick.Delivered)
        add(true, "我", ChatBody.Text("行李我帮你放后备箱"), ChatTick.Sent)
    }
}

/** 聊天列表项 */
data class ChatItem(
    val name: String,
    val message: String,
    val time: String,
    val unread: Int,
)

/** 聊天列表 + 实时未读数（铃铛胶囊与聊天卡片共用） */
object ChatListStore {
    val driver = mutableStateListOf(
        ChatItem("调度 · 陈师傅", "明早 8 点机场接机单已派给你，记得确认", "14:32", 2),
        ChatItem("张先生", "好的，我 5 分钟后到楼下", "14:20", 0),
        ChatItem("李女士", "行李有点多，麻烦后备箱留个位置", "13:58", 1),
        ChatItem("林女士", "师傅，发票可以开吗？", "12:31", 0),
        ChatItem("客服小星", "您的证件审核已经通过了 ✅", "11:05", 0),
        ChatItem("王先生", "我在 3 号门等你", "09:47", 0),
    )

    val official = mutableStateListOf(
        ChatItem("星域臻旅", "【政策】司机服务规范 V2.1 已更新，请查阅", "16:10", 1),
        ChatItem("星域臻旅", "【结算】上周薪资已打入你的账户", "10:22", 0),
        ChatItem("星域臻旅", "【活动】本月接单满 100 单奖励 RM200", "昨天", 0),
    )

    fun totalUnread(): Int = driver.sumOf { it.unread } + official.sumOf { it.unread }

    fun unreadIn(items: List<ChatItem>): Int = items.sumOf { it.unread }

    fun clearUnread(name: String) {
        for (i in driver.indices) {
            if (driver[i].name == name) driver[i] = driver[i].copy(unread = 0)
        }
        for (i in official.indices) {
            if (official[i].name == name) official[i] = official[i].copy(unread = 0)
        }
    }

    fun bumpUnread(name: String) {
        for (i in driver.indices) {
            if (driver[i].name == name) driver[i] = driver[i].copy(unread = driver[i].unread + 1)
        }
        for (i in official.indices) {
            if (official[i].name == name) official[i] = official[i].copy(unread = official[i].unread + 1)
        }
    }
}

/** 平台相关：当前日期与时间（yyyy-MM-dd / HH:mm） */
expect fun currentTimeParts(): Pair<String, String>
