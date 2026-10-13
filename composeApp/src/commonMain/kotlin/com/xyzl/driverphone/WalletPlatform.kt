package com.xyzl.driverphone

/** 把二维码（由 text 生成）保存到手机相册，返回是否成功 */
expect fun saveQrToGallery(text: String): Boolean
