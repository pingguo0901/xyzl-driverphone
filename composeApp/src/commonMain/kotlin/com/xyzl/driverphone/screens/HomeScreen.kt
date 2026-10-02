package com.xyzl.driverphone.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun HomeScreen() {
    var online by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("首页", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // 接单开关卡片
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (online) "接单中" else "休息中",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        if (online) "正在接收新订单" else "点击右侧按钮开始接单",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                CircularToggleButton(
                    online = online,
                    onToggle = { online = !online },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            StatCard("今日订单", "12", Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            StatCard("今日收入", "RM 486", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            StatCard("在线时长", "6.5h", Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            StatCard("评分", "4.9", Modifier.weight(1f))
        }
    }
}

/**
 * 圆形接单开关按钮：
 * 休息中为灰色，接单中时绿色从左到右渐变填充，
 * 切换回休息中时按原路反向退回。
 */
@Composable
private fun CircularToggleButton(
    online: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition by animateFloatAsState(
        targetValue = if (online) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "onlineTransition",
    )

    val green = Color(0xFF4CAF50)
    val gray = Color(0xFFBDBDBD)

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .clickable(onClick = onToggle),
    ) {
        // 灰色底色（休息中）
        Box(Modifier.fillMaxSize().background(gray))
        // 绿色从左到右渐变填充（接单中）
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(transition)
                .background(green),
        )
        // 中心电源图标
        Icon(
            imageVector = Icons.Filled.PowerSettingsNew,
            contentDescription = if (online) "停止接单" else "开始接单",
            tint = Color.White,
            modifier = Modifier.align(Alignment.Center).size(26.dp),
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}
