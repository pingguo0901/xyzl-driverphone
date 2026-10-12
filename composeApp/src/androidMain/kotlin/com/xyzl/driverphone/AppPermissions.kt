package com.xyzl.driverphone

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * 司机端需要原生获取的权限集合（按系统版本过滤后逐项申请）。
 * 电话 / 附近的设备 / 麦克风 / 通话记录 / 通讯录 / 通知 / 相机 /
 * 音乐和音频 / 照片和视频 / 位置信息 / 短信
 */
object AppPermissions {

    /** 当前系统版本下需要申请的全部权限（含已授权的） */
    fun all(): List<String> {
        val list = mutableListOf(
            // 电话
            Manifest.permission.READ_PHONE_STATE,
            // 麦克风
            Manifest.permission.RECORD_AUDIO,
            // 通话记录
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            // 通讯录
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            // 相机
            Manifest.permission.CAMERA,
            // 位置信息
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            // 短信
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
        )

        // 电话（新版）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            list += Manifest.permission.READ_PHONE_NUMBERS
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            list += Manifest.permission.ANSWER_PHONE_CALLS
        }

        // 附近的设备（蓝牙）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list += Manifest.permission.BLUETOOTH_SCAN
            list += Manifest.permission.BLUETOOTH_CONNECT
            list += Manifest.permission.BLUETOOTH_ADVERTISE
        } else {
            list += Manifest.permission.BLUETOOTH
            list += Manifest.permission.BLUETOOTH_ADMIN
        }

        // 通知（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list += Manifest.permission.POST_NOTIFICATIONS
            // 音乐和音频
            list += Manifest.permission.READ_MEDIA_AUDIO
            // 照片和视频
            list += Manifest.permission.READ_MEDIA_IMAGES
            list += Manifest.permission.READ_MEDIA_VIDEO
        } else {
            list += Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            list += Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        }

        return list
    }

    /** 还没授权的权限 */
    fun missing(context: Context): List<String> = all().filter { permission ->
        ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    /** 是否全部已授权 */
    fun allGranted(context: Context): Boolean = missing(context).isEmpty()
}
