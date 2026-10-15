package com.xyzl.driverphone.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.ExecutionStatus
import com.xyzl.driverphone.HomeRepository
import com.xyzl.driverphone.HomeSnapshot
import com.xyzl.driverphone.HomeTask
import com.xyzl.driverphone.HomeVehicle
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.formatHomeTimestamp
import com.xyzl.driverphone.waDigits
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.NavigationApp
import com.xyzl.driverphone.PermissionScene
import com.xyzl.driverphone.brandLogoPainter
import com.xyzl.driverphone.openNavigation
import com.xyzl.driverphone.requestScenePermission
import com.xyzl.driverphone.openUrl
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val RestingGray: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark: Color @Composable get() = LocalAppColors.current.ink
private val MutedGray: Color @Composable get() = LocalAppColors.current.muted
private val DividerGray: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val WhatsAppGreen = Color(0xFF25D366)
private val WeChatGreen = Color(0xFF07C160)

@Composable
fun HomeScreen(userId: String?, onOpenNotifications: () -> Unit = {}) {
    val repo = remember { HomeRepository() }
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf(HomeSnapshot()) }

    // 进首页拉一次真实数据：铃铛未读 / 接单开关 / 值班车辆 / 当前任务
    LaunchedEffect(userId) {
        if (userId != null) snapshot = repo.load(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(
            title = "首页",
            onOpenNotifications = onOpenNotifications,
            unreadCount = snapshot.unreadCount,
        )

        GreenDivider()

        OnlineStatusCard(
            online = snapshot.isOnline,
            onToggle = {
                val driverId = snapshot.driverProfileId
                if (driverId != null) {
                    val previous = snapshot.isOnline
                    // 先动界面（等网络回来才变色太钝），写库失败再翻回来
                    snapshot = snapshot.copy(isOnline = !previous)
                    scope.launch {
                        if (!repo.setOnline(driverId, !previous)) {
                            snapshot = snapshot.copy(isOnline = previous)
                        }
                    }
                }
            },
        )

        GreenDivider()

        DutyVehicleCard(
            vehicle = snapshot.vehicle,
            vehicles = snapshot.vehicles,
            onPick = { picked ->
                val driverId = snapshot.driverProfileId
                if (driverId != null) {
                    val previous = snapshot.vehicle
                    snapshot = snapshot.copy(vehicle = picked)
                    scope.launch {
                        if (!repo.setCurrentVehicle(driverId, picked.id)) {
                            snapshot = snapshot.copy(vehicle = previous)
                        }
                    }
                }
            },
        )

        GreenDivider()

        Text(
            "当前任务",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
        )

        CurrentTaskCard(
            task = snapshot.task,
            onAdvance = { next ->
                val task = snapshot.task
                if (task != null) {
                    val previous = task
                    snapshot = snapshot.copy(task = task.copy(executionStatus = next))
                    scope.launch {
                        if (!repo.setExecutionStatus(task.orderId, next)) {
                            snapshot = snapshot.copy(task = previous)
                        }
                    }
                }
            },
        )
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
    val iconTint = OnlineGreen

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.card),
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

/** 值班车辆卡片：当前值班车辆（se_driver_profiles.current_vehicle_id），点击可更换 */
@Composable
private fun DutyVehicleCard(
    vehicle: HomeVehicle?,
    vehicles: List<HomeVehicle>,
    onPick: (HomeVehicle) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(OnlineGreen.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = OnlineGreen,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "值班车辆",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = MutedGray,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    vehicle?.title ?: "未设置值班车辆",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark,
                )
            }
            Text(
                "更换",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnlineGreen,
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = OnlineGreen,
                modifier = Modifier.size(18.dp),
            )
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            containerColor = LocalAppColors.current.card,
            title = {
                Text(
                    "更换值班车辆",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (vehicles.isEmpty()) {
                        Text("没有可用车辆", fontSize = 14.sp, color = MutedGray)
                    } else {
                        vehicles.forEach { item ->
                            // 当前值班车辆 → 蓝点（NavOptionRow 左侧圆点）
                            NavOptionRow(
                                label = item.title,
                                color = if (item.id == vehicle?.id) OnlineGreen else MutedGray,
                                onClick = {
                                    onPick(item)
                                    showPicker = false
                                },
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("取消", color = MutedGray)
                }
            },
        )
    }
}

/** 当前任务卡片：接单时间 / 起终点 / 客户联系方式 / 滑块写 se_order_parents.execution_status */
@Composable
private fun CurrentTaskCard(
    task: HomeTask?,
    onAdvance: (String) -> Unit,
) {
    var navAddress by remember { mutableStateOf<String?>(null) }

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
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            if (task == null) {
                Text(
                    "暂无进行中的任务",
                    fontSize = 15.sp,
                    color = MutedGray,
                )
            } else {
                val status = task.executionStatus
                val name = task.customerName ?: "客户"
                val waNumber = waDigits(task.customerWhatsapp ?: task.customerPhone)

                InfoRow("接单时间", formatHomeTimestamp(task.acceptedAt) ?: "—")

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = DividerGray, thickness = 1.dp)

                Spacer(Modifier.height(16.dp))
                FieldWithAction(
                    label = "起点",
                    value = task.pickup ?: "—",
                    onAction = { task.pickup?.let { navAddress = it } },
                )

                Spacer(Modifier.height(14.dp))

                FieldWithAction(
                    label = "目的地",
                    value = task.dropoff ?: "—",
                    onAction = { task.dropoff?.let { navAddress = it } },
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = DividerGray, thickness = 1.dp)

                // 客户：名称 · 电话 / 名称 · 微信号（品牌按钮同行）
                Spacer(Modifier.height(16.dp))
                ContactRow(
                    text = name + " · " + (task.customerPhone ?: "未填电话"),
                    isWhatsApp = true,
                    onClick = {
                        if (waNumber != null) openUrl("https://wa.me/" + waNumber)
                    },
                )
                Spacer(Modifier.height(10.dp))
                ContactRow(
                    text = name + " · " + (task.customerWechat ?: "未填微信"),
                    isWhatsApp = false,
                    onClick = { openUrl("weixin://") },
                )

                // 滑块：待完成/迟到 → 进行中；超时/进行中 → 已完成
                Spacer(Modifier.height(16.dp))
                when {
                    status == ExecutionStatus.DONE -> CompletedBar()
                    status == ExecutionStatus.OVERDUE || status == ExecutionStatus.ONGOING ->
                        SlideToConfirmBar(
                            label = "滑动确认放人",
                            onConfirm = { onAdvance(ExecutionStatus.DONE) },
                        )
                    else ->
                        SlideToConfirmBar(
                            label = "滑动确认接人",
                            onConfirm = { onAdvance(ExecutionStatus.ONGOING) },
                        )
                }
            }
        }
    }

    navAddress?.let { address ->
        NavigationDialog(
            address = address,
            onDismiss = { navAddress = null },
            onPick = { app ->
                requestScenePermission(PermissionScene.Location)
                openNavigation(app, address)
                navAddress = null
            },
        )
    }
}

