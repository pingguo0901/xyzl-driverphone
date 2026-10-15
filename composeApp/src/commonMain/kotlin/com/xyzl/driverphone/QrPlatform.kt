package com.xyzl.driverphone

/** 手电筒开关，返回是否真的执行成功 */
expect fun setTorchEnabled(enabled: Boolean): Boolean

/** 调用系统分享面板 */
expect fun shareText(text: String)

/** 从本地图片 uri 识别二维码，未识别返回 null */
expect fun decodeQrFromImage(uri: String): String?

/** 生成二维码模块矩阵（每行一个字符串，'1' = 黑块）；不支持返回 null */
expect fun qrMatrixFor(text: String): List<String>?
