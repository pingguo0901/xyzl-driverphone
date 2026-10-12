package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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

private val ChatCard = Color(0xFF1C1C1E)
private val ChatAvatar = Color(0xFF27272A)
private val ChatGreen = Color(0xFF3B82F6)
private val ChatInk = Color(0xFFFFFFFF)
private val ChatMuted = Color(0xFFA1A1AA)

/** 聊天页：胶囊式左右选项（聊天 / 官方通知）+ 聊天列表 */
@Composable
fun ChatScreen(
    onOpenNotifications: () -> Unit = {},
    onOpenChat: (String) -> Unit = {},
    onOpenOfficial: (String) -> Unit = {},
) {
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

        val list = if (tab == 0) ChatListStore.driver else ChatListStore.official
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            items(list, key = { it.name + it.time + it.message }) { item ->
                ChatRow(item, onClick = {
                    if (tab == 0) onOpenChat(item.name) else onOpenOfficial(item.name)
                })
            }
        }
    }
}

@Composable
private fun ChatRow(item: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ChatCard)
            .clickable { onClick() }
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
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChatInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.message,
                fontSize = 13.sp,
                color = ChatMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(item.time, fontSize = 12.sp, color = ChatMuted)
            if (item.unread > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                // 未读：胶囊式（原来是小圆点）
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 20.dp, minHeight = 18.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ChatGreen)
                        .padding(horizontal = 7.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (item.unread > 99) "99+" else item.unread.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
