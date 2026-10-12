package com.xyzl.driverphone.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

/** 司机资料（本机保存）：昵称 + ID；司机没自定义前自动生成随机唯一 ID */
object DriverProfile {
    var nickname by mutableStateOf("陈师傅")
    var driverId by mutableStateOf(newDriverId())
    const val company = "星域臻旅"
}

/** 随机唯一 ID：XY-D- + 8 位随机数字 */
fun newDriverId(): String {
    val digits = StringBuilder()
    repeat(8) { digits.append(Random.nextInt(0, 10)) }
    return "XY-D-" + digits.toString()
}
