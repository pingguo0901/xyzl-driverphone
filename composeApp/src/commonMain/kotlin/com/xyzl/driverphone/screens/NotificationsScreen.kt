package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
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

private val NotifCard = Color(0xFF1C1C1E)
private val NotifChip = Color(0xFF27272A)
private val NotifGreen = Color(0xFF3B82F6)
private val NotifInk = Color(0xFFFFFFFF)
private val NotifMuted = Color(0xFFA1A1AA)

private data class NoticeItem(
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean,
    val official: Boolean,
)

private val notices = listOf(
    NoticeItem("新订单已派单", "T001 吉隆坡国际机场 → 武吉免登，请在 15 分钟内确认", "16:20", true, true),
    NoticeItem("薪资已到账", "本周薪资 RM 486.00 已到账，可在财务页查看流水", "10:22", true, true),
    NoticeItem("实名认证通过", "你的 KYC 认证已通过，可以正常接单", "昨天", false, true),
    NoticeItem("报销已受理", "加油报销 RM 120.00 已受理，3 个工作日内完成审核", "昨天", true, false),
    NoticeItem("服务分更新", "当前服务分 4.9，继续保持", "10-10", false, false),
)

/** 通知页：左上角返回 + 居中标题「通知」 + 胶囊式左右选项（全部/未读/已读/官方通知）+ 通知列表 */
@Composable
fun NotificationsScreen(onBack: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) }

    val filtered = notices.filter { n ->
        when (tab) {
            1 -> n.unread
            2 -> !n.unread
            3 -> n.official
            else -> true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NotifChip)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = NotifInk,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "通知",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = NotifInk,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(12.dp))
        SegmentedTabs(
            options = listOf("全部", "未读", "已读", "官方通知"),
            selectedIndex = tab,
            onSelect = { tab = it },
        )
        Spacer(Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            Text(
                "暂无通知",
                fontSize = 14.sp,
                color = NotifMuted,
                modifier = Modifier.padding(top = 32.dp),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
            ) {
                items(filtered, key = { it.title + it.time }) { item ->
                    NoticeRow(item)
                }
            }
        }
    }
}

@Composable
private fun NoticeRow(item: NoticeItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NotifCard)
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(if (item.unread) NotifGreen else Color.Transparent),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotifInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.official) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(NotifGreen.copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("官方", fontSize = 10.sp, color = NotifGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(item.body, fontSize = 13.sp, color = NotifMuted, lineHeight = 19.sp)
            Spacer(Modifier.height(6.dp))
            Text(item.time, fontSize = 12.sp, color = NotifMuted)
        }
    }
}
