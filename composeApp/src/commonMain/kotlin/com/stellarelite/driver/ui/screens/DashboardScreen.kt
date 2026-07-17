package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

@Composable
fun DashboardScreen(
    isWorking: Boolean,
    onToggleWork: (Boolean) -> Unit,
    user: DriverUser?
) {
    var showVehiclePicker by remember { mutableStateOf(false) }
    
    // Demo data
    val activeTrip = remember { 
        DriverTrip(
            id = "trip-001",
            customerName = "陈先生",
            pickupAddress = "KLIA Terminal 1, Arrival Hall",
            destinationAddress = "Genting Highlands, Pahang",
            status = "confirmed",
            departureTime = "2026-07-16 15:30",
            driverSalary = 350.0,
            contactPhone = "+60123456789",
            contactWechat = "chen_wechat_88"
        )
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
                    Text("星域臻旅", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("司机工作台", color = DriverColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.Surface).clickable { },
                    contentAlignment = Alignment.Center
                ) { Text("🔔", fontSize = 18.sp) }
            }
        }

        // Work Status Toggle
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isWorking) DriverColors.PrimaryBg else DriverColors.Surface)
                    .border(1.dp, if (isWorking) DriverColors.Primary.copy(alpha = 0.3f) else DriverColors.Border, RoundedCornerShape(20.dp))
                    .clickable { onToggleWork(!isWorking) }
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
                        if (isWorking) "正在接单中" else "系统休眠中",
                        color = if (isWorking) DriverColors.Primary else DriverColors.TextMuted,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isWorking) "您已在线，随时接收新订单" else "点击上线开始接单",
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

        // Active Trip Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("当前任务", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DriverColors.Card)
                    .border(1.dp, DriverColors.Border, RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column {
                    // Trip status badge
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DriverColors.Primary))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("执行中", color = DriverColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("RM ${String.format("%.0f", activeTrip.driverSalary)}", color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Pickup
                    Row(verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(DriverColors.Primary),
                            contentAlignment = Alignment.Center
                        ) { Text("起", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("出发", color = DriverColors.TextMuted, fontSize = 10.sp)
                            Text(activeTrip.pickupAddress, color = DriverColors.TextPrimary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(2.dp).height(20.dp).background(DriverColors.Border).padding(start = 9.dp))
                    
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(DriverColors.Danger),
                            contentAlignment = Alignment.Center
                        ) { Text("终", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("目的地", color = DriverColors.TextMuted, fontSize = 10.sp)
                            Text(activeTrip.destinationAddress, color = DriverColors.TextPrimary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Customer info & action
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(DriverColors.SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) { Text("👤", fontSize = 16.sp) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(activeTrip.customerName, color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(activeTrip.contactPhone, color = DriverColors.TextMuted, fontSize = 11.sp)
                                if (activeTrip.contactWechat.isNotEmpty()) {
                                    Text("微信: ${activeTrip.contactWechat}", color = DriverColors.Primary.copy(alpha = 0.7f), fontSize = 11.sp)
                                }
                            }
                        }
                        
                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(DriverColors.SurfaceVariant)
                                .clickable { }.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) { Text("📞", fontSize = 14.sp) }
                            Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(DriverColors.Primary)
                                .clickable { }.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) { Text("导航", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }

        // Vehicle Info
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

        // Quick Stats
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                statCard("今日薪资", "RM 350", DriverColors.Primary, Modifier.weight(1f))
                statCard("本月行程", "12 单", DriverColors.Warning, Modifier.weight(1f))
                statCard("本月收入", "RM 4,200", Color(0xFF3B82F6), Modifier.weight(1f))
            }
        }

        // Quick Actions
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
    
    // Vehicle picker modal placeholder
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
