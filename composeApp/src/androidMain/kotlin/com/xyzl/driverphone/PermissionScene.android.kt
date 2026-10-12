package com.xyzl.driverphone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

private var permissionActivity: ComponentActivity? = null
private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

/** 在 Activity.onCreate 里注册，之后各场景按需申请 */
fun initPermissionPlatform(activity: ComponentActivity) {
    permissionActivity = activity
    permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* 拒绝也不阻塞功能 */ }
}

actual fun requestScenePermission(scene: PermissionScene) {
    val activity = permissionActivity ?: return
    val needed = scenePermissions(scene).filter {
        ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
    }
    if (needed.isEmpty()) return
    permissionLauncher?.launch(needed.toTypedArray())
}

/** 场景 → 权限：相机用相机；相册用照片和视频；导航用位置信息；通知用通知；语音用麦克风 */
private fun scenePermissions(scene: PermissionScene): List<String> = when (scene) {
    PermissionScene.Camera -> listOf(Manifest.permission.CAMERA)
    PermissionScene.Photos -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    PermissionScene.Location -> listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    PermissionScene.Notification ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    PermissionScene.Voice -> listOf(Manifest.permission.RECORD_AUDIO)
}
