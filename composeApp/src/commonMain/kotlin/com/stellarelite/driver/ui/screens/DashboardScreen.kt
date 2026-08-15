package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.network.DriverOrder
import com.stellarelite.driver.network.SupabaseClient
import com.stellarelite.driver.platform.navigateWithWaze
import com.stellarelite.driver.platform.nowDateTimeString
import com.stellarelite.driver.ui.theme.DriverColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    isWorking: Boolean,
    onToggleWork: (Boolean) -> Unit,
    user: DriverUser?
) {
    val scope = rememberCoroutineScope()
    val driverId = user?.driverId ?: user?.id ?: ""

    var showVehiclePicker by remember { mutableStateOf(false) }
    var currentOrder by remember { mutableStateOf<DriverOrder?>(null) }
    var loadingOrder by remember { mutableStateOf(true) }

    // 拉伸按钮记录的时间
    var pickedUp by remember { mutableStateOf(false) }
    var pickupTime by remember { mutableStateOf<String?>(null) }
    var dropoffTime by remember { mutableStateOf<String?>(null) }

    // 启动时拉取值班状态 + 当前任务
    LaunchedEffect(driverId) {
        if (driverId.isNotBlank()) {
            val status = SupabaseClient.getDriverStatus(driverId)
            if (status != null) {
                onToggleWork(status == "online")
            }
            val order = SupabaseClient.getDriverCurrentOrder(driverId)
            currentOrder = order
            if (order != null) {
                pickedUp = order.status.lowercase().let {
                    it.contains("pick") || it.contains("picked") || it.contains("ongoing") || it.contains("on_board")
                }
            }
            loadingOrder = false
        } else {
            loadingOrder = false
        }
    }

    fun toggleWork() {
        val newVal = !isWorking
        onToggleWork(newVal)
        if (driverId.isNotBlank()) {
            scope.launch {
                SupabaseClient.updateDriverStatus(driverId, if (newVal) "online" else "offline")
            }
        }
    }

    // 导航：未 pickup -> 出发地；已 pickup -> 目的地
    fun navigate() {
        val order = currentOrder ?: return
        val target = if (pickedUp) order.destination_address else order.departure_address
        if (target.isNotBlank()) navigateWithWaze(target)
    }

    val vehicles = remember {
        listOf(
            DriverVehicle("v1", "SJJ 8849", "丰田", "第四代 埃尔法", isActive = true),
            DriverVehicle("v2", "SJJ 1123", "现代", "Staria", isActive = false)
        )
    }
    val activeVehicle = vehicles.find { it.isActive }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("星域司导", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("司机工作台", color = DriverColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.Surface).clickable { },
                    contentAlignment = Alignment.Center
                ) { Text("🔔", fontSize = 18.sp) }
            }
        }

        // 值班状态 Toggle（链接 driver_profile.status）
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isWorking) DriverColors.PrimaryBg else DriverColors.Surface)
                    .border(1.dp, if (isWorking) DriverColors.Primary.copy(alpha = 0.3f) else DriverColors.Border, RoundedCornerShape(20.dp))
                    .clickable { toggleWork() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(if (isWorking) DriverColors.Primary else DriverColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) { Text(if (isWorking) "🟢" else "⏸️", fontSize = 18.sp) }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isWorking) "正在值班中" else "系统休眠中",
                        color = if (isWorking) DriverColors.Primary else DriverColors.TextMuted,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isWorking) "您已上线，随时接收新订单" else "点击值班开始接单",
                        color = DriverColors.TextMuted, fontSize = 11.sp
                    )
                }
                Box(modifier = Modifier.clip(CircleShape).background(if (isWorking) DriverColors.Primary else DriverColors.TextDisabled)
                    .size(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(if (isWorking) "ON" else "OFF", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 当前任务卡片
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("当前任务", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DriverColors.Card)
                    .border(1.dp, DriverColors.Border, RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                if (loadingOrder) {
                    Text("加载中…", color = DriverColors.TextMuted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 24.dp))
                } else if (currentOrder == null) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧭", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("暂无当前任务", color = DriverColors.TextMuted, fontSize = 13.sp)
                    }
                } else {
                    val order = currentOrder!!
                    Column {
                        // 订单号 + 金额
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DriverColors.Primary))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (pickedUp) "行程中" else "待接客",
                                    color = DriverColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("单号 ${order.order_no}", color = DriverColors.TextMuted, fontSize = 10.sp)
                            }
                            Text(
                                "RM ${formatAmount(order.final_amount)}",
                                color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 出发
                        Row(verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(DriverColors.Primary),
                                contentAlignment = Alignment.Center
                            ) { Text("起", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("出发", color = DriverColors.TextMuted, fontSize = 10.sp)
                                Text(order.departure_address, color = DriverColors.TextPrimary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Box(modifier = Modifier.width(2.dp).height(20.dp).background(DriverColors.Border).padding(start = 9.dp))
                        Spacer(modifier = Modifier.height(3.dp))

                        // 目的地
                        Row(verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(DriverColors.Danger),
                                contentAlignment = Alignment.Center
                            ) { Text("终", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("目的地", color = DriverColors.TextMuted, fontSize = 10.sp)
                                Text(order.destination_address, color = DriverColors.TextPrimary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 联系方式（whatsapp / wechat）
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(DriverColors.SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) { Text("👤", fontSize = 16.sp) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                if (order.whatsapp.isNotBlank()) Text("WhatsApp: ${order.whatsapp}", color = DriverColors.TextMuted, fontSize = 11.sp)
                                if (order.wechat.isNotBlank()) Text("微信: ${order.wechat}", color = DriverColors.Primary.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 导航按钮（自动识别 pickup/dropoff）
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DriverColors.Primary)
                                .clickable { navigate() }.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (pickedUp) "🧭 导航至目的地" else "🧭 导航至出发地",
                                color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 拉伸式确认按钮：滑动记录 pickup / dropoff 时间
                        SlideToConfirmButton(
                            hint = if (!pickedUp) "滑动确认已接客" else "滑动确认已送达",
                            onConfirmed = {
                                if (!pickedUp) {
                                    pickupTime = nowDateTimeString()
                                    pickedUp = true
                                } else {
                                    dropoffTime = nowDateTimeString()
                                }
                            }
                        )

                        // 已记录的时间
                        if (pickupTime != null || dropoffTime != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                if (pickupTime != null) {
                                    Text("接客: $pickupTime", color = DriverColors.Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                if (dropoffTime != null) {
                                    Text("送达: $dropoffTime", color = DriverColors.Warning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 出勤车辆
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                    .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp))
                    .clickable { showVehiclePicker = true }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(DriverColors.PrimaryBg),
                    contentAlignment = Alignment.Center
                ) { Text("🚗", fontSize = 22.sp) }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("出勤车辆", color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(
                        activeVehicle?.let { "${it.plateNo} · ${it.brand} ${it.model}" } ?: "尚未登记车辆",
                        color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
                Text("切换 →", color = DriverColors.TextMuted, fontSize = 12.sp)
            }
        }

        // 快速统计
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                statCard("今日薪资", "RM 350", DriverColors.Primary, Modifier.weight(1f))
                statCard("本月行程", "12 单", DriverColors.Warning, Modifier.weight(1f))
                statCard("本月收入", "RM 4,200", Color(0xFF3B82F6), Modifier.weight(1f))
            }
        }

        // 快捷操作
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("快捷操作", color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                actionBtn("⛽", "报开销", Modifier.weight(1f)) { }
                actionBtn("💰", "上交现金", Modifier.weight(1f)) { }
                actionBtn("📍", "位置共享", Modifier.weight(1f)) { }
                actionBtn("📋", "全部行程", Modifier.weight(1f)) { }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // 车辆选择弹窗
    if (showVehiclePicker) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).clickable { showVehiclePicker = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(DriverColors.Surface).padding(24.dp)
            ) {
                Text("选择出勤车辆", color = DriverColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                vehicles.forEach { v ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(if (v.isActive) DriverColors.PrimaryBg else DriverColors.Background)
                            .clickable { showVehiclePicker = false }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚗", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.plateNo, color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("${v.brand} ${v.model}", color = DriverColors.TextMuted, fontSize = 12.sp)
                        }
                        if (v.isActive) Text("✓", color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/** 拉伸式（滑动确认）按钮 */
@Composable
private fun SlideToConfirmButton(
    hint: String,
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    val thumbSize = 40.dp
    val thumbPx = with(LocalDensity.current) { thumbSize.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(DriverColors.SurfaceVariant)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
    ) {
        Text(
            hint,
            color = DriverColors.TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        val maxOffset = (trackWidthPx - thumbPx).coerceAtLeast(0f)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .padding(6.dp)
                .size(thumbSize)
                .clip(CircleShape)
                .background(DriverColors.Primary)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX >= maxOffset * 0.8f) {
                                onConfirmed()
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(0f, maxOffset)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("→", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun formatAmount(value: Double): String {
    val rounded = (value * 100).roundToInt() / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString()
    else rounded.toString()
}

@Composable
private fun statCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(12.dp)
    ) {
        Text(label, color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = accent, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun actionBtn(emoji: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = DriverColors.TextSecondary, fontSize = 11.sp)
    }
}
