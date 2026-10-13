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
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.LedgerEntries
import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.LedgerKind
import com.xyzl.driverphone.model.formatMoney
import com.xyzl.driverphone.model.signedMoney
import com.xyzl.driverphone.saveStatementPdf

private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark = Color(0xFF111111)
private val MutedGray = Color(0xFF6B7280)
private val FieldGray = Color(0xFFEFEFF3)

private enum class LedgerFilter(val label: String) {
    ALL("全部"),
    REIMBURSEMENT("报销"),
    SALARY("薪资"),
    TRANSFER("转账"),
}

/** 流水记录页：报销 / 薪资 / 转账 全部流水 */
@Composable
fun LedgerScreen(onBack: () -> Unit = {}, embedded: Boolean = false, showPrint: Boolean = false) {
    var filter by remember { mutableStateOf(LedgerFilter.ALL) }
    var selected by remember { mutableStateOf<LedgerEntry?>(null) }

    val current = selected
    if (current != null) {
        TransactionDetailScreen(entry = current, onBack = { selected = null })
        return
    }

    val entries = remember { LedgerEntries }
    var savedInfo by remember { mutableStateOf<String?>(null) }

    val filtered = entries.filter { e ->
        when (filter) {
            LedgerFilter.ALL -> true
            LedgerFilter.REIMBURSEMENT -> e.kind == LedgerKind.REIMBURSEMENT
            LedgerFilter.SALARY -> e.kind == LedgerKind.SALARY
            LedgerFilter.TRANSFER -> e.kind == LedgerKind.TRANSFER
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = if (embedded) 0.dp else 20.dp, vertical = if (embedded) 0.dp else 12.dp)) {
        if (!embedded) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FieldGray)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = InkDark,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "流水记录",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        }

        if (showPrint) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(FieldGray)
                        .clickable {
                            val ok = saveStatementPdf(statementFileName(), statementText(entries))
                            savedInfo = if (ok) "e-statement PDF 已生成并保存到手机（下载/XYZL）" else "保存失败，请检查存储权限后重试"
                        }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Print, contentDescription = "打印", tint = OnlineGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("打印 e-statement", fontSize = 12.sp, color = OnlineGreen, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LedgerFilter.entries.forEach { f ->
                FilterChip(f.label, filter == f) { filter = f }
            }
        }

        Spacer(Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filtered, key = { it.id }) { entry ->
                LedgerRow(entry, onClick = { selected = entry })
            }
        }
    }

    val info = savedInfo
    if (info != null) {
        AlertDialog(
            onDismissRequest = { savedInfo = null },
            containerColor = Color(0xFFF7F7F9),
            title = { Text("e-statement", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = InkDark) },
            text = { Text(info, fontSize = 14.sp, color = MutedGray) },
            confirmButton = { TextButton(onClick = { savedInfo = null }) { Text("确定", color = OnlineGreen) } },
        )
    }
}

private fun statementFileName(): String {
    val d = currentTimeParts().first.replace("-", "")
    return "e-statement_" + d + ".pdf"
}

private fun statementText(entries: List<LedgerEntry>): String {
    val sb = StringBuilder()
    sb.appendLine("星域臻旅 司机端 · e-Statement（电子对账单）")
    val now = currentTimeParts()
    sb.appendLine("生成时间：" + now.first + " " + now.second)
    sb.appendLine("交易笔数：" + entries.size)
    sb.appendLine("--------------------------------------------------")
    entries.forEach { e ->
        sb.appendLine("交易编号：" + e.id)
        sb.appendLine("日期时间：" + e.date)
        sb.appendLine("类型：" + e.kind.label + (e.expenseType?.let { "（" + it + "）" } ?: ""))
        sb.appendLine("来自：" + e.from)
        if (e.orderNo != null) sb.appendLine("订单编号：" + e.orderNo)
        sb.appendLine("金额：" + signedMoney(e.currency, e.amount, e.income))
        sb.appendLine("状态：" + e.status)
        if (e.remark.isNotBlank()) sb.appendLine("备注：" + e.remark)
        sb.appendLine("--------------------------------------------------")
    }
    return sb.toString()
}

@Composable
private fun LedgerRow(entry: LedgerEntry, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KindBadge(entry.kind)
                    Spacer(Modifier.width(8.dp))
                    Text(entry.date, fontSize = 12.sp, color = MutedGray)
                }
                Spacer(Modifier.height(6.dp))
                Text(entry.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatMoney(entry.currency, entry.amount),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark,
                )
                Spacer(Modifier.height(4.dp))
                Text(entry.status, fontSize = 12.sp, color = OnlineGreen)
            }
        }
    }
}

@Composable
private fun KindBadge(kind: LedgerKind) {
    val color = when (kind) {
        LedgerKind.SALARY -> Color(0xFF3B82F6)
        LedgerKind.REIMBURSEMENT -> Color(0xFFFBBF24)
        LedgerKind.TRANSFER -> Color(0xFF3B82F6)
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(kind.label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) OnlineGreen else FieldGray)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else MutedGray,
        )
    }
}
