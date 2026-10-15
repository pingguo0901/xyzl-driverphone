package com.xyzl.driverphone

actual fun setTorchEnabled(enabled: Boolean): Boolean {
    // iOS 端暂未接入手电筒控制，留空
    return false
}

actual fun shareText(text: String) {
    // iOS 端暂未接入系统分享，留空
}

actual fun decodeQrFromImage(uri: String): String? {
    // iOS 端暂未接入二维码识别，留空
    return null
}

actual fun qrMatrixFor(text: String): List<String>? {
    // iOS 端暂未接入二维码生成，留空
    return null
}
