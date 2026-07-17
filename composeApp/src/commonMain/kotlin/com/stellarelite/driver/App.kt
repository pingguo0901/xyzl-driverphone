package com.stellarelite.driver

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stellarelite.driver.ui.components.BottomNavBar
import com.stellarelite.driver.ui.components.DriverTab
import com.stellarelite.driver.ui.screens.*
import com.stellarelite.driver.ui.theme.DriverColors

enum class AppView {
    Landing, Login, Register, Dashboard, ResetPassword
}

@Composable
fun App() {
    var currentView by remember { mutableStateOf(AppView.Landing) }
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(DriverTab.Home) }
    var isWorking by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<DriverUser?>(null) }

    // Splash screen
    if (showSplash) {
        LaunchScreen { showSplash = false }
        return
    }

    // Not logged in screens
    when (currentView) {
        AppView.Landing -> {
            LandingScreen(
                onEnterSystem = { currentView = AppView.Login }
            )
            return
        }
        AppView.Login -> {
            LoginScreen(
                onLoginSuccess = { loggedInUser ->
                    user = loggedInUser
                    currentView = AppView.Dashboard
                },
                onRegister = { currentView = AppView.Register },
                onForgotPassword = { currentView = AppView.ResetPassword }
            )
            return
        }
        AppView.Register -> {
            RegisterScreen(
                onBack = { currentView = AppView.Login },
                onRegisterSuccess = { currentView = AppView.Login }
            )
            return
        }
        AppView.ResetPassword -> {
            ResetPasswordScreen(
                onBack = { currentView = AppView.Login }
            )
            return
        }
        AppView.Dashboard -> { /* main dashboard with tabs */ }
    }

    // Dashboard - Tab Navigation
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverColors.Background)
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)) {
            AnimatedContent(targetState = currentTab) { tab ->
                when (tab) {
                    DriverTab.Home -> DashboardScreen(
                        isWorking = isWorking,
                        onToggleWork = { isWorking = it },
                        user = user
                    )
                    DriverTab.Trips -> TripsScreen()
                    DriverTab.Wallet -> WalletScreen()
                    DriverTab.Profile -> ProfileScreen(
                        user = user,
                        onLogout = {
                            user = null
                            currentView = AppView.Landing
                        },
                        onNavigateToLogin = { currentView = AppView.Login }
                    )
                }
            }
        }
        BottomNavBar(currentTab = currentTab, onTabSelected = { currentTab = it })
    }
}
