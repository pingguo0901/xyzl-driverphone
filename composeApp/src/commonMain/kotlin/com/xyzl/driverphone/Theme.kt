package com.xyzl.driverphone

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 主色：深色（黑）；副色/卡片：白色
private val AppColors = darkColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2C2C2E),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFF2F2F7),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF5E5CE6),
    onTertiary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color.White,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFFFFFFF),
    outline = Color(0xFFD1D1D6),
    error = Color(0xFFFF453A),
    onError = Color.White,
)

@Composable
fun DriverPhoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
