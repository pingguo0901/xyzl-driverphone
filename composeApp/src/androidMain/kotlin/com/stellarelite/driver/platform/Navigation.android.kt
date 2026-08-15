package com.stellarelite.driver.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

object AppContextHolder {
    var appContext: Context? = null
}

actual fun navigateWithWaze(address: String) {
    val ctx = AppContextHolder.appContext ?: return
    val encoded = java.net.URLEncoder.encode(address, "UTF-8")
    val uri = Uri.parse("https://waze.com/ul?q=$encoded&navigate=yes")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        ctx.startActivity(intent)
    } catch (_: Exception) {
        // Waze 未安装或跳转失败时静默处理
    }
}

actual fun nowDateTimeString(): String {
    return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date())
}
