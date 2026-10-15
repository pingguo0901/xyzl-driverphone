package com.xyzl.driverphone

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.io.File

private var mediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
private var pendingMediaCallback: ((List<String>) -> Unit)? = null
private var activityRef: ComponentActivity? = null
private var recorder: MediaRecorder? = null
private var recordFile: File? = null
private var player: MediaPlayer? = null

/** 在 MainActivity.onCreate 里调用 */
fun initChatPlatform(activity: ComponentActivity) {
    activityRef = activity
    mediaLauncher = activity.registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10),
    ) { uris ->
        pendingMediaCallback?.invoke(uris.map { it.toString() })
        pendingMediaCallback = null
    }
}

actual fun pickMediaMultiple(max: Int, onPicked: (List<String>) -> Unit) {
    val launcher = mediaLauncher
    if (launcher == null) {
        onPicked(emptyList())
        return
    }
    pendingMediaCallback = onPicked
    launcher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
    )
}

actual fun currentLocation(onResult: (String?) -> Unit) {
    val activity = activityRef ?: run { onResult(null); return }
    val granted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    if (!granted) {
        onResult(null)
        return
    }
    try {
        val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        onResult(loc?.let { it.latitude.toString() + "," + it.longitude })
    } catch (e: Exception) {
        onResult(null)
    }
}

actual fun startVoiceRecord(): Boolean {
    val activity = activityRef ?: return false
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    return try {
        val file = File(activity.cacheDir, "voice_" + System.currentTimeMillis() + ".m4a")
        recordFile = file
        val rec = if (android.os.Build.VERSION.SDK_INT >= 31) {
            MediaRecorder(activity)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        rec.setAudioSource(MediaRecorder.AudioSource.MIC)
        rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        rec.setOutputFile(file.absolutePath)
        rec.prepare()
        rec.start()
        recorder = rec
        true
    } catch (e: Exception) {
        recorder = null
        recordFile = null
        false
    }
}

actual fun stopVoiceRecord(onResult: (String?) -> Unit) {
    val rec = recorder
    val file = recordFile
    recorder = null
    recordFile = null
    if (rec == null || file == null) {
        onResult(null)
        return
    }
    try {
        rec.stop()
        rec.release()
        onResult(file.absolutePath)
    } catch (e: Exception) {
        try { rec.release() } catch (_: Exception) {}
        onResult(null)
    }
}

actual fun readFileBytes(uri: String): ByteArray? {
    val parsed = Uri.parse(uri)
    return try {
        // 录音等直接给文件路径，没有 scheme
        if (parsed.scheme == null) {
            File(uri).readBytes()
        } else {
            val activity = activityRef ?: return null
            activity.contentResolver.openInputStream(parsed)?.use { it.readBytes() }
        }
    } catch (e: Exception) {
        null
    }
}

actual fun playVoice(path: String) {
    stopVoicePlay()
    try {
        val mp = MediaPlayer()
        mp.setDataSource(path)
        mp.prepare()
        mp.start()
        player = mp
    } catch (e: Exception) {
        player = null
    }
}

actual fun stopVoicePlay() {
    val mp = player ?: return
    player = null
    try {
        if (mp.isPlaying) mp.stop()
        mp.release()
    } catch (_: Exception) {
    }
}
