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
import com.stellarelite.driver.network.DriverTripSummary
import com.stellarelite.driver.network.SupabaseClient
import com.stellarelite.driver.ui.theme.DriverColors
import kotlin.math.roundToInt

@Composable
fun TripsScreen(user: DriverUser?) {
    val driverId = user?.driverId ?: user?.id ?: ""
    var filter by remember { mutableStateOf("all") }
    var trips by remember { mutableStateOf<List<DriverTripSummary>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedOrderNo by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(driverId) {
        if (driverId.isNotBlank()) {
            trips = SupabaseClient.getDriverTrips(driverId)
        }
        loading = false
    }

    // 进入详情页
    val detailOrderNo = selectedOrderNo
    if (detailOrderNo != null) {
        TripDetailScreen(
            orderNo = detailOrderNo,
            driverId = driverId,
            onBack = { selectedOrderNo = null }
        )
        return
    }

    val filteredTrips = when (filter) {
        "pending" -> trips.filter { it.status == "pending" || it.status == "confirmed" }
        "completed" -> trips.filter { it.status == "completed" }
        "cancelled" -> trips.filter { it.status == "cancelled" }
        else -> trips
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("行程记录", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("${trips.size} 单", color = DriverColors.TextMuted, fontSize = 13.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all" to "全部", "pending" to "待执行", "completed" to "已完成", "cancelled" to "已取消").forEach { (key, label) ->
                    val selected = filter == key
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (selected) DriverColors.Primary else DriverColors.Surface)
                            .clickable { filter = key }.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(label, color = if (selected) Color.Black else DriverColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (loading) {
            item {
                Text("加载中…", color = DriverColors.TextMuted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 24.dp))
            }
        } else if (filteredTrips.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧭", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("暂无行程记录", color = DriverColors.TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(filteredTrips, key = { it.order_no }) { trip ->
                TripCard(trip = trip, onViewDetail = { selectedOrderNo = trip.order_no })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun TripCard(trip: DriverTripSummary, onViewDetail: () -> Unit) {
    val statusColor = when (trip.status) {
        "completed" -> DriverColors.Primary
        "pending", "confirmed" -> DriverColors.Warning
        "cancelled" -> DriverColors.Danger
        else -> DriverColors.TextMuted
    }
    val statusLabel = when (trip.status) {
        "completed" -> "已完成"
        "pending" -> "待执行"
        "confirmed" -> "已确认"
        "cancelled" -> "已取消"
        else -> trip.status
    }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DriverColors.Card)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) { Text("🚗", fontSize = 18.sp) }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("单号 ${trip.order_no}", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(if (trip.trips_date.isNotBlank()) trip.trips_date else "—", color = DriverColors.TextMuted, fontSize = 11.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RM ${formatAmount(trip.final_amount)}", color = DriverColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DriverColors.Primary))
                Spacer(modifier = Modifier.width(8.dp))
                Text(trip.departure_address, color = DriverColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DriverColors.Danger))
                Spacer(modifier = Modifier.width(8.dp))
                Text(trip.destination_address, color = DriverColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // 查看详情
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DriverColors.PrimaryBg)
                    .clickable { onViewDetail() }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("查看详情 →", color = DriverColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

internal fun formatAmount(value: Double): String {
    val rounded = (value * 100).roundToInt() / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString()
    else rounded.toString()
}
