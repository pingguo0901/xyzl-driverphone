package com.stellarelite.driver.platform

import platform.Foundation.NSURL
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.UIKit.UIApplication

actual fun navigateWithWaze(address: String) {
    val encoded = address.stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLQueryAllowedCharacterSet
    ) ?: address
    val url = NSURL(string = "https://waze.com/ul?q=$encoded&navigate=yes")
    url?.let { UIApplication.sharedApplication.openURL(it) }
}

actual fun nowDateTimeString(): String {
    val formatter = NSDateFormatter().apply { dateFormat = "yyyy-MM-dd HH:mm" }
    return formatter.stringFromDate(NSDate())
}
