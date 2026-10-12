package com.xyzl.driverphone

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 全站色板 —— 对齐后台端暗色规格：
 * 近纯黑背景 + 蓝色主色，表面/卡片分层，文字三级灰阶。
 */
object AppPalette {
    // 背景与表面
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF18181B)
    val SurfaceVariant = Color(0xFF27272A)
    val Card = Color(0xFF1C1C1E)
    val CardAlt = Color(0xFF222226)
    val Border = Color(0x15FFFFFF)

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
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA1A1AA)
    val TextMuted = Color(0xFF71717A)
    val TextDisabled = Color(0xFF52525B)

    // 状态徽章
    val StatusPending = Color(0xFFFBBF24)
    val StatusConfirmed = Color(0xFF3B82F6)
    val StatusInProgress = Color(0xFF10B981)
    val StatusCompleted = Color(0xFF6B7280)
}

private val AppColors = darkColorScheme(
    primary = AppPalette.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    inversePrimary = AppPalette.PrimaryDim,
    secondary = AppPalette.Primary,
    onSecondary = Color.White,
    secondaryContainer = AppPalette.SurfaceVariant,
    onSecondaryContainer = Color(0xFFE4E4E7),
    tertiary = AppPalette.TextMuted,
    onTertiary = Color.White,
    background = AppPalette.Background,
    onBackground = AppPalette.TextPrimary,
    surface = AppPalette.Surface,
    onSurface = AppPalette.TextPrimary,
    surfaceVariant = AppPalette.SurfaceVariant,
    onSurfaceVariant = AppPalette.TextSecondary,
    surfaceTint = AppPalette.Primary,
    surfaceContainerLowest = Color(0xFF0A0A0A),
    surfaceContainerLow = AppPalette.Surface,
    surfaceContainer = AppPalette.Card,
    surfaceContainerHigh = AppPalette.CardAlt,
    surfaceContainerHighest = AppPalette.SurfaceVariant,
    inverseSurface = Color(0xFFE4E4E7),
    inverseOnSurface = Color(0xFF18181B),
    error = AppPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECDD3),
    outline = Color(0x33FFFFFF),
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
