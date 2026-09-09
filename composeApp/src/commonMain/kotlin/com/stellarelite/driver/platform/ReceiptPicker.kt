package com.stellarelite.driver.platform

/** 收据来源：相册 / 相机 / 文件 */
enum class ReceiptSource { Gallery, Camera, File }

/** 选中的收据文件 */
data class PickedReceipt(
    val fileName: String,
    val bytes: ByteArray,
    val mimeType: String
)

/**
 * 打开系统选择器选择收据。
 * @param source 相册 / 相机 / 文件
 * @param onResult 选择结果回调，取消或失败时为 null
 */
expect fun launchReceiptPicker(source: ReceiptSource, onResult: (PickedReceipt?) -> Unit)
