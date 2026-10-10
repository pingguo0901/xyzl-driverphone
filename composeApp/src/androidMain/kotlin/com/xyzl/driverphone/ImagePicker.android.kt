package com.xyzl.driverphone

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private var birthdayLauncher: ActivityResultLauncher<Intent>? = null
private var cameraLauncher: ActivityResultLauncher<Uri>? = null
private var galleryLauncher: ActivityResultLauncher<String>? = null
private var pendingCameraUri: Uri? = null
private var pendingCameraCallback: ((String) -> Unit)? = null
private var pendingGalleryCallback: ((String) -> Unit)? = null

private lateinit var pickerActivity: ComponentActivity

/** 在 MainActivity.onCreate 里调用，注册 ActivityResult 回调 */
fun initImagePicker(activity: ComponentActivity) {
    pickerActivity = activity

    birthdayLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val year = data?.getIntExtra("year", 0) ?: 0
            val month = data?.getIntExtra("month", 0) ?: 0
            val day = data?.getIntExtra("day", 0) ?: 0
            if (year > 0) {
                pendingBirthdayCallback?.invoke(formatBirthday(year, month + 1, day))
            }
        }
        pendingBirthdayCallback = null
    }

    cameraLauncher = activity.registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) pendingCameraUri?.let { pendingCameraCallback?.invoke(it.toString()) }
        pendingCameraCallback = null
        pendingCameraUri = null
    }

    galleryLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let { pendingGalleryCallback?.invoke(it.toString()) }
        pendingGalleryCallback = null
    }
}

private var pendingBirthdayCallback: ((String) -> Unit)? = null

actual fun pickBirthday(onPicked: (String) -> Unit) {
    pendingBirthdayCallback = onPicked
    val cal = Calendar.getInstance()
    val dialog = DatePickerDialog(
        pickerActivity,
        { _, y, m, d -> onPicked(formatBirthday(y, m + 1, d)) },
        cal.get(Calendar.YEAR) - 20,
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH),
    )
    dialog.datePicker.maxDate = System.currentTimeMillis()
    dialog.show()
}

actual fun pickImage(source: ImageSource, onPicked: (String) -> Unit) {
    when (source) {
        ImageSource.Camera -> {
            val uri = createImageUri(pickerActivity)
            pendingCameraUri = uri
            pendingCameraCallback = onPicked
            cameraLauncher?.launch(uri)
        }
        ImageSource.Gallery -> {
            pendingGalleryCallback = onPicked
            galleryLauncher?.launch("image/*")
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "kyc").apply { mkdirs() }
    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val file = File(dir, "IMG_$stamp.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
