package com.xyzl.driverphone

import androidx.compose.ui.graphics.painter.Painter

/** 司机端 APP 图标（Android 取真实 launcher icon，iOS 返回 null 由调用方兜底） */
expect fun appIconPainter(): Painter?
