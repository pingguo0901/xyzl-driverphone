package com.xyzl.driverphone.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.NavigationApp
import com.xyzl.driverphone.openNavigation
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val RestingGray = Color(0xFFEDEDEF)
private val OnlineGreen = Color(0xFF206A4E)
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
            color = InkDark,
        )

        GreenDivider()

        OnlineStatusCard(online = online, onToggle = { online = !online })

        GreenDivider()

        Text(
            "当前任务",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
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

/** 当前任务卡片：接单时间 / 起终点 / 乘客联系方式 / WhatsApp微信 / 滑动确认滑块 */
@Composable
private fun CurrentTaskCard() {
    var stage by remember { mutableIntStateOf(0) } // 0=待接 1=接人 2=放人
    var navAddress by remember { mutableStateOf<String?>(null) } // 待导航地址（null=关闭弹窗）

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.45f),
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            // 接单时间
            InfoRow("接单时间", "今天 · 14:30")

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 起点
            Spacer(Modifier.height(16.dp))
            FieldWithAction(
                label = "起点",
                value = "吉隆坡国际机场",
                onAction = { navAddress = "吉隆坡国际机场" },
            )

            Spacer(Modifier.height(14.dp))

            // 目的地
            FieldWithAction(
                label = "目的地",
                value = "武吉免登",
                onAction = { navAddress = "武吉免登" },
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // 乘客联系方式
            Spacer(Modifier.height(16.dp))
            InfoRow("乘客联系方式", "张先生 · +60 12-345 6789")

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)

            // WhatsApp / 微信圆形联系按钮（右下角）
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "联系乘客",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = MutedGray,
                )
                Spacer(Modifier.weight(1f))
                RoundContactButton(
                    icon = Icons.Filled.Call,
                    tint = Color.White,
                    bg = WhatsAppGreen,
                    contentDescription = "WhatsApp",
                )
                Spacer(Modifier.width(10.dp))
                RoundContactButton(
                    icon = Icons.Filled.Chat,
                    tint = Color.White,
                    bg = WeChatGreen,
                    contentDescription = "微信",
                )
            }

            // 滑动确认滑块
            Spacer(Modifier.height(16.dp))
            PickupDropoffSlider(stage = stage, onStageChange = { stage = it })
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
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            color = MutedGray,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
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

@Composable
private fun RoundContactButton(
    icon: ImageVector,
    tint: Color,
    bg: Color,
    contentDescription: String,
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .background(bg)
            .clickable { },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** 滑动确认滑块：胶囊轨道，拖动圆球，绿色填充，未到位松手自动回弹。待接 → 接人 → 放人 */
@Composable
private fun PickupDropoffSlider(
    stage: Int,
    onStageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = listOf("待接", "接人", "放人")
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFEDEFF3)),
    ) {
        val density = LocalDensity.current
        val ballSize = 48.dp
        val trackHeightPx = with(density) { 56.dp.toPx() }
        val ballPx = with(density) { ballSize.toPx() }
        val trackPx = with(density) { maxWidth.toPx() }
        val maxPx = trackPx - ballPx
        val stepPx = maxPx / 2f
        val yOffset = ((trackHeightPx - ballPx) / 2f).roundToInt()

        val ballAnim = remember { Animatable(stage * stepPx) }

        // 绿色填充：从左到圆球中心
        val fillFraction = ((ballAnim.value + ballPx / 2f) / trackPx).coerceIn(0f, 1f)
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fillFraction)
                .background(OnlineGreen),
        )

        // 阶段标签
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            labels.forEachIndexed { i, label ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (i <= stage) Color.White else MutedGray,
                    )
                }
            }
        }

        // 可拖动圆球
        Box(
            Modifier
                .offset { IntOffset(ballAnim.value.roundToInt(), yOffset) }
                .size(ballSize)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .pointerInput(stage) {
                    detectHorizontalDragGestures(
                        onDragStart = { scope.launch { ballAnim.stop() } },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                ballAnim.snapTo((ballAnim.value + dragAmount).coerceIn(0f, maxPx))
                            }
                        },
                        onDragEnd = {
                            val nearest = (ballAnim.value / stepPx).roundToInt().coerceIn(0, 2)
                            onStageChange(nearest)
                            scope.launch {
                                ballAnim.animateTo(
                                    nearest * stepPx,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow,
                                    ),
                                )
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                ballAnim.animateTo(
                                    stage * stepPx,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow,
                                    ),
                                )
                            }
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        )
    }
}
