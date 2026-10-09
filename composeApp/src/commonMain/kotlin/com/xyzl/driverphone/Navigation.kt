package com.xyzl.driverphone

/** 导航 App 选择 */
enum class NavigationApp { WAZE, GOOGLE_MAPS }

/** 打开指定导航 App 跳转到目标地址（平台各自实现） */
expect fun openNavigation(app: NavigationApp, address: String)
