package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.MockData
import com.xyzl.driverphone.model.TripStatus

@Composable
fun HistoryScreen() {
    val history = MockData.trips.filter { it.status == TripStatus.COMPLETED || it.status == TripStatus.CANCELLED }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(
            "历史",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE8EDEA),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
        if (history.isEmpty()) {
            Text("暂无历史行程", fontSize = 14.sp, color = Color(0xFF9DB0A8), modifier = Modifier.padding(top = 32.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { trip -> TripCard(trip) }
            }
        }
    }
}
