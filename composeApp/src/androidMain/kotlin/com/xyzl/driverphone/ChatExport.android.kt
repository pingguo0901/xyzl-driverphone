package com.xyzl.driverphone

import android.content.ContentValues
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun exportChatRecordsPdf(lines: List<String>): String? {
    val context = platformAppContext() ?: return null
    return try {
        val paint = Paint().apply { textSize = 10f; isAntiAlias = true }
        val margin = 40f
        val lineHeight = 15f
        val pageWidth = 595
        val pageHeight = 842
        val perLine = 82

        val wrapped = mutableListOf<String>()
        lines.forEach { raw ->
            val text = raw.replace("\n", " ")
            if (text.length <= perLine) wrapped += text
            else text.chunked(perLine).forEach { wrapped += it }
        }

        val document = PdfDocument()
        var pageNo = 1
        var index = 0
        while (index < wrapped.size || wrapped.isEmpty()) {
            val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNo).create())
            val canvas = page.canvas
            var y = margin + 10f
            canvas.drawText("XYZL Chat Records  " + SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()), margin, y, paint)
            y += lineHeight * 2
            while (index < wrapped.size && y < pageHeight - margin) {
                canvas.drawText(wrapped[index], margin, y, paint)
                y += lineHeight
                index++
            }
            document.finishPage(page)
            pageNo++
            if (wrapped.isEmpty()) break
        }

        val name = "xyzl-chat-records-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date()) + ".pdf"
        val location: String
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("无法创建下载文件")
            context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
                ?: throw IllegalStateException("无法写入下载文件")
            location = "Download/" + name
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            val file = File(dir, name)
            FileOutputStream(file).use { document.writeTo(it) }
            location = file.absolutePath
        }
        document.close()

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "已保存到下载目录", Toast.LENGTH_SHORT).show()
        }
        location
    } catch (e: Exception) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "导出失败：" + (e.message ?: "未知错误"), Toast.LENGTH_SHORT).show()
        }
        null
    }
}
