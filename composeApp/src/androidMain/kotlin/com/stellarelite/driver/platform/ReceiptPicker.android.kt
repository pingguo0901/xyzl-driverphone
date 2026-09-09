package com.stellarelite.driver.platform

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File

/**
 * 收据选择器（Android）
 * 相册/文件：通过 content resolver 读取字节
 * 相机：写入缓存文件后读取
 */
object ReceiptPickerHolder {
    var activity: Activity? = null
    var pendingCallback: ((PickedReceipt?) -> Unit)? = null
    var pendingSource: ReceiptSource? = null
    var cameraFile: File? = null

    const val RC_CAMERA = 7101
    const val RC_GALLERY = 7102
    const val RC_FILE = 7103

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val cb = pendingCallback ?: return
        val source = pendingSource
        pendingCallback = null
        pendingSource = null

        if (resultCode != Activity.RESULT_OK) {
            cb(null)
            return
        }

        try {
            when (source) {
                ReceiptSource.Camera -> {
                    val f = cameraFile
                    cameraFile = null
                    if (f == null || !f.exists()) { cb(null); return }
                    cb(PickedReceipt(f.name, f.readBytes(), "image/jpeg"))
                }
                ReceiptSource.Gallery, ReceiptSource.File -> {
                    val uri = data?.data
                    val act = activity
                    if (uri == null || act == null) { cb(null); return }
                    val bytes = act.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes == null) { cb(null); return }
                    val (name, mime) = queryNameAndMime(act, uri)
                    cb(PickedReceipt(name, bytes, mime))
                }
                else -> cb(null)
            }
        } catch (_: Exception) {
            cb(null)
        }
    }

    private fun queryNameAndMime(act: Activity, uri: Uri): Pair<String, String> {
        var name = "receipt"
        var mime = act.contentResolver.getType(uri) ?: "application/octet-stream"
        try {
            act.contentResolver.query(uri, null, null, null, null)?.use { c ->
                if (c.moveToFirst()) {
                    val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) name = c.getString(idx) ?: name
                }
            }
        } catch (_: Exception) { }
        return name to mime
    }
}

actual fun launchReceiptPicker(source: ReceiptSource, onResult: (PickedReceipt?) -> Unit) {
    val act = ReceiptPickerHolder.activity
    if (act == null) { onResult(null); return }

    ReceiptPickerHolder.pendingCallback = onResult
    ReceiptPickerHolder.pendingSource = source

    try {
        when (source) {
            ReceiptSource.Camera -> {
                val file = File.createTempFile("receipt_", ".jpg", act.cacheDir)
                ReceiptPickerHolder.cameraFile = file
                val uri = FileProvider.getUriForFile(
                    act,
                    "${act.packageName}.fileprovider",
                    file
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                act.startActivityForResult(intent, ReceiptPickerHolder.RC_CAMERA)
            }
            ReceiptSource.Gallery -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                act.startActivityForResult(intent, ReceiptPickerHolder.RC_GALLERY)
            }
            ReceiptSource.File -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                }
                act.startActivityForResult(intent, ReceiptPickerHolder.RC_FILE)
            }
        }
    } catch (_: Exception) {
        ReceiptPickerHolder.pendingCallback = null
        ReceiptPickerHolder.pendingSource = null
        onResult(null)
    }
}
