package com.stellarelite.driver

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.model.VersionInfo
import com.stellarelite.driver.ui.components.BottomNavBar
import com.stellarelite.driver.ui.components.DriverTab
import com.stellarelite.driver.ui.screens.*
import com.stellarelite.driver.ui.theme.DriverColors

enum class AppView {
    Landing, Login, Register, Dashboard, ResetPassword
}

@Composable
fun App(
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onRequestUpdate: ((VersionInfo) -> Unit)? = null
) {
    var currentView by remember { mutableStateOf(AppView.Landing) }
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(DriverTab.Home) }
    var isWorking by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<DriverUser?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<VersionInfo?>(null) }

    // Check for updates on launch
    LaunchedEffect(Unit) {
        onCheckUpdate?.let { checkFn ->
            try {
                val info = checkFn()
                if (info != null) {
                    updateInfo = info
                    showUpdateDialog = true
                }
            } catch (_: Exception) { }
        }
    }

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
                    DriverTab.Chat -> ChatScreen()
                    DriverTab.Trips -> TripsScreen()
                    DriverTab.Home -> DashboardScreen(
                        isWorking = isWorking,
                        onToggleWork = { isWorking = it },
                        user = user
                    )
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

    // Update Dialog
    if (showUpdateDialog && updateInfo != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            containerColor = DriverColors.Surface,
            title = {
                Text(
                    "发现新版本 v${updateInfo!!.versionName}",
                    color = DriverColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    updateInfo!!.changelog.replace("- ", "• "),
                    color = DriverColors.TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUpdateDialog = false
                        onRequestUpdate?.invoke(updateInfo!!)
                    }
                ) {
                    Text("立即更新", color = DriverColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("稍后", color = DriverColors.TextMuted)
                }
            }
        )
    }
}
