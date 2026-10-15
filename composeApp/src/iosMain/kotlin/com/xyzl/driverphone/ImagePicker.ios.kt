package com.xyzl.driverphone

import androidx.compose.ui.graphics.ImageBitmap

actual fun pickBirthday(onPicked: (String) -> Unit) {
    // iOS 端暂未接入原生日历，留空
}

actual fun pickImage(source: ImageSource, onPicked: (String) -> Unit) {
    // iOS 端暂未接入相机/相册，留空
}

actual fun pickDocument(onPicked: (String) -> Unit) {
    // iOS 端暂未接入文档选择，留空
}

actual fun loadImageBitmap(uri: String): ImageBitmap? {
    // iOS 端暂未接入图片读取，留空
    return null
}

/** iOS 端暂未实现：KYC 证件照上传先返回 null */
actual suspend fun readUriBytes(uri: String): ByteArray? = null
