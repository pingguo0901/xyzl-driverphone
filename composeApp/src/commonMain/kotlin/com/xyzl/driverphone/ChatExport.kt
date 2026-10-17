package com.xyzl.driverphone

/** 导出聊天记录为 PDF：Android 写 Download，返回保存位置；失败返回 null */
expect fun exportChatRecordsPdf(lines: List<String>): String?
