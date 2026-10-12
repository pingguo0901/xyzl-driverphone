package com.xyzl.driverphone.screens

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun currentTimeParts(): Pair<String, String> {
    val now = Date()
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
    return date to time
}
