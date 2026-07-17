package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

data class ChatItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0,
    val avatarBg: Color = DriverColors.Surface
)

@Composable
fun ChatScreen() {
    val chats = listOf(
        ChatItem("1", "系统通知", "行程派单：已为您分配新行程 #20260717-001", "20:45", 1, Color(0xFF1a3a1a)),
        ChatItem("2", "客服中心", "您好，请上传今日收款凭证", "18:30", 0, Color(0xFF1a2744)),
        ChatItem("3", "车队群组", "张三：明天早班谁来？", "17:15", 3, Color(0xFF2d1a44)),
        ChatItem("4", "财务通知", "本月结算报表已生成，请查收", "14:20", 1, Color(0xFF443a1a)),
        ChatItem("5", "审核中心", "您的车辆信息审核已通过 ✅", "昨天", 0, Color(0xFF1a443a)),
        ChatItem("6", "王磊（队友）", "好的，明天见！", "昨天", 0, Color(0xFF3a1a3a)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverColors.Background)
    ) {
        Text(
            "聊天",
            color = DriverColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            items(chats) { chat ->
                ChatRow(chat)
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DriverColors.Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(chat.avatarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                chat.name.take(1),
                color = DriverColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    chat.name,
                    color = DriverColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    chat.time,
                    color = DriverColors.TextDisabled,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    chat.lastMessage,
                    color = DriverColors.TextMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unread > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(if (chat.unread > 9) 22.dp else 20.dp)
                            .clip(CircleShape)
                            .background(DriverColors.Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (chat.unread > 99) "99+" else chat.unread.toString(),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
