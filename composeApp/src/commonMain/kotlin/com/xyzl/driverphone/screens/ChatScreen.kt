package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChatCard = Color(0xFF2A3833)
private val ChatAvatar = Color(0xFF31403A)
private val ChatGreen = Color(0xFF206A4E)
private val ChatInk = Color(0xFFE8EDEA)
private val ChatMuted = Color(0xFF9DB0A8)

private data class ChatItem(
    val name: String,
    val message: String,
    val time: String,
    val unread: Int,
)

private val driverChats = listOf(
    ChatItem("调度 · 陈师傅", "明早 8 点机场接机单已派给你，记得确认", "14:32", 2),
    ChatItem("张先生", "好的，我 5 分钟后到楼下", "14:20", 0),
    ChatItem("李女士", "行李有点多，麻烦后备箱留个位置", "13:58", 1),
    ChatItem("林女士", "师傅，发票可以开吗？", "12:31", 0),
    ChatItem("客服小星", "您的证件审核已经通过了 ✅", "11:05", 0),
    ChatItem("王先生", "我在 3 号门等你", "09:47", 0),
)

private val officialChats = listOf(
    ChatItem("星域臻旅", "【政策】司机服务规范 V2.1 已更新，请查阅", "16:10", 1),
    ChatItem("星域臻旅", "【结算】上周薪资已打入你的账户", "10:22", 0),
    ChatItem("星域臻旅", "【活动】本月接单满 100 单奖励 RM200", "昨天", 0),
)

/** 聊天页：胶囊式左右选项（聊天 / 官方通知）+ 聊天列表 */
@Composable
fun ChatScreen(onOpenNotifications: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        ScreenHeader(title = "聊天", onOpenNotifications = onOpenNotifications)
        Spacer(Modifier.height(12.dp))
        SegmentedTabs(
            options = listOf("聊天", "官方通知"),
            selectedIndex = tab,
            onSelect = { tab = it },
        )
        Spacer(Modifier.height(14.dp))

        val list = if (tab == 0) driverChats else officialChats
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            items(list, key = { it.name + it.time + it.message }) { item ->
                ChatRow(item)
            }
        }
    }
}

@Composable
private fun ChatRow(item: ChatItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ChatCard)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(ChatAvatar),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                item.name.take(1),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChatInk,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChatInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                item.message,
                fontSize = 13.sp,
                color = ChatMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(item.time, fontSize = 12.sp, color = ChatMuted)
            if (item.unread > 0) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier.size(18.dp).clip(CircleShape).background(ChatGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        item.unread.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
