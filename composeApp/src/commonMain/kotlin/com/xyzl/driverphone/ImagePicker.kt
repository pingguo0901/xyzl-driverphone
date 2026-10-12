package com.xyzl.driverphone

import androidx.compose.ui.graphics.ImageBitmap

/** 图片来源 */
enum class ImageSource { Camera, Gallery }

/** 选择生日日期（原生日历），回调 yyyy-MM-dd */
expect fun pickBirthday(onPicked: (String) -> Unit)

/** 选择图片（拍照 / 相册），回调本地 uri 字符串 */
expect fun pickImage(source: ImageSource, onPicked: (String) -> Unit)

/** 选择文档（PDF / 图片等），回调本地 uri 字符串 */
expect fun pickDocument(onPicked: (String) -> Unit)

/** 读取本地 uri 的图片（用于头像预览），失败返回 null */
expect fun loadImageBitmap(uri: String): ImageBitmap?
