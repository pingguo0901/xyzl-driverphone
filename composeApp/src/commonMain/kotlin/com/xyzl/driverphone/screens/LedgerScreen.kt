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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.LedgerKind
import com.xyzl.driverphone.model.formatMoney

private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark = Color(0xFFFFFFFF)
private val MutedGray = Color(0xFFA1A1AA)
private val FieldGray = Color(0xFF27272A)

private enum class LedgerFilter(val label: String) {
    ALL("全部"),
    REIMBURSEMENT("报销"),
    SALARY("薪资"),
    TRANSFER("转账"),
}

/** 流水记录页：报销 / 薪资 / 转账 全部流水 */
@Composable
fun LedgerScreen(onBack: () -> Unit = {}, embedded: Boolean = false) {
    var filter by remember { mutableStateOf(LedgerFilter.ALL) }
    var selected by remember { mutableStateOf<LedgerEntry?>(null) }

    val current = selected
    if (current != null) {
        TransactionDetailScreen(entry = current, onBack = { selected = null })
        return
    }

    val entries = remember {
        listOf(
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
        )
    }

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
}

@Composable
private fun LedgerRow(entry: LedgerEntry, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
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
