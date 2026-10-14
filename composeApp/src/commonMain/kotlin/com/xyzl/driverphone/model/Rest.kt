package com.xyzl.driverphone.model

import androidx.compose.runtime.mutableStateListOf

/** 休息类型 */
enum class RestType(val label: String) {
    LEAVE("请假"),
    HOLIDAY("放假"),
}

/** 申请状态 */
enum class RestStatus(val label: String) {
    PENDING("待通过"),
    APPROVED("已通过"),
    CANCELLED("已取消"),
}

/** 一条休息申请 */
data class RestApplication(
    val id: String,
    val appliedDate: String,   // 申请日期 dd/MM/yyyy
    val startDate: String,     // 休息日期
    val endDate: String,       // 结束日期
    val reason: String,        // 原因
    val days: Int,             // 申请休息天数
    val type: RestType,
    val status: RestStatus,
)

/** 休息申请数据源（本地可变，确认/撤销实时生效） */
object RestStore {
    val apps = mutableStateListOf(
        RestApplication("A001", "12/10/2026", "15/10/2026", "16/10/2026", "家中有事，需请两天假", 2, RestType.LEAVE, RestStatus.PENDING),
        RestApplication("A002", "08/10/2026", "10/10/2026", "11/10/2026", "公司统一放假", 2, RestType.HOLIDAY, RestStatus.APPROVED),
        RestApplication("A003", "01/10/2026", "03/10/2026", "03/10/2026", "个人原因", 1, RestType.LEAVE, RestStatus.CANCELLED),
    )

    fun add(app: RestApplication) {
        apps.add(0, app)
    }

    fun cancel(id: String) {
        val i = apps.indexOfFirst { it.id == id }
        if (i >= 0) apps[i] = apps[i].copy(status = RestStatus.CANCELLED)
    }

    fun nextId(): String {
        val n = apps.size + 1
        return "A" + n.toString().padStart(3, '0')
    }
}

/** dd/MM/yyyy → 天数序号（civil 算法，跨平台） */
private fun ddMmYyyyToDays(date: String): Int {
    val p = date.split("/")
    if (p.size != 3) return 0
    val d = p[0].toIntOrNull() ?: 1
    val m = p[1].toIntOrNull() ?: 1
    val y = p[2].toIntOrNull() ?: 2000
    val yy = if (m <= 2) y - 1 else y
    val era = (if (yy >= 0) yy else yy - 399) / 400
    val yoe = yy - era * 400
    val doy = (153 * (if (m > 2) m - 3 else m + 9) + 2) / 5 + d - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097 + doe - 719468
}

/** 申请休息天数（含首尾） */
fun restDaysBetween(startDate: String, endDate: String): Int {
    val n = ddMmYyyyToDays(endDate) - ddMmYyyyToDays(startDate) + 1
    return if (n < 0) 0 else n
}

/** dd/MM/yyyy 是否落在 [start, end] 内 */
fun dateInRange(date: String, start: String, end: String): Boolean {
    val v = ddMmYyyyToDays(date)
    return v >= ddMmYyyyToDays(start) && v <= ddMmYyyyToDays(end)
}

/** yyyy-MM-dd → dd/MM/yyyy */
fun isoToDateDisplay(iso: String): String {
    val p = iso.split("-")
    return if (p.size == 3) p[2] + "/" + p[1] + "/" + p[0] else iso
}

/** yyyy-MM-dd → 当月第一天 yyyy-MM-dd */
fun monthStartIso(iso: String): String {
    val p = iso.split("-")
    return if (p.size == 3) p[0] + "-" + p[1] + "-01" else iso
}

/** UTC 毫秒 → dd/MM/yyyy（commonMain 无 java.time） */
fun millisToDateDisplay(millis: Long): String {
    val z = millis / 86400000L + 719468L
    val era = (if (z >= 0) z else z - 146096L) / 146097L
    val doe = z - era * 146097L
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L
    val y = yoe + era * 400L
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val d = doy - (153L * mp + 2L) / 5L + 1L
    val m = if (mp < 10L) mp + 3L else mp - 9L
    val yy = if (m <= 2L) y + 1L else y
    val dd = if (d < 10L) "0" + d else "" + d
    val mm = if (m < 10L) "0" + m else "" + m
    return dd + "/" + mm + "/" + yy
}
