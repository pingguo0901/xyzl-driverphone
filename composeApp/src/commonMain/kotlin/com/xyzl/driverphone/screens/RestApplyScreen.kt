package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.xyzl.driverphone.model.isoToDateDisplay
import com.xyzl.driverphone.model.millisToDateDisplay
import com.xyzl.driverphone.model.restDaysBetween

private val ApplyBlue = Color(0xFF3B82F6)
private val ApplyInk: Color @Composable get() = LocalAppColors.current.ink
private val ApplyMuted: Color @Composable get() = LocalAppColors.current.muted
private val ApplyChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val ApplyCard: Color @Composable get() = LocalAppColors.current.card
private val ApplyBorder: Color @Composable get() = LocalAppColors.current.border

/** 申请休息：休息/结束日期（胶囊+原生日历）、原因、申请休息天数、取消/确认 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestApplyScreen(
    onBack: () -> Unit = {},
    onConfirm: (RestApplication) -> Unit = {},
) {
    val today = remember { isoToDateDisplay(currentTimeParts().first) }
    var startDate by remember { mutableStateOf(today) }
    var endDate by remember { mutableStateOf(today) }
    var reason by remember { mutableStateOf("") }
    var pickStart by remember { mutableStateOf(false) }
    var pickEnd by remember { mutableStateOf(false) }

    val days = restDaysBetween(startDate, endDate)

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
                    .background(ApplyChip)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = ApplyInk,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                LocalStrings.current.s0166,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = ApplyInk,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            FieldLabel("休息日期")
            DateCapsule(startDate) { pickStart = true }

            Spacer(Modifier.height(16.dp))
            FieldLabel("结束日期")
            DateCapsule(endDate) { pickEnd = true }

            Spacer(Modifier.height(16.dp))
            FieldLabel("原因")
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(LocalStrings.current.s0671) },
                minLines = 3,
                shape = RoundedCornerShape(14.dp),
            )

            Spacer(Modifier.height(16.dp))

            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ApplyCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(LocalStrings.current.s0167, fontSize = 14.sp, color = ApplyMuted, modifier = Modifier.weight(1f))
                    Text(days.toString() + LocalStrings.current.s0006, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ApplyBlue)
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ApplyBorder, RoundedCornerShape(16.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("取消", color = ApplyInk, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ApplyBlue)
                    .clickable {
                        onConfirm(
                            RestApplication(
                                id = RestStore.nextId(),
                                appliedDate = today,
                                startDate = startDate,
                                endDate = endDate,
                                reason = reason.ifBlank { "—" },
                                days = days,
                                type = RestType.LEAVE,
                                status = RestStatus.PENDING,
                            ),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(LocalStrings.current.s0071, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (pickStart) {
        val st = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { pickStart = false },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedDateMillis?.let { startDate = millisToDateDisplay(it) }
                    pickStart = false
                }) { Text(LocalStrings.current.confirm, color = ApplyBlue, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { pickStart = false }) { Text("取消", color = ApplyMuted) } },
        ) {
            DatePicker(state = st, modifier = Modifier.weight(1f))
        }
    }

    if (pickEnd) {
        val st = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { pickEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedDateMillis?.let { endDate = millisToDateDisplay(it) }
                    pickEnd = false
                }) { Text(LocalStrings.current.confirm, color = ApplyBlue, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { pickEnd = false }) { Text("取消", color = ApplyMuted) } },
        ) {
            DatePicker(state = st, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = ApplyMuted,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp),
    )
}

@Composable
private fun DateCapsule(value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(ApplyChip)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarMonth,
            contentDescription = null,
            tint = ApplyBlue,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ApplyInk)
    }
}
