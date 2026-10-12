package com.xyzl.driverphone

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

private var qrContext: Context? = null

/** 在 MainActivity.onCreate 里调用 */
fun initQrPlatform(context: Context) {
    qrContext = context.applicationContext
}

/** 供其他 androidMain actual 复用宿主 application context */
internal fun platformAppContext(): Context? = qrContext

actual fun setTorchEnabled(enabled: Boolean): Boolean {
    val context = qrContext ?: return false
    return try {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return false
        val cameraId = manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return false
        manager.setTorchMode(cameraId, enabled)
        true
    } catch (_: Exception) {
        false
    }
}

actual fun shareText(text: String) {
    val context = qrContext ?: return
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(send, "分享").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
}

actual fun decodeQrFromImage(uri: String): String? {
    val context = qrContext ?: return null
    return try {
        val parsed = Uri.parse(uri)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(parsed)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        var sample = 1
        val longest = maxOf(bounds.outWidth, bounds.outHeight)
        while (longest / (sample * 2) >= 1200) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = context.contentResolver.openInputStream(parsed)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: return null

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pixels)
        val binary = BinaryBitmap(HybridBinarizer(source))
        val hints = mapOf(DecodeHintType.TRY_HARDER to true)
        try {
            MultiFormatReader().decode(binary, hints).text
        } catch (_: NotFoundException) {
            null
        }
    } catch (_: Exception) {
        null
    }
}
