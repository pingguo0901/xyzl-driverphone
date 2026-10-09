package com.xyzl.driverphone

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun brandLogoPainter(isWhatsApp: Boolean): Painter =
    painterResource(
        if (isWhatsApp) R.drawable.whatsapp_logo else R.drawable.wechat_logo,
    )
