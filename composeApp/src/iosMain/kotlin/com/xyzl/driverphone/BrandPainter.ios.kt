package com.xyzl.driverphone

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
actual fun brandLogoPainter(isWhatsApp: Boolean): Painter =
    rememberVectorPainter(if (isWhatsApp) WhatsAppLogo else WeChatLogo)
