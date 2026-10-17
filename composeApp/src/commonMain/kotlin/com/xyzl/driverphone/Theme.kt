package com.xyzl.driverphone

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.xyzl.driverphone.screens.DisplayPrefs

/** 主题色板（随 深色 / 浅色 切换） */
data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val card: Color,
    val cardAlt: Color,
    val border: Color,
    val ink: Color,
    val muted: Color,
    val textMuted: Color,
    val textDisabled: Color,
)

private val LightAppColors = AppThemeColors(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF6F6F8),
    surfaceVariant = Color(0xFFE7E7ED),
    card = Color(0xFFF2F2F6),
    cardAlt = Color(0xFFE9E9EF),
    border = Color(0x24000000),
    ink = Color(0xFF111111),
    muted = Color(0xFF5F6470),
    textMuted = Color(0xFF8A8F98),
    textDisabled = Color(0xFFA6A6AE),
)

private val DarkAppColors = AppThemeColors(
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF18181B),
    surfaceVariant = Color(0xFF27272A),
    card = Color(0xFF1C1C1E),
    cardAlt = Color(0xFF222226),
    border = Color(0x15FFFFFF),
    ink = Color(0xFFFFFFFF),
    muted = Color(0xFFA1A1AA),
    textMuted = Color(0xFF71717A),
    textDisabled = Color(0xFF52525B),
)

/** 当前生效的色板（浅色 / 深色） */
val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/** 品牌主色与功能色（深浅色一致） */
object AppPalette {
    val Primary = Color(0xFF3B82F6)
    val PrimaryDim = Color(0xFF2563EB)
    val PrimaryBg = Color(0x1A3B82F6)
    val Success = Color(0xFF10B981)
    val Danger = Color(0xFFF43F5E)
    val Warning = Color(0xFFFBBF24)
    val Info = Color(0xFF06B6D4)
    val StatusPending = Color(0xFFFBBF24)
    val StatusConfirmed = Color(0xFF3B82F6)
    val StatusInProgress = Color(0xFF10B981)
    val StatusCompleted = Color(0xFF6B7280)
}

private val LightScheme = lightColorScheme(
    primary = AppPalette.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    inversePrimary = AppPalette.PrimaryDim,
    secondary = AppPalette.Primary,
    onSecondary = Color.White,
    secondaryContainer = LightAppColors.surfaceVariant,
    onSecondaryContainer = Color(0xFF3F3F46),
    tertiary = LightAppColors.textMuted,
    onTertiary = Color.White,
    background = LightAppColors.background,
    onBackground = LightAppColors.ink,
    surface = LightAppColors.background,
    onSurface = LightAppColors.ink,
    surfaceVariant = LightAppColors.surfaceVariant,
    onSurfaceVariant = LightAppColors.muted,
    surfaceTint = AppPalette.Primary,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = LightAppColors.surface,
    surfaceContainer = LightAppColors.card,
    surfaceContainerHigh = LightAppColors.cardAlt,
    surfaceContainerHighest = LightAppColors.surfaceVariant,
    inverseSurface = Color(0xFF27272A),
    inverseOnSurface = Color(0xFFF4F4F5),
    error = AppPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    outline = Color(0x33000000),
    outlineVariant = LightAppColors.border,
    scrim = Color(0xCC000000),
)

private val DarkScheme = darkColorScheme(
    primary = AppPalette.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    inversePrimary = AppPalette.PrimaryDim,
    secondary = AppPalette.Primary,
    onSecondary = Color.White,
    secondaryContainer = DarkAppColors.surfaceVariant,
    onSecondaryContainer = Color(0xFFE4E4E7),
    tertiary = DarkAppColors.textMuted,
    onTertiary = Color.White,
    background = DarkAppColors.background,
    onBackground = DarkAppColors.ink,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.ink,
    surfaceVariant = DarkAppColors.surfaceVariant,
    onSurfaceVariant = DarkAppColors.muted,
    surfaceTint = AppPalette.Primary,
    surfaceContainerLowest = Color(0xFF0A0A0A),
    surfaceContainerLow = DarkAppColors.surface,
    surfaceContainer = DarkAppColors.card,
    surfaceContainerHigh = DarkAppColors.cardAlt,
    surfaceContainerHighest = DarkAppColors.surfaceVariant,
    inverseSurface = Color(0xFFE4E4E7),
    inverseOnSurface = Color(0xFF18181B),
    error = AppPalette.Danger,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECDD3),
    outline = Color(0x33FFFFFF),
    outlineVariant = DarkAppColors.border,
    scrim = Color(0xCC000000),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

/** 主题：跟随系统 → 用系统深浅色；否则用手动选择 */
@Composable
fun DriverPhoneTheme(content: @Composable () -> Unit) {
    val dark = if (DisplayPrefs.followSystem) isSystemInDarkTheme() else DisplayPrefs.darkMode
    val baseDensity = LocalDensity.current
    val language = AppLanguageStore.language
    CompositionLocalProvider(
        LocalAppColors provides if (dark) DarkAppColors else LightAppColors,
        LocalDensity provides Density(baseDensity.density, AppFontStore.scale),
        LocalStrings provides appStrings(language),
        LocalAppLanguage provides language,
    ) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            shapes = AppShapes,
            content = content,
        )
    }
}
