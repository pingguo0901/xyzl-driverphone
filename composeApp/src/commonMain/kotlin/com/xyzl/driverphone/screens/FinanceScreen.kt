package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FinanceScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("财务", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("账户余额", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("RM 2,846.50", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("提现") }
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("明细") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("本周收入", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(day, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Text(amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
