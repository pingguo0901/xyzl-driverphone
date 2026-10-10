package com.xyzl.driverphone

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * 联系乘客按钮的品牌 Logo（WhatsApp / 微信）。
 * Android 使用真实品牌图片资源，iOS 回退到矢量 Logo。
 */
@Composable
expect fun brandLogoPainter(isWhatsApp: Boolean): Painter

/**
 * 登录/注册入口页背景图。
 * Android 使用真实图片资源，iOS 回退到纯色渐变。
 */
@Composable
expect fun entryBackgroundPainter(): Painter?
