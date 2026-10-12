package com.xyzl.driverphone

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.screens.ExpenseSubmitScreen
import com.xyzl.driverphone.screens.FinanceScreen
import com.xyzl.driverphone.screens.LedgerScreen
import com.xyzl.driverphone.screens.ChatScreen
import com.xyzl.driverphone.screens.NotificationsScreen
import com.xyzl.driverphone.screens.ChangePasswordScreen
import com.xyzl.driverphone.screens.DataRetentionPolicyScreen
import com.xyzl.driverphone.screens.DisclaimerScreen
import com.xyzl.driverphone.screens.FinalComplianceScreen
import com.xyzl.driverphone.screens.KycPolicyScreen
import com.xyzl.driverphone.screens.PrivacyPolicyScreen
import com.xyzl.driverphone.screens.RefundPolicyScreen
import com.xyzl.driverphone.screens.UserServiceAgreementScreen
import com.xyzl.driverphone.screens.ChatRecordsScreen
import com.xyzl.driverphone.screens.ChatSettingsScreen
import com.xyzl.driverphone.screens.MyReviewsScreen
import com.xyzl.driverphone.screens.DarkModeScreen
import com.xyzl.driverphone.screens.DisplayScreen
import com.xyzl.driverphone.screens.FontSizeScreen
import com.xyzl.driverphone.screens.LanguageScreen
import com.xyzl.driverphone.screens.NotificationSettingsScreen
import com.xyzl.driverphone.screens.SecuritySettingsScreen
import com.xyzl.driverphone.screens.TranslateToScreen
import com.xyzl.driverphone.screens.TranslationScreen
import com.xyzl.driverphone.screens.DeviceManageScreen
import com.xyzl.driverphone.screens.ForgotPasswordScreen
import com.xyzl.driverphone.screens.PersonalInfoScreen
import com.xyzl.driverphone.screens.ResetPasswordScreen
import com.xyzl.driverphone.screens.VerifyCodeScreen
import com.xyzl.driverphone.screens.PrivacyInfoScreen
import com.xyzl.driverphone.screens.VehicleInfoScreen
import com.xyzl.driverphone.screens.HomeScreen
import com.xyzl.driverphone.screens.EntryScreen
import com.xyzl.driverphone.screens.LoginScreen
import com.xyzl.driverphone.screens.RegisterScreen
import com.xyzl.driverphone.screens.PolicyScreen
import com.xyzl.driverphone.screens.AboutScreen
import com.xyzl.driverphone.screens.EulaScreen
import com.xyzl.driverphone.screens.QrCodeScreen
import com.xyzl.driverphone.screens.VersionUpdateScreen
import com.xyzl.driverphone.screens.ProfileScreen
import com.xyzl.driverphone.screens.TripsScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
    Home("首页"),
    Trips("行程"),
    Chat("聊天"),
    Finance("财务"),
    Profile("我"),
}

