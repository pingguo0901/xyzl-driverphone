package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.DutyData
import com.xyzl.driverphone.model.DutyRecord
import com.xyzl.driverphone.model.formatRinggit

private val DutyBlue = Color(0xFF3B82F6)
private val DutyGreen = Color(0xFF10B981)
private val DutyInk: Color @Composable get() = LocalAppColors.current.ink
private val DutyMuted: Color @Composable get() = LocalAppColors.current.muted
private val DutyChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val DutyCard: Color @Composable get() = LocalAppColors.current.card

/** 我的值班：日期范围（胶囊+原生日历）/ 休息·跨境额度胶囊卡片 / 值班记录列表 / 申请休息 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DutyScreen(
    onBack: () -> Unit = {},
) {
    var selected by remember { mutableStateOf<DutyRecord?>(null) }

    val current = selected
    if (current != null) {
        DutyDetailScreen(record = current, onBack = { selected = null }, onDone = { selected = null })
        return
    }

    val todayIso = remember { currentTimeParts().first }
    var rangeStart by remember { mutableStateOf(mmddFromIso(monthStartIso(todayIso))) }
    var rangeEnd by remember { mutableStateOf(mmddFromIso(todayIso)) }
    var showPicker by remember { mutableStateOf(false) }
    var showApply by remember { mutableStateOf(false) }
    var applyMsg by remember { mutableStateOf<String?>(null) }

    val summary = DutyData.summary
    val records = remember { DutyData.records }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 顶栏：返回 + 标题
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DutyChip)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = DutyInk,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "我的值班",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = DutyInk,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 胶囊式日期范围选择
            item {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(DutyChip)
                        .clickable { showPicker = true }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = DutyBlue,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        rangeStart + "  -  " + rangeEnd,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DutyInk,
                    )
                }
            }

            // 胶囊式卡片：休息额度 / 跨境额度
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DutyStatCard(
                        title = "休息",
                        leftLabel = "已休息天数",
                        leftValue = summary.restUsed.toString() + " 天",
                        rightLabel = "剩余休息天数",
                        rightValue = summary.restRemaining.toString() + " 天",
                        accent = DutyBlue,
                        modifier = Modifier.weight(1f),
                    )
                    DutyStatCard(
                        title = "跨境",
                        leftLabel = "已跨境天数",
                        leftValue = summary.crossUsed.toString() + " 天",
                        rightLabel = "剩余天数",
                        rightValue = summary.crossRemaining.toString() + " 天",
                        accent = DutyGreen,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Text(
                    "值班记录",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DutyMuted,
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                )
            }

            items(records, key = { it.id }) { record ->
                DutyRecordRow(record, onClick = { selected = record })
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DutyBlue)
                .clickable { showApply = true },
            contentAlignment = Alignment.Center,
        ) {
            Text("申请休息", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    // 原生日历：日期范围选择
    if (showPicker) {
        val pickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedStartDateMillis?.let { rangeStart = millisToDate(it) }
                    pickerState.selectedEndDateMillis?.let { rangeEnd = millisToDate(it) }
                    showPicker = false
                }) { Text("确定", color = DutyBlue, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("取消", color = DutyMuted) }
            },
        ) {
            DateRangePicker(state = pickerState, modifier = Modifier.weight(1f))
        }
    }

    if (showApply) {
        AlertDialog(
            onDismissRequest = { showApply = false },
            containerColor = DutyCard,
            title = { Text("申请休息", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = DutyInk) },
            text = { Text("确认提交休息申请吗？", fontSize = 14.sp, color = DutyMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showApply = false
                    applyMsg = "休息申请已提交，请等待调度确认。"
                }) { Text("确认提交", color = DutyBlue, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showApply = false }) { Text("取消", color = DutyMuted) }
            },
        )
    }

    val msg = applyMsg
    if (msg != null) {
        AlertDialog(
            onDismissRequest = { applyMsg = null },
            containerColor = DutyCard,
            title = { Text("申请休息", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = DutyInk) },
            text = { Text(msg, fontSize = 14.sp, color = DutyMuted) },
            confirmButton = { TextButton(onClick = { applyMsg = null }) { Text("确定", color = DutyBlue) } },
        )
    }
}

@Composable
private fun DutyStatCard(
    title: String,
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DutyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accent)
            Spacer(Modifier.height(12.dp))
            StatLine(leftLabel, leftValue, accent)
            Spacer(Modifier.height(8.dp))
            StatLine(rightLabel, rightValue, DutyInk)
        }
    }
}

@Composable
private fun StatLine(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = DutyMuted)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun DutyRecordRow(record: DutyRecord, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DutyCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(record.date, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DutyInk)
                Spacer(Modifier.height(4.dp))
                Text(record.orders.toString() + " 单 · 薪资 " + formatRinggit(record.salary), fontSize = 12.sp, color = DutyMuted)
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = DutyMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/** yyyy-MM-dd → dd/MM/yyyy */
private fun mmddFromIso(iso: String): String {
    val p = iso.split("-")
    return if (p.size == 3) p[2] + "/" + p[1] + "/" + p[0] else iso
}

/** yyyy-MM-dd → 当月第一天 yyyy-MM-dd */
private fun monthStartIso(iso: String): String {
    val p = iso.split("-")
    return if (p.size == 3) p[0] + "-" + p[1] + "-01" else iso
}

/** UTC 毫秒 → dd/MM/yyyy（commonMain 无 java.time） */
private fun millisToDate(millis: Long): String {
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
