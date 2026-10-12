package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HeaderInk = Color(0xFFFFFFFF)
private val HeaderChip = Color(0xFF27272A)
private val HeaderBadge = Color(0xFF3B82F6)

/** 通用页面标题栏：标题居中，右上角通知铃铛（可插入额外按钮） */
@Composable
fun ScreenHeader(
    title: String,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    extraEnd: @Composable () -> Unit = {},
) {
    Box(modifier.fillMaxWidth().height(44.dp)) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = HeaderInk,
            modifier = Modifier.align(Alignment.Center),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            extraEnd()
            // 铃铛 + 右上角未读胶囊（实时未读数）
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HeaderChip)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "通知",
                        tint = HeaderInk,
                        modifier = Modifier.size(20.dp),
                    )
                }
                val unread = ChatListStore.totalUnread()
                if (unread > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                            .defaultMinSize(minWidth = 18.dp, minHeight = 16.dp)
                            .clip(RoundedCornerShape(50))
                            .background(HeaderBadge)
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (unread > 99) "99+" else unread.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
