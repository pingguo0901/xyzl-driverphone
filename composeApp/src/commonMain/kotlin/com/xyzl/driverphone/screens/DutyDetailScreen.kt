package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.xyzl.driverphone.model.DutyRecord
import com.xyzl.driverphone.model.formatRinggit
import com.xyzl.driverphone.model.formatSgd
import com.xyzl.driverphone.saveStatementPdf

private val DutyAccent = Color(0xFF3B82F6)
private val DutyInk: Color @Composable get() = LocalAppColors.current.ink
private val DutyMuted: Color @Composable get() = LocalAppColors.current.muted
private val DutyChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val DutyCard: Color @Composable get() = LocalAppColors.current.card

/** 值班详情：左上返回 / 右上打印 PDF / 当日结算明细 / 底部完成 */
@Composable
fun DutyDetailScreen(
    record: DutyRecord,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    var savedInfo by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // 顶栏：返回（左） + 标题（中） + 打印 PDF（右）
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
                    LocalStrings.current.s0111,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DutyInk,
                    modifier = Modifier.align(Alignment.Center),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DutyChip)
                        .clickable {
                            val ok = saveStatementPdf(dutyPdfFileName(record), dutyPdfText(record))
                            savedInfo = if (ok) LocalStrings.current.s0296 else LocalStrings.current.s0109
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Print,
                        contentDescription = LocalStrings.current.s0444,
                        tint = DutyAccent,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DutyCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column(Modifier.padding(20.dp)) {
                        DetailRow("日期", record.date)
                        DetailDivider()
                        DetailRow("今日薪资", formatRinggit(record.salary))
                        DetailDivider()
                        DetailRow("今日报销", formatRinggit(record.reimbursement))
                        DetailDivider()
                        DetailRow("今日单数", record.orders.toString() + " 单")
                        DetailDivider()
                        DetailRow("今日代收SGD", formatSgd(record.collectSgd))
                        DetailDivider()
                        DetailRow("今日代收MYR", formatRinggit(record.collectMyr))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DutyAccent)
                    .clickable { onDone() },
                contentAlignment = Alignment.Center,
            ) {
                Text("完成", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    val info = savedInfo
    if (info != null) {
        AlertDialog(
            onDismissRequest = { savedInfo = null },
            containerColor = DutyCard,
            title = { Text(LocalStrings.current.s0111, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = DutyInk) },
            text = { Text(info, fontSize = 14.sp, color = DutyMuted) },
            confirmButton = { TextButton(onClick = { savedInfo = null }) { Text(LocalStrings.current.confirm, color = DutyAccent) } },
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = DutyMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DutyInk)
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        color = LocalAppColors.current.surfaceVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 14.dp),
    )
}

private fun dutyPdfFileName(record: DutyRecord): String {
    val d = record.date.replace("/", "")
    return "值班详情_" + d + ".pdf"
}

private fun dutyPdfText(record: DutyRecord): String {
    val sb = StringBuilder()
    sb.appendLine(LocalStrings.current.s0498)
    sb.appendLine(LocalStrings.current.s0143 + record.date)
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("今日薪资：" + formatRinggit(record.salary))
    sb.appendLine("今日报销：" + formatRinggit(record.reimbursement))
    sb.appendLine("今日单数：" + record.orders + " 单")
    sb.appendLine("今日代收SGD：" + formatSgd(record.collectSgd))
    sb.appendLine("今日代收MYR：" + formatRinggit(record.collectMyr))
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("生成时间：" + currentTimeParts().first + " " + currentTimeParts().second)
    return sb.toString()
}
