package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinanceScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text("财务", fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(Modifier.height(14.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("账户余额", fontSize = 13.sp, color = Color(0xFF8E8E93))
                Spacer(Modifier.height(6.dp))
                Text("RM 2,846.50", fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111113))
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("本周收入", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(Modifier.height(8.dp))
        WeeklyRow("周一", "RM 412")
        WeeklyRow("周二", "RM 386")
        WeeklyRow("周三", "RM 528")
        WeeklyRow("周四", "RM 465")
        WeeklyRow("周五", "RM 509")
    }
}

@Composable
private fun WeeklyRow(day: String, amount: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(day, Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFFC7C7CC))
        Text(amount, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
