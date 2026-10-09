package com.xyzl.driverphone

import platform.Foundation.NSURL
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.NSCharacterSet
import platform.UIKit.UIApplication

actual fun openNavigation(app: NavigationApp, address: String) {
    val encoded = address.stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLQueryAllowedCharacterSet
    ) ?: address
    val urlString = when (app) {
        NavigationApp.WAZE -> "https://waze.com/ul?q=$encoded"
        NavigationApp.GOOGLE_MAPS -> "https://www.google.com/maps/search/?api=1&query=$encoded"
    }
    NSURL.URLWithString(urlString)?.let { url ->
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun openUrl(url: String) {
    NSURL.URLWithString(url)?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
