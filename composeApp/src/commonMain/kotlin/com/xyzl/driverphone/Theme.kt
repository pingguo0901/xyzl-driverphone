package com.xyzl.driverphone

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 深色主题：后台端同款黑底 + 灰绿色卡片 + 沉稳深绿主色
private val AppColors = lightColorScheme(
    primary = Color(0xFF206A4E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBE2),
    onPrimaryContainer = Color(0xFF0B3A28),
    secondary = Color(0xFF206A4E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2A3833),
    onSecondaryContainer = Color(0xFFE8EDEA),
    tertiary = Color(0xFF8E8E93),
    onTertiary = Color.White,
    background = Color(0xFF0B0F0E),
    onBackground = Color(0xFFE8EDEA),
    surface = Color(0xFF2A3833),
    onSurface = Color(0xFFE8EDEA),
    surfaceVariant = Color(0xFF2A3833),
    onSurfaceVariant = Color(0xFF9DB0A8),
    surfaceContainerLowest = Color(0xFF1E2A26),
    surfaceContainerLow = Color(0xFF243029),
    surfaceContainer = Color(0xFF2A3833),
    surfaceContainerHigh = Color(0xFF31403A),
    surfaceContainerHighest = Color(0xFF3A4A44),
    outline = Color(0xFF3A4A44),
    error = Color(0xFFD93025),
    onError = Color.White,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

@Composable
fun DriverPhoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        shapes = AppShapes,
        content = content,
    )
}
