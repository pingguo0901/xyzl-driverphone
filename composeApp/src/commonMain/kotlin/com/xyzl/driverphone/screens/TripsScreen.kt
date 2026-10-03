package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.MockData
import com.xyzl.driverphone.model.TripStatus

@Composable
fun TripsScreen() {
    val activeTrips = MockData.trips.filter { it.status == TripStatus.PENDING || it.status == TripStatus.ONGOING }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text("行程", fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(Modifier.height(14.dp))
        if (activeTrips.isEmpty()) {
            Text("暂无进行中的行程", fontSize = 14.sp, color = Color(0xFF8E8E93), modifier = Modifier.padding(top = 32.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(activeTrips) { trip -> TripCard(trip) }
            }
        }
    }
}
