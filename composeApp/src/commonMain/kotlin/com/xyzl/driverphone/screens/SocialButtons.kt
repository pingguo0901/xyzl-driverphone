package com.xyzl.driverphone.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// 通用圆形按钮容器
// ============================================================
@Composable
fun SocialCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = Color.White,
    backgroundBrush: Brush? = null,
    borderColor: Color = Color(0xFFE0E0E0),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (backgroundBrush != null) Modifier.background(backgroundBrush)
                else Modifier.background(backgroundColor),
            )
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

// ============================================================
// WhatsApp
// ============================================================
@Composable
fun WhatsAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFF25D366),
        borderColor = Color.Transparent,
    ) {
        WhatsAppLogo(logoSize)
    }
}

@Composable
private fun WhatsAppLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        val bubble = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            cubicTo(w * 0.2f, h * 0.12f, w * 0.05f, h * 0.35f, w * 0.05f, h * 0.55f)
            cubicTo(w * 0.05f, h * 0.78f, w * 0.22f, h * 0.92f, w * 0.5f, h * 0.92f)
            cubicTo(w * 0.78f, h * 0.92f, w * 0.95f, h * 0.78f, w * 0.95f, h * 0.55f)
            cubicTo(w * 0.95f, h * 0.35f, w * 0.8f, h * 0.12f, w * 0.5f, h * 0.12f)
            close()
        }
        drawPath(bubble, Color.White, style = Stroke(width = w * 0.07f))
        val phone = Path().apply {
            moveTo(w * 0.32f, h * 0.35f)
            cubicTo(w * 0.36f, h * 0.3f, w * 0.42f, h * 0.3f, w * 0.46f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.46f)
            lineTo(w * 0.45f, h * 0.52f)
            cubicTo(w * 0.5f, h * 0.62f, w * 0.6f, h * 0.72f, w * 0.7f, h * 0.77f)
            lineTo(w * 0.76f, h * 0.72f)
            lineTo(w * 0.84f, h * 0.78f)
            cubicTo(w * 0.8f, h * 0.88f, w * 0.7f, h * 0.88f, w * 0.6f, h * 0.82f)
            cubicTo(w * 0.45f, h * 0.72f, w * 0.3f, h * 0.55f, w * 0.22f, h * 0.42f)
            cubicTo(w * 0.18f, h * 0.34f, w * 0.22f, h * 0.28f, w * 0.32f, h * 0.35f)
            close()
        }
        drawPath(phone, Color.White)
    }
}

// ============================================================
// WeChat
// ============================================================
@Composable
fun WeChatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFF07C160),
        borderColor = Color.Transparent,
    ) {
        WeChatLogo(logoSize)
    }
}

@Composable
private fun WeChatLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        drawOval(
            color = Color.White,
            topLeft = Offset(w * 0.08f, h * 0.15f),
            size = Size(w * 0.55f, h * 0.5f),
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(w * 0.38f, h * 0.42f),
            size = Size(w * 0.55f, h * 0.45f),
        )
        drawCircle(Color(0xFF07C160), radius = w * 0.04f, center = Offset(w * 0.25f, h * 0.4f))
        drawCircle(Color(0xFF07C160), radius = w * 0.04f, center = Offset(w * 0.42f, h * 0.4f))
        drawCircle(Color(0xFF07C160), radius = w * 0.035f, center = Offset(w * 0.55f, h * 0.62f))
        drawCircle(Color(0xFF07C160), radius = w * 0.035f, center = Offset(w * 0.72f, h * 0.62f))
    }
}

// ============================================================
// Apple
// ============================================================
@Composable
fun AppleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFFF7F7F9),
        borderColor = Color(0x1A000000),
    ) {
        AppleLogo(logoSize)
    }
}

@Composable
private fun AppleLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        val apple = Path().apply {
            moveTo(w * 0.5f, h * 0.32f)
            cubicTo(w * 0.25f, h * 0.25f, w * 0.15f, h * 0.45f, w * 0.18f, h * 0.65f)
            cubicTo(w * 0.22f, h * 0.88f, w * 0.4f, h * 0.92f, w * 0.5f, h * 0.85f)
            cubicTo(w * 0.6f, h * 0.92f, w * 0.78f, h * 0.88f, w * 0.82f, h * 0.65f)
            cubicTo(w * 0.85f, h * 0.45f, w * 0.75f, h * 0.25f, w * 0.5f, h * 0.32f)
            close()
        }
        drawPath(apple, Color.White)
        val leaf = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.45f, h * 0.15f, w * 0.55f, h * 0.08f, w * 0.65f, h * 0.1f)
            cubicTo(w * 0.62f, h * 0.22f, w * 0.55f, h * 0.3f, w * 0.5f, h * 0.3f)
            close()
        }
        drawPath(leaf, Color.White)
        drawCircle(Color(0xFFF7F7F9), radius = w * 0.12f, center = Offset(w * 0.78f, h * 0.5f))
    }
}

// ============================================================
// Facebook
// ============================================================
@Composable
fun FacebookButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFF1877F2),
        borderColor = Color.Transparent,
    ) {
        FacebookLogo()
    }
}

@Composable
private fun FacebookLogo() {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "f",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ============================================================
// Email
// ============================================================
@Composable
fun EmailButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFFEA4335),
        borderColor = Color.Transparent,
    ) {
        EmailLogo(logoSize)
    }
}

@Composable
private fun EmailLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.1f, h * 0.25f),
            size = Size(w * 0.8f, h * 0.5f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
        )
        val line = Path().apply {
            moveTo(w * 0.1f, h * 0.25f)
            lineTo(w * 0.5f, h * 0.55f)
            lineTo(w * 0.9f, h * 0.25f)
        }
        drawPath(line, Color(0xFFEA4335), style = Stroke(width = w * 0.06f, cap = StrokeCap.Round))
    }
}

// ============================================================
// Instagram
// ============================================================
@Composable
fun InstagramButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF58529),
                Color(0xFFDD2A7B),
                Color(0xFF8134AF),
                Color(0xFF515BD4),
            ),
        ),
        borderColor = Color.Transparent,
    ) {
        InstagramLogo(logoSize)
    }
}

@Composable
private fun InstagramLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.1f, h * 0.1f),
            size = Size(w * 0.8f, h * 0.8f),
            cornerRadius = CornerRadius(w * 0.2f, w * 0.2f),
            style = Stroke(width = w * 0.07f),
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = w * 0.07f),
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.05f,
            center = Offset(w * 0.75f, h * 0.25f),
        )
    }
}

// ============================================================
// 一行组合（可选）
// ============================================================
@Composable
fun SocialLoginRow(
    onWhatsApp: () -> Unit,
    onWeChat: () -> Unit,
    onApple: () -> Unit,
    onFacebook: () -> Unit,
    onEmail: () -> Unit,
    onInstagram: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WhatsAppButton(onClick = onWhatsApp, size = 44.dp)
        WeChatButton(onClick = onWeChat, size = 44.dp)
        AppleButton(onClick = onApple, size = 44.dp)
        FacebookButton(onClick = onFacebook, size = 44.dp)
        EmailButton(onClick = onEmail, size = 44.dp)
        InstagramButton(onClick = onInstagram, size = 44.dp)
    }
}
