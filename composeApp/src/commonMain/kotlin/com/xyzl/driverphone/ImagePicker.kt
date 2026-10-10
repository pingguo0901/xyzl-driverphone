package com.xyzl.driverphone

/** 图片来源 */
enum class ImageSource { Camera, Gallery }

/** 选择生日日期（原生日历），回调 yyyy-MM-dd */
expect fun pickBirthday(onPicked: (String) -> Unit)

/** 选择图片（拍照 / 相册），回调本地 uri 字符串 */
expect fun pickImage(source: ImageSource, onPicked: (String) -> Unit)
