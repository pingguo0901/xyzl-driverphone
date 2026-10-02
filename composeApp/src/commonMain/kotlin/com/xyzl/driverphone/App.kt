package com.xyzl.driverphone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.xyzl.driverphone.screens.FinanceScreen
import com.xyzl.driverphone.screens.HistoryScreen
import com.xyzl.driverphone.screens.HomeScreen
import com.xyzl.driverphone.screens.ProfileScreen
import com.xyzl.driverphone.screens.TripsScreen

enum class AppTab(val label: String) {
    Home("首页"),
    Trips("行程"),
    History("历史"),
    Finance("财务"),
    Profile("我")
}

@Composable
fun App() {
    DriverPhoneTheme {
        var selectedTab by remember { mutableStateOf(AppTab.Home) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { TabIcon(tab) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    AppTab.Home -> HomeScreen()
                    AppTab.Trips -> TripsScreen()
                    AppTab.History -> HistoryScreen()
                    AppTab.Finance -> FinanceScreen()
                    AppTab.Profile -> ProfileScreen()
                }
            }
        }
    }
}

@Composable
private fun TabIcon(tab: AppTab) {
    val icon: ImageVector = when (tab) {
        AppTab.Home -> Icons.Filled.Home
        AppTab.Trips -> Icons.Filled.DirectionsCar
        AppTab.History -> Icons.Filled.History
        AppTab.Finance -> Icons.Filled.AccountBalanceWallet
        AppTab.Profile -> Icons.Filled.Person
    }
    Icon(icon, contentDescription = tab.label)
}
