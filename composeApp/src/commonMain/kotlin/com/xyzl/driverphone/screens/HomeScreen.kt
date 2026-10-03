package com.xyzl.driverphone.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RestingGray = Color(0xFFE9E9EB)
private val OnlineGreen = Color(0xFF34C759)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)

@Composable
fun HomeScreen() {
    var online by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            "首页",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        OnlineStatusCard(online = online, onToggle = { online = !online })

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("今日订单", "12", Modifier.weight(1f))
            StatCard("今日收入", "RM 486", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("在线时长", "6.5h", Modifier.weight(1f))
            StatCard("评分", "4.9", Modifier.weight(1f))
        }
    }
}

/** 接单开关卡片：休息中柔和灰 → 接单中高级绿从左到右渐变整卡填充 */
@Composable
private fun OnlineStatusCard(
    online: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition by animateFloatAsState(
        targetValue = if (online) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "onlineTransition",
    )

    val titleColor = if (online) Color.White else InkDark
    val subColor = if (online) Color.White.copy(alpha = 0.85f) else MutedGray
    val iconTint = if (online) OnlineGreen else InkDark

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(92.dp)) {
            Box(Modifier.fillMaxSize().background(RestingGray))
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(transition)
                    .background(OnlineGreen),
            )

            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (online) "接单中" else "休息中",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (online) "正在接收新订单" else "点击右侧按钮开始接单",
                        fontSize = 13.sp,
                        color = subColor,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PowerSettingsNew,
                        contentDescription = if (online) "停止接单" else "开始接单",
                        tint = iconTint,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MutedGray,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
            )
        }
    }
}
