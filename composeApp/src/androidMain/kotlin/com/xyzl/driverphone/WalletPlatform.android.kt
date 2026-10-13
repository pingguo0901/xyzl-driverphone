package com.xyzl.driverphone

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File

actual fun saveQrToGallery(text: String): Boolean {
    val context = platformAppContext() ?: return false
    return try {
        val size = 720
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)

        val name = "xyzl_qr_" + System.currentTimeMillis() + ".png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/XYZL")
            } else {
                @Suppress("DEPRECATION")
                put(
                    MediaStore.Images.Media.DATA,
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name).absolutePath,
                )
            }
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } ?: return false
        true
    } catch (_: Exception) {
        false
    }
}

actual fun saveStatementPdf(fileName: String, content: String): Boolean {
    val context = platformAppContext() ?: return false
    val doc = PdfDocument()
    return try {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val lineHeight = 16f
        val bodySize = 11f
        val paint = Paint().apply {
            this.textSize = bodySize
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }
        val lines = content.split("\n").ifEmpty { listOf("") }
        var index = 0
        var pageNumber = 1
        while (index < lines.size) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            var y = margin + bodySize
            while (index < lines.size && y < pageHeight - margin) {
                page.canvas.drawText(lines[index], margin, y, paint)
                y += lineHeight
                index++
            }
            doc.finishPage(page)
            pageNumber++
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/XYZL")
            } else {
                @Suppress("DEPRECATION")
                put(
                    MediaStore.MediaColumns.DATA,
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName).absolutePath,
                )
            }
        }
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        val uri = resolver.insert(collection, values) ?: return false
        resolver.openOutputStream(uri)?.use { out ->
            doc.writeTo(out)
            out.flush()
        } ?: return false
        true
    } catch (_: Exception) {
        false
    } finally {
        doc.close()
    }
}
