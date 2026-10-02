package com.xyzl.driverphone

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.xyzl.driverphone.screens.ProfileScreen
import com.xyzl.driverphone.screens.TripsScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.roundToInt

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
fun App() {
    DriverPhoneTheme {
        var selectedTab by remember { mutableStateOf(AppTab.Home) }
        val hazeState = remember { HazeState() }

        Box(Modifier.fillMaxSize()) {
            // 页面内容（毛玻璃的模糊来源）
            Box(
                Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .padding(bottom = 88.dp)
            ) {
                when (selectedTab) {
                    AppTab.Home -> HomeScreen()
                    AppTab.Trips -> TripsScreen()
                    AppTab.History -> HistoryScreen()
                    AppTab.Finance -> FinanceScreen()
                    AppTab.Profile -> ProfileScreen()
                }
            }

            // 胶囊式毛玻璃底部栏
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

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
            .height(52.dp)
            .padding(4.dp),
    ) {
        // 白色滑块（滑到选中项下方）
        Box(
            modifier = Modifier
                .offset(x = animatedX)
                .width(animatedW)
                .fillMaxHeight()
                .background(Color.White, RoundedCornerShape(50)),
        )

        // 五个 tab 项
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
    val contentColor = if (selected) Color(0xFF1B1B1B) else Color(0xFF8A8A8A)

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
