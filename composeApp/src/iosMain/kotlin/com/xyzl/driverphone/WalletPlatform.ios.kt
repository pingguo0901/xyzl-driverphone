package com.xyzl.driverphone

actual fun saveQrToGallery(text: String): Boolean {
    // iOS 端暂未接入保存到相册，留空
    return false
}

actual fun saveStatementPdf(fileName: String, content: String): Boolean {
    // iOS 端暂未接入保存文档，留空
    return false
}
