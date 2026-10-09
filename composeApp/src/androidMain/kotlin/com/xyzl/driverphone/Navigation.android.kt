package com.xyzl.driverphone

import android.content.Context
import android.content.Intent
import android.net.Uri

private lateinit var appContext: Context

fun initNavigationContext(context: Context) {
    appContext = context.applicationContext
}

actual fun openNavigation(app: NavigationApp, address: String) {
    val encoded = Uri.encode(address)
    val uri = when (app) {
        NavigationApp.WAZE -> Uri.parse("https://waze.com/ul?q=$encoded")
        NavigationApp.GOOGLE_MAPS -> Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}
