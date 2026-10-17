package com.xyzl.driverphone

import com.russhwolf.settings.Settings
import platform.Foundation.NSBundle
import platform.Foundation.NSUUID
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

actual object DeviceInfo {

    private const val KEY_DEVICE_ID = "xyzl_device_id"
    private val settings: Settings by lazy { Settings() }

    actual fun deviceId(): String {
        val saved = settings.getStringOrNull(KEY_DEVICE_ID)
        if (!saved.isNullOrBlank()) return saved
        val id = NSUUID().UUIDString
        settings.putString(KEY_DEVICE_ID, id)
        return id
    }

    actual fun deviceName(): String = UIDevice.currentDevice.name

    actual fun deviceModel(): String = UIDevice.currentDevice.model

    actual fun deviceType(): String =
        if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) "tablet" else "phone"

    actual fun osName(): String = "iOS"

    actual fun osVersion(): String = UIDevice.currentDevice.systemVersion

    actual fun appVersion(): String =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String) ?: ""
}
