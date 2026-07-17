package com.stellarelite.driver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

enum class DriverTab(val label: String, val emoji: String) {
    Home("工作台", "🏠"),
    Trips("行程", "📋"),
    Wallet("财务", "💰"),
    Profile("我的", "👤")
}

@Composable
fun BottomNavBar(
    currentTab: DriverTab,
    onTabSelected: (DriverTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DriverColors.NavBar)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DriverTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(tab.emoji, fontSize = 20.sp)
                Text(
                    tab.label,
                    color = if (isSelected) DriverColors.Primary else DriverColors.TextMuted,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
