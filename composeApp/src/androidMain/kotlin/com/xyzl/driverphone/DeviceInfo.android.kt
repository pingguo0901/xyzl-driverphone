package com.xyzl.driverphone

import android.content.res.Configuration
import android.os.Build
import com.russhwolf.settings.Settings
import java.util.UUID

actual object DeviceInfo {

    private const val KEY_DEVICE_ID = "xyzl_device_id"
    private val settings: Settings by lazy { Settings() }

    actual fun deviceId(): String {
        val saved = settings.getStringOrNull(KEY_DEVICE_ID)
        if (!saved.isNullOrBlank()) return saved
        val id = UUID.randomUUID().toString()
        settings.putString(KEY_DEVICE_ID, id)
        return id
    }

    actual fun deviceName(): String = Build.MODEL?.takeIf { it.isNotBlank() } ?: "Android"

    actual fun deviceModel(): String = Build.MODEL ?: ""

    actual fun deviceType(): String {
        val context = platformAppContext() ?: return "phone"
        val size = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        return if (size >= Configuration.SCREENLAYOUT_SIZE_LARGE) "tablet" else "phone"
    }

    actual fun osName(): String = "Android"

    actual fun osVersion(): String = Build.VERSION.RELEASE ?: ""

    actual fun appVersion(): String {
        val context = platformAppContext() ?: return ""
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}
