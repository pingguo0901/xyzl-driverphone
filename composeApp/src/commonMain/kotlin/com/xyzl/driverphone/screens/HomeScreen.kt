package com.xyzl.driverphone.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
private val WhatsAppGreen = Color(0xFF25D366)
private val WeChatGreen = Color(0xFF07C160)

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

        Text(
            "当前任务",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        CurrentTaskCard()
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

/** 当前任务卡片：日期时间 + 起终点 + 联系方式 + WhatsApp/WeChat + 接人/放人滑块 */
@Composable
private fun CurrentTaskCard() {
    var stage by remember { mutableStateOf(false) } // false=接人 true=放人

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "今天 · 14:30",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MutedGray,
            )
            Spacer(Modifier.height(16.dp))

            InfoRow("起点", "吉隆坡国际机场")
            Spacer(Modifier.height(12.dp))
            InfoRow("目的地", "武吉免登")
            Spacer(Modifier.height(12.dp))
            InfoRow("联系方式", "+60 12-345 6789")
            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ContactButton("WhatsApp", WhatsAppGreen, Modifier.weight(1f))
                ContactButton("WeChat", WeChatGreen, Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))

            PickupDropoffSlider(stage = stage, onStageChange = { stage = it })
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            color = MutedGray,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
        )
    }
}

@Composable
private fun ContactButton(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable { },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

/** 长条滑块：接人 / 放人 */
@Composable
private fun PickupDropoffSlider(
    stage: Boolean,
    onStageChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF2F2F4)),
    ) {
        val half = maxWidth / 2
        val pillOffset by animateDpAsState(
            targetValue = if (stage) half else 0.dp,
            animationSpec = tween(durationMillis = 250),
            label = "stagePill",
        )

        Box(
            Modifier
                .offset(x = pillOffset)
                .width(half)
                .fillMaxHeight()
                .background(OnlineGreen, RoundedCornerShape(50)),
        )

        Row(Modifier.fillMaxSize()) {
            SliderOption("接人", !stage, Modifier.weight(1f)) { onStageChange(false) }
            SliderOption("放人", stage, Modifier.weight(1f)) { onStageChange(true) }
        }
    }
}

@Composable
private fun SliderOption(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else MutedGray,
        )
    }
}
