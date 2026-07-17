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
fun TripsScreen() {
    var filter by remember { mutableStateOf("all") }
    
    val allTrips = remember {
        listOf(
            DriverTrip("t1", "陈先生", "KLIA Terminal 1", "Genting Highlands", "completed", "2026-07-16 15:30", 350.0),
            DriverTrip("t2", "李女士", "KL Sentral", "KLCC", "completed", "2026-07-15 10:00", 120.0),
            DriverTrip("t3", "Michael", "Pavilion KL", "Batu Caves", "pending", "2026-07-17 08:00", 180.0),
            DriverTrip("t4", "王先生", "Sunway Pyramid", "Putrajaya", "cancelled", "2026-07-14 14:00", 200.0),
            DriverTrip("t5", "Sarah", "Mid Valley", "KLIA2", "completed", "2026-07-13 06:00", 280.0),
        )
    }
    
    val filteredTrips = when (filter) {
        "pending" -> allTrips.filter { it.status == "pending" }
        "completed" -> allTrips.filter { it.status == "completed" }
        "cancelled" -> allTrips.filter { it.status == "cancelled" }
        else -> allTrips
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("行程记录", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("${allTrips.size} 单", color = DriverColors.TextMuted, fontSize = 13.sp)
            }
        }
        
        // Filter tabs
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all" to "全部", "pending" to "待处理", "completed" to "已完成", "cancelled" to "已取消").forEach { (key, label) ->
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
        
        // Trip list
        items(filteredTrips) { trip ->
            val statusColor = when (trip.status) {
                "completed" -> DriverColors.Primary
                "pending" -> DriverColors.Warning
                "cancelled" -> DriverColors.Danger
                else -> DriverColors.TextMuted
            }
            val statusLabel = when (trip.status) {
                "completed" -> "已完成"
                "pending" -> "待执行"
                "cancelled" -> "已取消"
                else -> trip.status
            }
            
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DriverColors.Card)
                    .border(1.dp, DriverColors.Border, RoundedCornerShape(16.dp))
                    .clickable { }.padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) { Text("👤", fontSize = 18.sp) }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(trip.customerName, color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(trip.departureTime, color = DriverColors.TextMuted, fontSize = 11.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("RM ${String.format("%.0f", trip.driverSalary)}", color = DriverColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // Route
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DriverColors.Primary))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(trip.pickupAddress, color = DriverColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DriverColors.Danger))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(trip.destinationAddress, color = DriverColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
