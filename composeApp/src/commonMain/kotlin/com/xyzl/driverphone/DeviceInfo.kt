package com.xyzl.driverphone

/**
 * 当前设备信息（登录设备管理 se_user_devices 用）。
 * Android / iOS 各自 actual 实现；device_id 首次生成后持久化，之后一直复用。
 */
expect object DeviceInfo {
    fun deviceId(): String
    fun deviceName(): String
    fun deviceModel(): String
    fun deviceType(): String   // phone / tablet
    fun osName(): String       // Android / iOS
    fun osVersion(): String
    fun appVersion(): String
}
