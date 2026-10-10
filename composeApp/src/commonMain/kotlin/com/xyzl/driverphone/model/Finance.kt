package com.xyzl.driverphone.model

/** 值班车辆（示例数据，待董事长给真实车牌与车型） */
val DutyVehicles = listOf(
    "JXY 8888 · 丰田 Innova",
    "WMB 2233 · 本田 CR-V",
    "VKL 5566 · 日产 Serena",
)

/** 报销类型选项 */
val ExpenseTypes = listOf(
    "加油", "TNG", "RFID", "Autopass", "洗车", "维修", "住宿", "其他",
)

/** 货币选项 */
val Currencies = listOf("MYR", "SGD")

/** 根据报销类型自动填货币：Autopass 是 SGD，其余都是 MYR */
fun currencyForType(type: String): String = if (type == "Autopass") "SGD" else "MYR"

/** 流水记录类型 */
enum class LedgerKind(val label: String) {
    SALARY("薪资"),
    REIMBURSEMENT("报销"),
    COLLECTION("代收"),
}

/** 一条流水记录 */
data class LedgerEntry(
    val id: String,
    val kind: LedgerKind,
    val title: String,
    val currency: String,
    val amount: Double,
    val date: String,
    val status: String,
)

/** 金额格式化（带货币前缀，跨平台） */
fun formatMoney(currency: String, amount: Double): String {
    val cents = (amount * 100).toLong()
    val sign = if (cents < 0) "-" else ""
    val abs = if (cents < 0) -cents else cents
    return currency + " " + sign + (abs / 100) + "." + (abs % 100).toString().padStart(2, '0')
}
