package com.xyzl.driverphone

/** 把二维码（由 text 生成）保存到手机相册，返回是否成功 */
expect fun saveQrToGallery(text: String): Boolean

/** 生成 e-statement PDF 文档并保存到手机（下载目录），返回是否成功 */
expect fun saveStatementPdf(fileName: String, content: String): Boolean
