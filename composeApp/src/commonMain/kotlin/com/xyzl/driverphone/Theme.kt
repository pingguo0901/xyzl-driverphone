package com.xyzl.driverphone

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 全站色板 —— 浅色主题：
 * 纯白背景 + 蓝色主色，表面/卡片分层，文字三级灰阶。
 */
object AppPalette {
    // 背景与表面
    val Background = Color(0xFFFFFFFF)
    val Surface = Color(0xFFF7F7F9)
    val SurfaceVariant = Color(0xFFEFEFF3)
    val Card = Color(0xFFF7F7F9)
    val CardAlt = Color(0xFFF2F2F7)
    val Border = Color(0x1A000000)

    // 主色（蓝）
    val Primary = Color(0xFF3B82F6)
    val PrimaryDim = Color(0xFF2563EB)
    val PrimaryBg = Color(0x1A3B82F6)

    // 功能色
    val Success = Color(0xFF10B981)
    val Danger = Color(0xFFF43F5E)
    val Warning = Color(0xFFFBBF24)
    val Info = Color(0xFF06B6D4)

    // 文字（三级灰阶 + 占位符）
    val TextPrimary = Color(0xFF111111)
    val TextSecondary = Color(0xFF6B7280)
    val TextMuted = Color(0xFF9CA3AF)
    val TextDisabled = Color(0xFFB0B0B8)

    // 状态徽章
    val StatusPending = Color(0xFFFBBF24)
    val StatusConfirmed = Color(0xFF3B82F6)
    val StatusInProgress = Color(0xFF10B981)
    val StatusCompleted = Color(0xFF6B7280)
}

private val AppColors = lightColorScheme(
    primary = AppPalette.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    inversePrimary = AppPalette.PrimaryDim,
    secondary = AppPalette.Primary,
    onSecondary = Color.White,
    secondaryContainer = AppPalette.SurfaceVariant,
    onSecondaryContainer = Color(0xFF3F3F46),
    tertiary = AppPalette.TextMuted,
    onTertiary = Color.White,
    background = AppPalette.Background,
    onBackground = AppPalette.TextPrimary,
    surface = AppPalette.Background,
    onSurface = AppPalette.TextPrimary,
    surfaceVariant = AppPalette.SurfaceVariant,
    onSurfaceVariant = AppPalette.TextSecondary,
    surfaceTint = AppPalette.Primary,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = AppPalette.Surface,
    surfaceContainer = AppPalette.Card,
    surfaceContainerHigh = AppPalette.CardAlt,
    surfaceContainerHighest = AppPalette.SurfaceVariant,
    inverseSurface = Color(0xFF27272A),
    inverseOnSurface = Color(0xFFF4F4F5),
    error = AppPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    outline = Color(0x33000000),
    outlineVariant = AppPalette.Border,
    scrim = Color(0xCC000000),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

@Composable
fun DriverPhoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        shapes = AppShapes,
        content = content,
    )
}
