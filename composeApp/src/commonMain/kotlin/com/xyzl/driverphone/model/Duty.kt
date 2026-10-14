package com.xyzl.driverphone.model

import kotlin.math.roundToInt

/** 一条值班记录（每日结算） */
data class DutyRecord(
    val id: String,
    val date: String,          // 14/10/2026
    val salary: Double,        // 今日薪资 (RM)
    val reimbursement: Double, // 今日报销 (RM)
    val orders: Int,           // 今日单数
    val collectSgd: Double,    // 今日代收 SGD
    val collectMyr: Double,    // 今日代收 MYR
)

/** 值班汇总：休息额度 / 跨境额度 */
data class DutySummary(
    val restUsed: Int,
    val restRemaining: Int,
    val crossUsed: Int,
    val crossRemaining: Int,
)

object DutyData {
    val summary = DutySummary(
        restUsed = 3,
        restRemaining = 3,
        crossUsed = 8,
        crossRemaining = 7,
    )

    val records = listOf(
        DutyRecord("D001", "14/10/2026", 120.00, 18.50, 6, 320.00, 450.00),
        DutyRecord("D002", "13/10/2026", 135.00, 0.00, 7, 210.00, 620.00),
        DutyRecord("D003", "12/10/2026", 98.00, 25.00, 5, 180.00, 300.00),
        DutyRecord("D004", "11/10/2026", 150.00, 12.00, 8, 540.00, 210.00),
        DutyRecord("D005", "10/10/2026", 110.00, 0.00, 6, 0.00, 780.00),
        DutyRecord("D006", "09/10/2026", 126.00, 8.00, 7, 260.00, 410.00),
    )
}

/** SGD 金额格式化，跨平台（commonMain 无 String.format） */
fun formatSgd(amount: Double): String {
    val cents = (amount * 100).roundToInt()
    return "S\$ " + (cents / 100) + "." + (cents % 100).toString().padStart(2, '0')
}
