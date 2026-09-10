package com.stellarelite.driver.platform

import androidx.compose.ui.graphics.ImageBitmap

/** 把图片字节解码为可显示的 ImageBitmap，失败返回 null */
expect fun decodeImage(bytes: ByteArray): ImageBitmap?
