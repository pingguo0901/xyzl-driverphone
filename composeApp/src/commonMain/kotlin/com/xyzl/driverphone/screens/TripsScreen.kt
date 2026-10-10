package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.MockData
import com.xyzl.driverphone.model.Trip
import com.xyzl.driverphone.model.TripStatus

private val OnlineGreen = Color(0xFF206A4E)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)

private enum class TripFilter(val label: String) {
    ALL("全部"),
    COMPLETED("已完成"),
    INCOMPLETE("未完成"),
    CANCELLED("已取消"),
}

@Composable
fun TripsScreen() {
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    val trip = selectedTrip
    if (trip != null) {
        TripDetailScreen(trip = trip, onBack = { selectedTrip = null })
        return
    }

    var filter by remember { mutableStateOf(TripFilter.ALL) }

    val filtered = MockData.trips.filter { t ->
        when (filter) {
            TripFilter.ALL -> true
            TripFilter.COMPLETED -> t.status == TripStatus.COMPLETED
            TripFilter.INCOMPLETE -> t.status == TripStatus.PENDING || t.status == TripStatus.ONGOING
            TripFilter.CANCELLED -> t.status == TripStatus.CANCELLED
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(
            "行程",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = OnlineGreen.copy(alpha = 0.55f), thickness = 1.5.dp)

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TripFilter.entries.forEach { f ->
                FilterChip(
                    label = f.label,
                    selected = filter == f,
                    onClick = { filter = f },
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) {
            Text("暂无行程", fontSize = 14.sp, color = MutedGray, modifier = Modifier.padding(top = 32.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { t ->
                    TripCard(trip = t, onClick = { selectedTrip = t })
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) OnlineGreen else Color(0xFFF2F2F4)
    val fg = if (selected) Color.White else MutedGray
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = fg,
        )
    }
}
