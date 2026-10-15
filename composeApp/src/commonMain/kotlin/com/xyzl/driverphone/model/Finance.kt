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
    TRANSFER("转账"),
}

/** 一条流水记录 */
data class LedgerEntry(
    /** 交易编号（每笔流水唯一） */
    val id: String,
    val kind: LedgerKind,
    val title: String,
    val currency: String,
    val amount: Double,
    val date: String,
    val status: String,
    /** 来自（付款方 / 来源） */
    val from: String = "",
    /** 报销类型（仅报销记录） */
    val expenseType: String? = null,
    /** 订单编号（有关联订单时显示） */
    val orderNo: String? = null,
    /** 备注 */
    val remark: String = "",
    /** 是否为入账（true 显示 +，false 显示 -） */
    val income: Boolean = true,
    /** 收据（Storage 路径或链接） */
    val receiptUrl: String? = null,
    /** 交易编号 */
    val txNo: String? = null,
)

/** 全部流水记录（示例数据，钱包与流水页共用） */
val LedgerEntries = listOf(
    LedgerEntry(
        id = "TXN20261013001",
        kind = LedgerKind.SALARY,
        title = "今日薪资 · 6 单",
        currency = "MYR",
        amount = 386.00,
        date = "13/10/2026 20:00",
        status = "已入账",
        from = "星域臻旅 · 薪资结算",
        orderNo = "SO20261013006",
        remark = "6 单合并结算",
        income = true,
    ),
    LedgerEntry(
        id = "TXN20261013002",
        kind = LedgerKind.REIMBURSEMENT,
        title = "加油 · JXY 8888",
        currency = "MYR",
        amount = 128.50,
        date = "13/10/2026 18:30",
        status = "审核中",
        from = "星域臻旅 · 报销",
        expenseType = "加油",
        remark = "JXY 8888 加油费",
        income = true,
    ),
    LedgerEntry(
        id = "TXN20261013003",
        kind = LedgerKind.TRANSFER,
        title = "代收车费 · 张先生",
        currency = "MYR",
        amount = 86.00,
        date = "13/10/2026 14:42",
        status = "已代收",
        from = "乘客 · 张先生",
        orderNo = "ORD20261013021",
        remark = "吉隆坡机场 → 新山 行程代收",
        income = false,
    ),
    LedgerEntry(
        id = "TXN20261013004",
        kind = LedgerKind.TRANSFER,
        title = "代收车费 · 李女士",
        currency = "SGD",
        amount = 24.50,
        date = "13/10/2026 15:20",
        status = "已代收",
        from = "乘客 · 李女士",
        orderNo = "ORD20261013022",
        remark = "吉隆坡机场 → 新加坡 行程代收",
        income = false,
    ),
    LedgerEntry(
        id = "TXN20261012005",
        kind = LedgerKind.REIMBURSEMENT,
        title = "Autopass · WMB 2233",
        currency = "SGD",
        amount = 42.00,
        date = "12/10/2026 19:10",
        status = "已报销",
        from = "星域臻旅 · 报销",
        expenseType = "Autopass",
        remark = "WMB 2233 过路费",
        income = true,
    ),
    LedgerEntry(
        id = "TXN20261012006",
        kind = LedgerKind.SALARY,
        title = "昨日薪资 · 5 单",
        currency = "MYR",
        amount = 312.00,
        date = "12/10/2026 20:00",
        status = "已入账",
        from = "星域臻旅 · 薪资结算",
        orderNo = "SO20261012005",
        remark = "5 单合并结算",
        income = true,
    ),
    LedgerEntry(
        id = "TXN20261012007",
        kind = LedgerKind.REIMBURSEMENT,
        title = "洗车 · VKL 5566",
        currency = "MYR",
        amount = 25.00,
        date = "12/10/2026 12:00",
        status = "已报销",
        from = "星域臻旅 · 报销",
        expenseType = "洗车",
        remark = "VKL 5566 洗车费",
        income = true,
    ),
    LedgerEntry(
        id = "TXN20261012008",
        kind = LedgerKind.TRANSFER,
        title = "代收车费 · 王先生",
        currency = "MYR",
        amount = 120.00,
        date = "12/10/2026 11:20",
        status = "已代收",
        from = "乘客 · 王先生",
        orderNo = "ORD20261012018",
        remark = "新山 → 吉隆坡机场 行程代收",
        income = false,
    ),
    LedgerEntry(
        id = "TXN20261012009",
        kind = LedgerKind.TRANSFER,
        title = "代收车费 · 陈女士",
        currency = "SGD",
        amount = 18.00,
        date = "12/10/2026 09:05",
        status = "已代收",
        from = "乘客 · 陈女士",
        orderNo = "ORD20261012019",
        remark = "新加坡 → 新山 行程代收",
        income = false,
    ),
)

/** 金额格式化（带货币前缀，跨平台） */
fun formatMoney(currency: String, amount: Double): String {
    val cents = (amount * 100).toLong()
    val sign = if (cents < 0) "-" else ""
    val abs = if (cents < 0) -cents else cents
    return currency + " " + sign + (abs / 100) + "." + (abs % 100).toString().padStart(2, '0')
}

/** 带正负号的金额，如「+ MYR 386.00」「- MYR 86.00」 */
fun signedMoney(currency: String, amount: Double, income: Boolean): String {
    val sign = if (income) "+" else "-"
    val abs = if (amount < 0) -amount else amount
    return sign + " " + formatMoney(currency, abs)
}
