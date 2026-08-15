package com.stellarelite.driver.platform

/** 用 Waze 打开导航（按地址） */
expect fun navigateWithWaze(address: String)

/** 当前日期时间字符串（yyyy-MM-dd HH:mm） */
expect fun nowDateTimeString(): String