/** 乘客联系方式一行：名称 · 号码 + 对应品牌按钮（同行） */
@Composable
private fun ContactRow(text: String, isWhatsApp: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = InkDark,
            modifier = Modifier.weight(1f),
        )
        RoundContactButton(
            painter = brandLogoPainter(isWhatsApp = isWhatsApp),
            contentDescription = if (isWhatsApp) "WhatsApp" else "微信",
            onClick = onClick,
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
                bg = LocalAppColors.current.surfaceVariant,
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
        containerColor = LocalAppColors.current.card,
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
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(42.dp),
        )
    }
}

/** 滑动确认条：胶囊轨道 + 圆球，拖过九成触发确认，不到松手自动回弹 */
@Composable
private fun SlideToConfirmBar(
    label: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(LocalAppColors.current.surfaceVariant),
    ) {
        val density = LocalDensity.current
        val ballSize = 48.dp
        val trackWidth = maxWidth
        val trackHeightPx = with(density) { 56.dp.toPx() }
        val ballPx = with(density) { ballSize.toPx() }
        val trackPx = with(density) { trackWidth.toPx() }
        val maxPx = trackPx - ballPx
        val yOffset = ((trackHeightPx - ballPx) / 2f).roundToInt()
        val triggerPx = maxPx * 0.9f

        val ballAnim = remember(label) { Animatable(0f) }

        // 绿色填充：从左到圆球中心
        val fillFraction = ((ballAnim.value + ballPx / 2f) / trackPx).coerceIn(0f, 1f)

        // 底层灰色文字
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MutedGray,
            )
        }

        // 绿色填充 + 顶层白色文字（裁剪到填充区域，营造扫光揭示效果）
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fillFraction)
                .clipToBounds()
                .background(OnlineGreen),
        ) {
            Box(
                Modifier
                    .width(trackWidth)
                    .fillMaxHeight(),
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

        // 可拖动圆球
        Box(
            Modifier
                .offset { IntOffset(ballAnim.value.roundToInt(), yOffset) }
                .size(ballSize)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { scope.launch { ballAnim.stop() } },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                ballAnim.snapTo((ballAnim.value + dragAmount).coerceIn(0f, maxPx))
                            }
                        },
                        onDragEnd = {
                            if (ballAnim.value >= triggerPx) {
                                onConfirm()
                            } else {
                                scope.launch {
                                    ballAnim.animateTo(
                                        0f,
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow,
                                        ),
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                ballAnim.animateTo(
                                    0f,
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
        ) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = OnlineGreen,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/** 行程已完成状态条 */
@Composable
private fun CompletedBar(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(OnlineGreen),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "行程已完成",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}
