package com.xyzl.driverphone.model

import kotlin.math.roundToInt

data class Trip(
    val id: String,
    val date: String,
    val time: String,
    val origin: String,
    val destination: String,
    val passenger: String,
    val whatsapp: String,
    val wechat: String,
    val adults: Int,
    val children: Int,
    val luggage: Int,
    val fare: Double,
    val salary: Double,
    val status: TripStatus,
    val pickupTime: String,
    val dropoffTime: String,
)

enum class TripStatus(val label: String) {
    PENDING("未完成"),
    ONGOING("进行中"),
    COMPLETED("完成"),
    CANCELLED("已取消"),
}

/** RM 金额格式化，跨平台（commonMain 无 String.format） */
fun formatRinggit(amount: Double): String {
    val cents = (amount * 100).roundToInt()
    return "RM " + (cents / 100) + "." + (cents % 100).toString().padStart(2, '0')
}
