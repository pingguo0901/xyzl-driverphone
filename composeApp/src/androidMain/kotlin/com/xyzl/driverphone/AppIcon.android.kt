package com.xyzl.driverphone

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

actual fun appIconPainter(): Painter? {
    val context = platformAppContext() ?: return null
    return try {
        val id = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
        if (id == 0) return null
        val drawable = context.getDrawable(id) ?: return null
        val size = 192
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        BitmapPainter(bitmap.asImageBitmap())
    } catch (_: Exception) {
        null
    }
}
