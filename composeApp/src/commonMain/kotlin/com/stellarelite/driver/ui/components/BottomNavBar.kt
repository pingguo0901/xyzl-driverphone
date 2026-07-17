package com.stellarelite.driver.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

enum class DriverTab(val label: String, val emoji: String) {
    Chat("聊天", "💬"),
    Trips("行程", "📋"),
    Home("工作台", "🏠"),
    Wallet("财务", "💰"),
    Profile("我的", "👤")
}

@Composable
fun BottomNavBar(
    currentTab: DriverTab,
    onTabSelected: (DriverTab) -> Unit
) {
    val tabs = DriverTab.entries
    val centerIndex = tabs.indexOf(DriverTab.Home)
    val leftTabs = tabs.take(centerIndex)
    val rightTabs = tabs.drop(centerIndex + 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DriverColors.NavBar)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left tabs
            Row(modifier = Modifier.weight(1f)) {
                leftTabs.forEach { tab ->
                    NavTabItem(
                        tab = tab,
                        isSelected = currentTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Center spacer for the prominent button
            Spacer(modifier = Modifier.width(72.dp))

            // Right tabs
            Row(modifier = Modifier.weight(1f)) {
                rightTabs.forEach { tab ->
                    NavTabItem(
                        tab = tab,
                        isSelected = currentTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Center prominent button
        val isCenterActive = currentTab == DriverTab.Home
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = if (isCenterActive)
                            listOf(DriverColors.Primary, DriverColors.PrimaryDim)
                        else
                            listOf(DriverColors.SurfaceVariant, DriverColors.Card)
                    )
                )
                .clickable { onTabSelected(DriverTab.Home) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                DriverTab.Home.emoji,
                fontSize = 26.sp
            )
        }
    }
}

@Composable
private fun NavTabItem(
    tab: DriverTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(tab.emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            tab.label,
            color = if (isSelected) DriverColors.Primary else DriverColors.TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
