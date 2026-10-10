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

private val OnlineGreen = Color(0xFF206A4E)
private val InkDark = Color(0xFFE8EDEA)
private val MutedGray = Color(0xFF9DB0A8)
private val FieldGray = Color(0xFF31403A)

private enum class LedgerFilter(val label: String) {
    ALL("全部"),
    REIMBURSEMENT("报销"),
    SALARY("薪资"),
    COLLECTION("代收"),
}

/** 流水记录页：报销 / 薪资 / 代收 全部流水 */
@Composable
fun LedgerScreen(onBack: () -> Unit = {}) {
    var filter by remember { mutableStateOf(LedgerFilter.ALL) }

    val entries = remember {
        listOf(
            LedgerEntry("L001", LedgerKind.SALARY, "今日薪资 · 6 单", "MYR", 386.00, "今天 20:00", "已入账"),
            LedgerEntry("L002", LedgerKind.REIMBURSEMENT, "加油 · JXY 8888", "MYR", 128.50, "今天 18:30", "审核中"),
            LedgerEntry("L003", LedgerKind.COLLECTION, "代收车费 · 张先生", "MYR", 86.00, "今天 14:42", "已代收"),
            LedgerEntry("L004", LedgerKind.COLLECTION, "代收车费 · 李女士", "SGD", 24.50, "今天 15:20", "已代收"),
            LedgerEntry("L005", LedgerKind.REIMBURSEMENT, "Autopass · WMB 2233", "SGD", 42.00, "昨天 19:10", "已报销"),
            LedgerEntry("L006", LedgerKind.SALARY, "昨日薪资 · 5 单", "MYR", 312.00, "昨天 20:00", "已入账"),
            LedgerEntry("L007", LedgerKind.REIMBURSEMENT, "洗车 · VKL 5566", "MYR", 25.00, "昨天 12:00", "已报销"),
        )
    }

    val filtered = entries.filter { e ->
        when (filter) {
            LedgerFilter.ALL -> true
            LedgerFilter.REIMBURSEMENT -> e.kind == LedgerKind.REIMBURSEMENT
            LedgerFilter.SALARY -> e.kind == LedgerKind.SALARY
            LedgerFilter.COLLECTION -> e.kind == LedgerKind.COLLECTION
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
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

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LedgerFilter.entries.forEach { f ->
                FilterChip(f.label, filter == f) { filter = f }
            }
        }

        Spacer(Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filtered, key = { it.id }) { entry ->
                LedgerRow(entry)
            }
        }
    }
}

@Composable
private fun LedgerRow(entry: LedgerEntry) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3833)),
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
        LedgerKind.SALARY -> Color(0xFF206A4E)
        LedgerKind.REIMBURSEMENT -> Color(0xFFFF9F0A)
        LedgerKind.COLLECTION -> Color(0xFF0A84FF)
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
