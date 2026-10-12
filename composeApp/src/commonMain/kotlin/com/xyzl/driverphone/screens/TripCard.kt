package com.xyzl.driverphone.screens

import androidx.compose.foundation.clickable
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
import com.xyzl.driverphone.model.formatRinggit

private val InkDark = Color(0xFFFFFFFF)
private val MutedGray = Color(0xFFA1A1AA)
private val FaintGray = Color(0xFF71717A)

/** 状态标签颜色（行程列表与详情页共用） */
internal fun statusColor(status: TripStatus): Color = when (status) {
    TripStatus.PENDING -> Color(0xFFFBBF24)
    TripStatus.ONGOING -> Color(0xFF3B82F6)
    TripStatus.COMPLETED -> Color(0xFF6B7280)
    TripStatus.CANCELLED -> Color(0xFFF43F5E)
}

@Composable
internal fun StatusBadge(status: TripStatus) {
    val color = statusColor(status)
    Surface(color = color.copy(alpha = 0.10f), shape = RoundedCornerShape(8.dp)) {
        Text(
            status.label,
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

@Composable
fun TripCard(trip: Trip, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${trip.date} · ${trip.time}",
                    fontSize = 12.sp,
                    color = FaintGray,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${trip.origin} → ${trip.destination}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${trip.passenger} · ${trip.whatsapp}",
                    fontSize = 12.sp,
                    color = MutedGray,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatRinggit(trip.fare),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark,
                )
                Spacer(Modifier.height(6.dp))
                StatusBadge(trip.status)
            }
        }
    }
}