private fun tabIcon(tab: AppTab): ImageVector = when (tab) {
    AppTab.Home -> Icons.Filled.Home
    AppTab.Trips -> Icons.Filled.DirectionsCar
    AppTab.Chat -> Icons.AutoMirrored.Filled.Chat
    AppTab.Finance -> Icons.Filled.AccountBalanceWallet
    AppTab.Profile -> Icons.Filled.Person
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun App(
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onApplyUpdate: (suspend (VersionInfo, (Long, Long) -> Unit) -> String?)? = null,
    appVersionName: String = "",
) {
    DriverPhoneTheme {
        var showEntry by remember { mutableStateOf(true) }
        var showLogin by remember { mutableStateOf(false) }
        var showRegister by remember { mutableStateOf(false) }
        var showPolicy by remember { mutableStateOf(false) }
        var preAuthPolicy by remember { mutableStateOf(-1) }
        var loggedIn by remember { mutableStateOf(false) }
        var selectedTab by remember { mutableStateOf(AppTab.Home) }
        var showLedger by remember { mutableStateOf(false) }
        var showExpense by remember { mutableStateOf(false) }
        var showNotifications by remember { mutableStateOf(false) }
        var showPersonalInfo by remember { mutableStateOf(false) }
        var showPrivacyInfo by remember { mutableStateOf(false) }
        var showVehicleInfo by remember { mutableStateOf(false) }
        var showChangePassword by remember { mutableStateOf(false) }
        var showForgotPassword by remember { mutableStateOf(false) }
        var showVerifyCode by remember { mutableStateOf(false) }
        var showResetPassword by remember { mutableStateOf(false) }
        var showDeviceManage by remember { mutableStateOf(false) }
        var verifyEmail by remember { mutableStateOf("") }
        var showSecurity by remember { mutableStateOf(false) }
        var showNotificationSettings by remember { mutableStateOf(false) }
        var showDisplay by remember { mutableStateOf(false) }
        var showDarkMode by remember { mutableStateOf(false) }
        var showFontSize by remember { mutableStateOf(false) }
        var showLanguage by remember { mutableStateOf(false) }
        var showTranslation by remember { mutableStateOf(false) }
        var showTranslateTo by remember { mutableStateOf(false) }
        var showChatSettings by remember { mutableStateOf(false) }
        var showChatRecords by remember { mutableStateOf(false) }
        var showMyReviews by remember { mutableStateOf(false) }
        var policyIndex by remember { mutableStateOf(-1) }
        var showQrCode by remember { mutableStateOf(false) }
        var showAbout by remember { mutableStateOf(false) }
        var showVersionUpdate by remember { mutableStateOf(false) }
        val hazeState = remember { HazeState() }
        var showUpdateDialog by remember { mutableStateOf(false) }
        var updateInfo by remember { mutableStateOf<VersionInfo?>(null) }
        var updating by remember { mutableStateOf(false) }
        var updateProgress by remember { mutableStateOf(0f) }
        var updateError by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        // 按场景申请权限：拍照 → 相机；相册 → 照片和视频
        val requestPick: (ImageSource, (String) -> Unit) -> Unit = { source, cb ->
            requestScenePermission(
                if (source == ImageSource.Camera) PermissionScene.Camera else PermissionScene.Photos
            )
            pickImage(source, cb)
        }

        // 启动时检查更新
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

        // 更新弹窗
        if (showUpdateDialog && updateInfo != null) {
            if (updating) {
                AlertDialog(
                    onDismissRequest = {},
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            "正在更新 v${updateInfo!!.versionName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text("正在下载新版本，请稍候…", fontSize = 14.sp)
                            LinearProgressIndicator(
                                progress = { updateProgress },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                "${(updateProgress * 100).toInt()}%",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    },
                    confirmButton = {},
                )
            } else {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            "发现新版本 v${updateInfo!!.versionName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    text = {
                        Column {
                            Text(
                                updateInfo!!.changelog.replace("- ", "• "),
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                            )
                            if (updateError != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "⚠️ $updateError",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                updateError = null
                                scope.launch {
                                    val fn = onApplyUpdate
                                    if (fn == null) {
                                        updateError = "更新功能不可用"
                                    } else {
                                        updating = true
                                        updateProgress = 0f
                                        val err = fn.invoke(updateInfo!!) { done, total ->
                                            if (total > 0) updateProgress = done.toFloat() / total.toFloat()
                                        }
                                        if (err != null) {
                                            updating = false
                                            updateError = err
                                        } else {
                                            showUpdateDialog = false
                                            updating = false
                                        }
                                    }
                                }
                            },
                        ) {
                            Text(
                                "立即更新",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showUpdateDialog = false }) {
                            Text("稍后", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                )
            }
        }

        if (!loggedIn) {
            Box(Modifier.fillMaxSize()) {
                when {
                    showPolicy -> PolicyScreen(onBack = { showPolicy = false })
                    showEntry -> EntryScreen(
                        onLoginClick = { showEntry = false; showLogin = true },
                        onRegisterClick = { showEntry = false; showRegister = true },
                    )
                    showRegister -> RegisterScreen(
                        onBack = { showRegister = false; showEntry = true },
                        onSubmit = { loggedIn = true },
                        onPickBirthday = { cb -> pickBirthday(cb) },
                        onPickImage = { source, cb -> requestPick(source, cb) },
                        onOpenPolicy = { preAuthPolicy = it },
                    )
                    else -> LoginScreen(
                        onBack = { showLogin = false; showEntry = true },
                        onLoginSuccess = { loggedIn = true },
                        onRegister = { showLogin = false; showRegister = true },
                        onForgetPassword = { showLogin = false; showForgotPassword = true },
                        onOpenPolicy = { preAuthPolicy = it },
                    )
                }

                // 协议与政策页：登录前也能看，返回回到刚才那一页
                if (preAuthPolicy >= 0) {
                    Box(
                        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                    ) {
                        val closePreAuthPolicy = { preAuthPolicy = -1 }
                        when (preAuthPolicy) {
                            0 -> UserServiceAgreementScreen(onBack = closePreAuthPolicy)
                            1 -> PrivacyPolicyScreen(onBack = closePreAuthPolicy)
                            2 -> KycPolicyScreen(onBack = closePreAuthPolicy)
                            3 -> RefundPolicyScreen(onBack = closePreAuthPolicy)
                            4 -> DataRetentionPolicyScreen(onBack = closePreAuthPolicy)
                            5 -> DisclaimerScreen(onBack = closePreAuthPolicy)
                            6 -> FinalComplianceScreen(onBack = closePreAuthPolicy)
                            7 -> EulaScreen(onBack = closePreAuthPolicy)
                            else -> FinalComplianceScreen(onBack = closePreAuthPolicy)
                        }
                    }
                }

                // 忘记密码 → 验证码 → 重建密码：登录前也能走
                if (showForgotPassword || showVerifyCode || showResetPassword) {
                    Box(
                        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                    ) {
                        when {
                            showResetPassword -> ResetPasswordScreen(
                                onBack = { showResetPassword = false },
                                onDone = {
                                    showResetPassword = false
                                    showVerifyCode = false
                                    showForgotPassword = false
                                    showLogin = true
                                    showEntry = false
                                    showRegister = false
                                },
                            )
                            showVerifyCode -> VerifyCodeScreen(
                                email = verifyEmail,
                                onBack = {
                                    showVerifyCode = false
                                    showForgotPassword = true
                                },
                                onConfirm = {
                                    showVerifyCode = false
                                    showResetPassword = true
                                },
                            )
                            else -> ForgotPasswordScreen(
                                onBack = { showForgotPassword = false },
                                onSendCode = { email ->
                                    verifyEmail = email
                                    showForgotPassword = false
                                    showVerifyCode = true
                                },
                            )
                        }
                    }
                }
            }
            return@DriverPhoneTheme
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // 底层内容：首页背景 + 中层卡片（毛玻璃的模糊来源）
            Box(
                Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
            ) {
                when (selectedTab) {
                    AppTab.Home -> HomeScreen(onOpenNotifications = { showNotifications = true })
                    AppTab.Trips -> TripsScreen(onOpenNotifications = { showNotifications = true })
                    AppTab.Chat -> ChatScreen(onOpenNotifications = { showNotifications = true })
                    AppTab.Finance -> FinanceScreen(
                        onOpenLedger = { showLedger = true },
                        onSubmitExpense = { showExpense = true },
                        onOpenNotifications = { showNotifications = true },
                    )
                    AppTab.Profile -> ProfileScreen(
                        onOpenNotifications = { showNotifications = true },
                        onOpenPersonalInfo = { showPersonalInfo = true },
                        onOpenPrivacyInfo = { showPrivacyInfo = true },
                        onOpenVehicleInfo = { showVehicleInfo = true },
                        onOpenChangePassword = { showChangePassword = true },
                        onOpenDeviceManage = { showDeviceManage = true },
                        onOpenSecurity = { showSecurity = true },
                        onOpenNotificationSettings = { showNotificationSettings = true },
                        onOpenDisplay = { showDisplay = true },
                        onOpenChatSettings = { showChatSettings = true },
                        onOpenChatRecords = { showChatRecords = true },
                        onOpenMyReviews = { showMyReviews = true },
                        onOpenPolicy = { policyIndex = it },
                        onOpenQr = { showQrCode = true },
                        onOpenAbout = { showAbout = true },
                        onLogout = {
                            loggedIn = false
                            showEntry = false
                            showLogin = true
                            showRegister = false
                            showPolicy = false
                            selectedTab = AppTab.Home
                        },
                    )
                }
            }

            // 顶层底部快捷栏：毛玻璃 + 边框胶囊，无实色背景
            CapsuleBottomBar(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it },
                hazeState = hazeState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
            )

            // 流水记录页（覆盖层）
            if (showLedger) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LedgerScreen(onBack = { showLedger = false })
                }
            }

            // 提交报销页（覆盖层）
            if (showExpense) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ExpenseSubmitScreen(
                        onBack = { showExpense = false },
                        onPickImage = { source, cb -> requestPick(source, cb) },
                        onPickDocument = { cb -> pickDocument(cb) },
                        onSubmit = { showExpense = false },
                    )
                }
            }

            // 通知页（覆盖层）
            if (showNotifications) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NotificationsScreen(onBack = { showNotifications = false })
                }
            }

            // 个人信息页（覆盖层）
            if (showPersonalInfo) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    PersonalInfoScreen(
                        onBack = { showPersonalInfo = false },
                        onPickImage = { source, cb -> requestPick(source, cb) },
                    )
                }
            }

            // 隐私信息页（覆盖层）
            if (showPrivacyInfo) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    PrivacyInfoScreen(
                        onBack = { showPrivacyInfo = false },
                        onPickBirthday = { cb -> pickBirthday(cb) },
                    )
                }
            }

            // 车辆信息页（覆盖层）
            if (showVehicleInfo) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    VehicleInfoScreen(
                        onBack = { showVehicleInfo = false },
                        onPickImage = { source, cb -> requestPick(source, cb) },
                        onPickDocument = { cb -> pickDocument(cb) },
                    )
                }
            }

            // 司机端密码页（覆盖层）
            if (showChangePassword) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ChangePasswordScreen(
                        onBack = { showChangePassword = false },
                        onForgotPassword = { showForgotPassword = true },
                    )
                }
            }

            // 忘记密码页（覆盖层）
            if (showForgotPassword) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ForgotPasswordScreen(
                        onBack = { showForgotPassword = false },
                        onSendCode = { email ->
                            verifyEmail = email
                            showForgotPassword = false
                            showVerifyCode = true
                        },
                    )
                }
            }

            // 验证码页（覆盖层）
            if (showVerifyCode) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    VerifyCodeScreen(
                        email = verifyEmail,
                        onBack = {
                            showVerifyCode = false
                            showForgotPassword = true
                        },
                        onConfirm = {
                            showVerifyCode = false
                            showResetPassword = true
                        },
                    )
                }
            }

            // 重建密码页（覆盖层）：确认后回到登录页
            if (showResetPassword) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ResetPasswordScreen(
                        onBack = { showResetPassword = false },
                        onDone = {
                            showResetPassword = false
                            showVerifyCode = false
                            showForgotPassword = false
                            showChangePassword = false
                            loggedIn = false
                            showEntry = false
                            showLogin = true
                            showRegister = false
                            showPolicy = false
                            selectedTab = AppTab.Home
                        },
                    )
                }
            }

            // 登录设备管理页（覆盖层）
            if (showDeviceManage) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    DeviceManageScreen(onBack = { showDeviceManage = false })
                }
            }

            // 更多安全设置页（覆盖层）
            if (showSecurity) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SecuritySettingsScreen(onBack = { showSecurity = false })
                }
            }

            // 通知设置页（覆盖层）
            if (showNotificationSettings) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NotificationSettingsScreen(onBack = { showNotificationSettings = false })
                }
            }

            // 界面与显示页（覆盖层）
            if (showDisplay) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    DisplayScreen(
                        onBack = { showDisplay = false },
                        onOpenDarkMode = { showDarkMode = true },
                        onOpenFontSize = { showFontSize = true },
                        onOpenLanguage = { showLanguage = true },
                        onOpenTranslation = { showTranslation = true },
                    )
                }
            }

            // 深色模式页（覆盖层）
            if (showDarkMode) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    DarkModeScreen(onBack = { showDarkMode = false })
                }
            }

            // 设置字体大小页（覆盖层）
            if (showFontSize) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    FontSizeScreen(onBack = { showFontSize = false })
                }
            }

            // 多语言设置页（覆盖层）
            if (showLanguage) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LanguageScreen(onBack = { showLanguage = false })
                }
            }

            // 翻译页（覆盖层）
            if (showTranslation) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TranslationScreen(
                        onBack = { showTranslation = false },
                        onOpenTranslateTo = { showTranslateTo = true },
                    )
                }
            }

            // 将文字翻译为页（覆盖层）
            if (showTranslateTo) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TranslateToScreen(onBack = { showTranslateTo = false })
                }
            }

            // 聊天设置页（覆盖层）
            if (showChatSettings) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ChatSettingsScreen(onBack = { showChatSettings = false })
                }
            }

            // 聊天记录管理页（覆盖层）
            if (showChatRecords) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ChatRecordsScreen(onBack = { showChatRecords = false })
                }
            }

            // 我的评价页（覆盖层）
            if (showMyReviews) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    MyReviewsScreen(onBack = { showMyReviews = false })
                }
            }

            // 关于平台页（覆盖层）
            if (showAbout) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AboutScreen(
                        versionName = appVersionName,
                        onOpenVersionUpdate = { showVersionUpdate = true },
                        onOpenEula = { policyIndex = 7 },
                        onBack = { showAbout = false },
                    )
                }
            }

            // 版本更新页（覆盖层）
            if (showVersionUpdate) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    VersionUpdateScreen(
                        currentVersion = appVersionName,
                        onCheckUpdate = onCheckUpdate,
                        onApplyUpdate = onApplyUpdate,
                        onBack = { showVersionUpdate = false },
                    )
                }
            }

            // 二维码页（覆盖层）
            if (showQrCode) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    QrCodeScreen(onBack = { showQrCode = false })
                }
            }

            // 政策与协议页（覆盖层）
            if (policyIndex >= 0) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val closePolicy = { policyIndex = -1 }
                    when (policyIndex) {
                        0 -> UserServiceAgreementScreen(onBack = closePolicy)
                        1 -> PrivacyPolicyScreen(onBack = closePolicy)
                        2 -> KycPolicyScreen(onBack = closePolicy)
                        3 -> RefundPolicyScreen(onBack = closePolicy)
                        4 -> DataRetentionPolicyScreen(onBack = closePolicy)
                        5 -> DisclaimerScreen(onBack = closePolicy)
                        6 -> FinalComplianceScreen(onBack = closePolicy)
                        7 -> EulaScreen(onBack = closePolicy)
                        else -> FinalComplianceScreen(onBack = closePolicy)
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun CapsuleBottomBar(
    selectedTab: AppTab,
    onSelect: (AppTab) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val itemPositions = remember { mutableStateMapOf<AppTab, IntOffset>() }
    val itemWidths = remember { mutableStateMapOf<AppTab, Int>() }

    val targetX = itemPositions[selectedTab]?.x ?: 0
    val targetW = itemWidths[selectedTab] ?: 0

    val animatedX by animateDpAsState(
        targetValue = with(density) { targetX.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabX",
    )
    val animatedW by animateDpAsState(
        targetValue = with(density) { targetW.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabW",
    )

    // 毛玻璃 + 边框的胶囊，无实色背景
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
            .background(Color(0xFF1C1C1E), RoundedCornerShape(50))
            .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(50))
            .height(52.dp)
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .offset(x = animatedX)
                .width(animatedW)
                .fillMaxHeight()
                .shadow(2.dp, RoundedCornerShape(50))
                .background(Color.White, RoundedCornerShape(50)),
        )

        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTab.entries.forEach { tab ->
                CapsuleBarItem(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.onGloballyPositioned { coords ->
                        itemPositions[tab] =
                            IntOffset(coords.positionInParent().x.roundToInt(), 0)
                        itemWidths[tab] = coords.size.width
                    },
                )
            }
        }
    }
}

@Composable
private fun CapsuleBarItem(
    tab: AppTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) Color(0xFF18181B) else Color(0xFF71717A)

    Row(
        modifier = modifier
            .fillMaxHeight()
            .animateContentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = tabIcon(tab),
            contentDescription = tab.label,
            tint = contentColor,
            modifier = Modifier.size(22.dp),
        )
        if (selected) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = tab.label,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
