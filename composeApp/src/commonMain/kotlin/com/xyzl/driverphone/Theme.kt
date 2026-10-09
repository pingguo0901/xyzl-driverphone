package com.xyzl.driverphone

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 专业高级商用：干净白底 + 柔和灰白卡片 + 沉稳深绿主色
private val AppColors = lightColorScheme(
    primary = Color(0xFF206A4E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBE2),
    onPrimaryContainer = Color(0xFF0B3A28),
    secondary = Color(0xFF206A4E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF4F4F6),
    onSecondaryContainer = Color(0xFF111113),
    tertiary = Color(0xFF8E8E93),
    onTertiary = Color.White,
    background = Color(0xFFF5F6F8),
    onBackground = Color(0xFF111113),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111113),
    surfaceVariant = Color(0xFFF4F4F6),
    onSurfaceVariant = Color(0xFF8E8E93),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBFBFC),
    surfaceContainer = Color(0xFFF7F7F8),
    surfaceContainerHigh = Color(0xFFF2F2F4),
    surfaceContainerHighest = Color(0xFFEDEDEF),
    outline = Color(0xFFE8E8EC),
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
