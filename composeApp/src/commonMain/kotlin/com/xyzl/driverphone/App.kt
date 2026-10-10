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
import androidx.compose.material.icons.filled.History
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
import com.xyzl.driverphone.screens.FinanceScreen
import com.xyzl.driverphone.screens.HistoryScreen
import com.xyzl.driverphone.screens.HomeScreen
import com.xyzl.driverphone.screens.EntryScreen
import com.xyzl.driverphone.screens.LoginScreen
import com.xyzl.driverphone.screens.RegisterScreen
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
    History("历史"),
    Finance("财务"),
    Profile("我"),
}

private fun tabIcon(tab: AppTab): ImageVector = when (tab) {
    AppTab.Home -> Icons.Filled.Home
    AppTab.Trips -> Icons.Filled.DirectionsCar
    AppTab.History -> Icons.Filled.History
    AppTab.Finance -> Icons.Filled.AccountBalanceWallet
    AppTab.Profile -> Icons.Filled.Person
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun App(
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onApplyUpdate: (suspend (VersionInfo, (Long, Long) -> Unit) -> String?)? = null,
) {
    DriverPhoneTheme {
        var showEntry by remember { mutableStateOf(true) }
        var showLogin by remember { mutableStateOf(false) }
        var showRegister by remember { mutableStateOf(false) }
        var loggedIn by remember { mutableStateOf(false) }
        var selectedTab by remember { mutableStateOf(AppTab.Home) }
        val hazeState = remember { HazeState() }
        var showUpdateDialog by remember { mutableStateOf(false) }
        var updateInfo by remember { mutableStateOf<VersionInfo?>(null) }
        var updating by remember { mutableStateOf(false) }
        var updateProgress by remember { mutableStateOf(0f) }
        var updateError by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

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
            when {
                showEntry -> EntryScreen(
                    onLoginClick = { showEntry = false; showLogin = true },
                    onRegisterClick = { showEntry = false; showRegister = true },
                )
                showRegister -> RegisterScreen(
                    onBack = { showRegister = false; showEntry = true },
                    onSubmit = { loggedIn = true },
                    onPickBirthday = { cb -> pickBirthday(cb) },
                    onPickImage = { source, cb -> pickImage(source, cb) },
                )
                else -> LoginScreen(
                    onLoginSuccess = { loggedIn = true },
                    onRegister = { showLogin = false; showRegister = true },
                )
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
                    AppTab.Home -> HomeScreen()
                    AppTab.Trips -> TripsScreen()
                    AppTab.History -> HistoryScreen()
                    AppTab.Finance -> FinanceScreen()
                    AppTab.Profile -> ProfileScreen()
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
            .border(1.dp, Color(0xFFE4E6EA), RoundedCornerShape(50))
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
    val contentColor = if (selected) Color(0xFF206A4E) else Color(0xFF9AA0A6)

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
