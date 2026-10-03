package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.Trip
import com.xyzl.driverphone.model.TripStatus

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${trip.origin} → ${trip.destination}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111113),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("乘客：${trip.passenger}", fontSize = 13.sp, color = Color(0xFF8E8E93))
                    Spacer(Modifier.height(2.dp))
                    Text("${trip.distanceKm} km · ${trip.time}", fontSize = 12.sp, color = Color(0xFFB0B0B5))
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("RM ${trip.fare}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111113))
                    Spacer(Modifier.height(6.dp))
                    StatusBadge(trip.status)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TripStatus) {
    val (text, color) = when (status) {
        TripStatus.PENDING -> "待接单" to Color(0xFFFF9F0A)
        TripStatus.ONGOING -> "进行中" to Color(0xFF0A84FF)
        TripStatus.COMPLETED -> "已完成" to Color(0xFF34C759)
        TripStatus.CANCELLED -> "已取消" to Color(0xFFFF453A)
    }
    Surface(color = color.copy(alpha = 0.10f), shape = RoundedCornerShape(8.dp)) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}
