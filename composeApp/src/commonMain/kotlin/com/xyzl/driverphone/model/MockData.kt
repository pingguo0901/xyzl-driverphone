package com.xyzl.driverphone.model

object MockData {
    val trips = listOf(
        Trip("T001", "吉隆坡国际机场", "武吉免登", "张先生", 86.00, 58.4, "14:30", TripStatus.ONGOING),
        Trip("T002", "双子塔", "谷中城", "李女士", 24.50, 12.8, "15:00", TripStatus.PENDING),
        Trip("T003", "中央车站", "黑风洞", "王先生", 32.00, 18.2, "15:20", TripStatus.PENDING),
        Trip("T004", "柏威年广场", "蒲种", "林女士", 41.80, 26.5, "昨天 18:40", TripStatus.COMPLETED),
        Trip("T005", "茨厂街", "孟沙", "陈先生", 18.60, 9.3, "昨天 16:10", TripStatus.COMPLETED),
        Trip("T006", "武吉加里尔", "沙登", "黄女士", 27.40, 15.7, "昨天 13:55", TripStatus.COMPLETED),
        Trip("T007", "双威金字塔", "梳邦", "吴先生", 35.20, 21.0, "前天 20:15", TripStatus.CANCELLED),
    )
}
