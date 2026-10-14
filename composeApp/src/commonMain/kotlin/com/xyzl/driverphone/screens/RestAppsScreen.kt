package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.xyzl.driverphone.model.RestApplication
import com.xyzl.driverphone.model.RestStatus
import com.xyzl.driverphone.model.RestStore
import com.xyzl.driverphone.model.RestType
import com.xyzl.driverphone.model.dateInRange
import com.xyzl.driverphone.model.isoToDateDisplay
import com.xyzl.driverphone.model.millisToDateDisplay
import com.xyzl.driverphone.model.monthStartIso

private val AppsBlue = Color(0xFF3B82F6)
private val AppsGreen = Color(0xFF10B981)
private val AppsAmber = Color(0xFFFBBF24)
private val AppsRed = Color(0xFFF43F5E)
private val AppsInk: Color @Composable get() = LocalAppColors.current.ink
private val AppsMuted: Color @Composable get() = LocalAppColors.current.muted
private val AppsChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val AppsCard: Color @Composable get() = LocalAppColors.current.card
private val AppsBorder: Color @Composable get() = LocalAppColors.current.border

private val appFilters = listOf("全部", "请假", "放假", "已通过", "待通过", "已取消")

/** 查看申请：日期范围（胶囊+原生日历）+ 合并式筛选 + 申请列表 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestAppsScreen(onBack: () -> Unit = {}) {
    var selectedId by remember { mutableStateOf<String?>(null) }

    val current = RestStore.apps.firstOrNull { it.id == selectedId }
    if (current != null) {
        RestAppDetailScreen(
            app = current,
            onBack = { selectedId = null },
            onDone = { selectedId = null },
        )
        return
    }

    val todayIso = remember { currentTimeParts().first }
    var rangeStart by remember { mutableStateOf(isoToDateDisplay(monthStartIso(todayIso))) }
    var rangeEnd by remember { mutableStateOf(isoToDateDisplay(todayIso)) }
    var showPicker by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(0) }

    val filtered = RestStore.apps.filter { a ->
        val byDate = dateInRange(a.appliedDate, rangeStart, rangeEnd)
        val byType = when (filter) {
            1 -> a.type == RestType.LEAVE
            2 -> a.type == RestType.HOLIDAY
            else -> true
        }
        val byStatus = when (filter) {
            3 -> a.status == RestStatus.APPROVED
            4 -> a.status == RestStatus.PENDING
            5 -> a.status == RestStatus.CANCELLED
            else -> true
        }
        byDate && byType && byStatus
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppsChip)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = AppsInk,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "查看申请",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppsInk,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(AppsChip)
                .clickable { showPicker = true }
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = AppsBlue,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                rangeStart + "  -  " + rangeEnd,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppsInk,
            )
        }

        Spacer(Modifier.height(12.dp))
        SegmentedTabs(options = appFilters, selectedIndex = filter, onSelect = { filter = it })
        Spacer(Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filtered, key = { it.id }) { app ->
                RestAppRow(app, onClick = { selectedId = app.id })
            }
        }
    }

    if (showPicker) {
        val st = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedStartDateMillis?.let { rangeStart = millisToDateDisplay(it) }
                    st.selectedEndDateMillis?.let { rangeEnd = millisToDateDisplay(it) }
                    showPicker = false
                }) { Text("确定", color = AppsBlue, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("取消", color = AppsMuted) } },
        ) {
            DateRangePicker(state = st, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RestAppRow(app: RestApplication, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppsCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RestTypeBadge(app.type)
                    Spacer(Modifier.width(8.dp))
                    Text("申请 " + app.appliedDate, fontSize = 12.sp, color = AppsMuted)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "休息 " + app.startDate + " - " + app.endDate,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppsInk,
                )
                Spacer(Modifier.height(4.dp))
                Text(app.days.toString() + " 天 · " + app.reason, fontSize = 12.sp, color = AppsMuted, maxLines = 1)
            }
            Spacer(Modifier.width(8.dp))
            RestStatusBadge(app.status)
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = AppsMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/** 申请详情：字段明细 + 待通过可撤销 */
@Composable
fun RestAppDetailScreen(
    app: RestApplication,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppsChip)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = AppsInk,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "申请详情",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppsInk,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppsCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(20.dp)) {
                    RestDetailRow("申请日期", app.appliedDate)
                    RestDetailDivider()
                    RestDetailRow("休息日期", app.startDate)
                    RestDetailDivider()
                    RestDetailRow("结束日期", app.endDate)
                    RestDetailDivider()
                    RestDetailRow("原因", app.reason)
                    RestDetailDivider()
                    RestDetailRow("申请休息天数", app.days.toString() + " 天")
                    RestDetailDivider()
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("状态", fontSize = 14.sp, color = AppsMuted, modifier = Modifier.weight(1f))
                        RestStatusBadge(app.status)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (app.status == RestStatus.PENDING) {
            Row(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, AppsRed, RoundedCornerShape(16.dp))
                        .clickable {
                            RestStore.cancel(app.id)
                            onDone()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("撤销申请", color = AppsRed, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppsBlue)
                        .clickable { onDone() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("完成", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppsBlue)
                    .clickable { onDone() },
                contentAlignment = Alignment.Center,
            ) {
                Text("完成", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun RestDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = AppsMuted, modifier = Modifier.weight(1f))
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppsInk,
            modifier = Modifier.weight(1.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
        )
    }
}

@Composable
private fun RestDetailDivider() {
    HorizontalDivider(
        color = AppsChip,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 14.dp),
    )
}

@Composable
private fun RestTypeBadge(type: RestType) {
    val c = if (type == RestType.LEAVE) AppsBlue else AppsGreen
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(c.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(type.label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = c)
    }
}

@Composable
private fun RestStatusBadge(status: RestStatus) {
    val c = when (status) {
        RestStatus.PENDING -> AppsAmber
        RestStatus.APPROVED -> AppsGreen
        RestStatus.CANCELLED -> AppsMuted
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(c.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(status.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = c)
    }
}
