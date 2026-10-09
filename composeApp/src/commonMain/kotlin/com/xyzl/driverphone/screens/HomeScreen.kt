package com.xyzl.driverphone.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.NavigationApp
import com.xyzl.driverphone.openNavigation
import kotlin.math.roundToInt

private val RestingGray = Color(0xFFE9E9EB)
private val OnlineGreen = Color(0xFF34C759)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)
private val DividerGray = Color(0xFFE8E8EC)
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

        GreenDivider()

        OnlineStatusCard(online = online, onToggle = { online = !online })

        GreenDivider()

        Text(
            "当前任务",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        CurrentTaskCard()
    }
}

/** 绿色隔离线：标题与卡片之间的分隔 */
@Composable
private fun GreenDivider() {
    HorizontalDivider(
        color = OnlineGreen.copy(alpha = 0.55f),
        thickness = 1.5.dp,
    )
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

/** 当前任务卡片：日期时间 / 起点 / 目的地 / 联系方式 / 接人放人滑块，各段之间用分隔线 */
@Composable
private fun CurrentTaskCard() {
    var stage by remember { mutableIntStateOf(0) } // 0=待接 1=接人 2=放人
    var navAddress by remember { mutableStateOf<String?>(null) } // 待导航地址（null=关闭弹窗）

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(vertical = 20.dp)) {
            // 日期 时间
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "今天 · 14:30",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedGray,
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 起点
            Spacer(Modifier.height(16.dp))
            FieldWithAction(
                modifier = Modifier.padding(horizontal = 20.dp),
                label = "起点",
                value = "吉隆坡国际机场",
                onAction = { navAddress = "吉隆坡国际机场" },
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 目的地
            Spacer(Modifier.height(16.dp))
            FieldWithAction(
                modifier = Modifier.padding(horizontal = 20.dp),
                label = "目的地",
                value = "武吉免登",
                onAction = { navAddress = "武吉免登" },
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 联系方式
            Spacer(Modifier.height(16.dp))
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "联系方式",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = MutedGray,
                )
                Spacer(Modifier.height(12.dp))
                ContactRow("WhatsApp", "+60 12-345 6789", Icons.Filled.Call, WhatsAppGreen)
                Spacer(Modifier.height(12.dp))
                ContactRow("WeChat", "driver_zhang", Icons.Filled.Chat, WeChatGreen)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 长条滑块：往右滑第一次=接人 第二次=放人
            Spacer(Modifier.height(16.dp))
            PickupDropoffSlider(
                modifier = Modifier.padding(horizontal = 20.dp),
                stage = stage,
                onStageChange = { stage = it },
            )
        }
    }

    navAddress?.let { address ->
        NavigationDialog(
            address = address,
            onDismiss = { navAddress = null },
            onPick = { app ->
                openNavigation(app, address)
                navAddress = null
            },
        )
    }
}

@Composable
private fun FieldWithAction(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onAction: () -> Unit = {},
) {
    Column(modifier) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            color = MutedGray,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
                modifier = Modifier.weight(1f),
            )
            RoundActionButton(
                icon = Icons.Filled.Navigation,
                tint = InkDark,
                bg = Color(0xFFF2F2F4),
                onClick = onAction,
            )
        }
    }
}

/** 选择导航 App 弹窗：Waze / Google Maps */
@Composable
private fun NavigationDialog(
    address: String,
    onDismiss: () -> Unit,
    onPick: (NavigationApp) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                "导航到",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    address,
                    fontSize = 14.sp,
                    color = MutedGray,
                )
                Spacer(Modifier.height(8.dp))
                NavOptionRow("Waze", WhatsAppGreen, onClick = { onPick(NavigationApp.WAZE) })
                NavOptionRow("Google Maps", Color(0xFF4285F4), onClick = { onPick(NavigationApp.GOOGLE_MAPS) })
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = MutedGray)
            }
        },
    )
}

@Composable
private fun NavOptionRow(label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.10f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
        )
    }
}

@Composable
private fun ContactRow(platform: String, value: String, icon: ImageVector, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$platform：",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MutedGray,
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
            modifier = Modifier.weight(1f),
        )
        RoundActionButton(
            icon = icon,
            tint = accent,
            bg = accent.copy(alpha = 0.12f),
        )
    }
}

@Composable
private fun RoundActionButton(
    icon: ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 长条滑块：三段式，往右滑第一次=接人，第二次=放人 */
@Composable
private fun PickupDropoffSlider(
    stage: Int,
    onStageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = listOf("待接", "接人", "放人")

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF2F2F4)),
    ) {
        val density = LocalDensity.current
        val segment = maxWidth / 3
        val usable = with(density) { (maxWidth - segment).toPx() }
        val step = usable / 2f

        var dragPx by remember { mutableFloatStateOf(stage * step) }

        val dragState = rememberDraggableState { delta ->
            dragPx = (dragPx + delta).coerceIn(0f, usable)
        }

        Box(
            Modifier
                .matchParentSize()
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        val nearest = (dragPx / step).roundToInt().coerceIn(0, 2)
                        if (nearest != stage) onStageChange(nearest)
                        dragPx = nearest * step
                    },
                ),
        )

        Box(
            Modifier
                .offset { IntOffset(dragPx.roundToInt(), 0) }
                .width(segment)
                .fillMaxHeight()
                .padding(3.dp)
                .background(OnlineGreen, RoundedCornerShape(50)),
        )

        Row(Modifier.fillMaxSize()) {
            labels.forEachIndexed { i, label ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (i == stage) Color.White else MutedGray,
                    )
                }
            }
        }
    }
}
