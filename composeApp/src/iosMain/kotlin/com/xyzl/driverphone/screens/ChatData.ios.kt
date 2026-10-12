package com.xyzl.driverphone.screens

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun currentTimeParts(): Pair<String, String> {
    val fmt = NSDateFormatter()
    fmt.dateFormat = "yyyy-MM-dd"
    val date = fmt.stringFromDate(NSDate())
    fmt.dateFormat = "HH:mm"
    val time = fmt.stringFromDate(NSDate())
    return date to time
}
