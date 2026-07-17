package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

@Composable
fun WalletScreen() {
    var showLedger by remember { mutableStateOf(false) }
    var showRemit by remember { mutableStateOf(false) }
    var showAdvance by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }
    var showFuelLog by remember { mutableStateOf(false) }

    // Demo balances
    val walletBalance = 1250.0
    val collectedSgd = 340.0
    val collectedRm = 890.0
    
    val ledgerEntries = remember {
        listOf(
            LedgerEntry("l1", "trip_salary", 350.0, "RM", "行程收入 #t1", 900.0, 1250.0, "2026-07-16"),
            LedgerEntry("l2", "remit_rm", -200.0, "RM", "上交现金", 1100.0, 900.0, "2026-07-15"),
            LedgerEntry("l3", "expense_add", -85.0, "RM", "加油 Shell", 1185.0, 1100.0, "2026-07-14"),
        )
    }

    if (showLedger) {
        LedgerScreen(ledgerEntries, onBack = { showLedger = false })
        return
    }
    if (showRemit) {
        RemitScreen(onBack = { showRemit = false })
        return
    }
    if (showAdvance) {
        AdvanceScreen(onBack = { showAdvance = false })
        return
    }
    if (showWithdraw) {
        WithdrawScreen(onBack = { showWithdraw = false })
        return
    }
    if (showFuelLog) {
        FuelLogScreen(onBack = { showFuelLog = false })
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("财务结算", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Text("Treasury Center", color = DriverColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }

        // Balance Cards
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                balanceCard("💰 钱包余额", "RM ${String.format("%.2f", walletBalance)}", DriverColors.Primary, Modifier.weight(1f))
                balanceCard("💵 代收SGD", "SGD ${String.format("%.2f", collectedSgd)}", Color(0xFF3B82F6), Modifier.weight(1f))
                balanceCard("💵 代收RM", "RM ${String.format("%.2f", collectedRm)}", DriverColors.Warning, Modifier.weight(1f))
            }
        }

        // Action buttons
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("财务操作", color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                walletActionBtn("📤", "上交现金", Modifier.weight(1f)) { showRemit = true }
                walletActionBtn("💳", "预支工资", Modifier.weight(1f)) { showAdvance = true }
                walletActionBtn("🏧", "提现", Modifier.weight(1f)) { showWithdraw = true }
                walletActionBtn("⛽", "开销报备", Modifier.weight(1f)) { showFuelLog = true }
            }
        }

        // Recent ledger
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("近期流水", color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("查看全部 →", color = DriverColors.Primary, fontSize = 12.sp, modifier = Modifier.clickable { showLedger = true })
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(ledgerEntries.take(5)) { entry ->
            val isPositive = entry.amount > 0
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DriverColors.Card)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(if (isPositive) DriverColors.PrimaryBg else DriverColors.Danger.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) { Text(if (isPositive) "+" else "-", color = if (isPositive) DriverColors.Primary else DriverColors.Danger, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(entry.note, color = DriverColors.TextPrimary, fontSize = 13.sp)
                    Text(entry.createdAt, color = DriverColors.TextMuted, fontSize = 10.sp)
                }
                Text(
                    "${if (isPositive) "+" else ""}${entry.amount} ${entry.currency}",
                    color = if (isPositive) DriverColors.Primary else DriverColors.Danger,
                    fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun balanceCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(12.dp)
    ) {
        Text(label, color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun walletActionBtn(emoji: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = DriverColors.TextSecondary, fontSize = 10.sp)
    }
}

// ─── LEDGER (流水) ───

@Composable
private fun LedgerScreen(entries: List<LedgerEntry>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("流水明细", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        LazyColumn { items(entries) { entry ->
            val isPositive = entry.amount > 0
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DriverColors.Card).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(if (isPositive) DriverColors.PrimaryBg else DriverColors.Danger.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) { Text(if (isPositive) "📥" else "📤", fontSize = 16.sp) }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(entry.note, color = DriverColors.TextPrimary, fontSize = 14.sp)
                    Text("${entry.createdAt} · 余额: ${entry.balanceAfter}", color = DriverColors.TextMuted, fontSize = 11.sp)
                }
                Text("${if (isPositive) "+" else ""}${entry.amount}", color = if (isPositive) DriverColors.Primary else DriverColors.Danger,
                    fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        } }
    }
}

// ─── REMIT (上交) ───

@Composable
private fun RemitScreen(onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("RM") }
    var method by remember { mutableStateOf("CASH") }
    
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("上交现金", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        
        val fieldStyle = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        
        sectionLabel("金额")
        BasicTextField(value = amount, onValueChange = { amount = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 16.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (amount.isEmpty()) Text("0.00", color = DriverColors.TextDisabled, fontSize = 16.sp); inner() } })
        
        sectionSpacer()
        sectionLabel("币种")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("SGD", "RM").forEach { c ->
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                    .background(if (currency == c) DriverColors.Primary else DriverColors.Surface)
                    .clickable { currency = c }.padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Text(c, color = if (currency == c) Color.Black else DriverColors.TextMuted, fontWeight = FontWeight.Bold) }
            }
        }
        
        sectionSpacer()
        sectionLabel("方式")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CASH", "PAYNOW", "BANK", "TNG", "DUITNOW").forEach { m ->
                Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    .background(if (method == m) DriverColors.PrimaryBg else DriverColors.Surface)
                    .border(1.dp, if (method == m) DriverColors.Primary else DriverColors.Border, RoundedCornerShape(10.dp))
                    .clickable { method = m }.padding(horizontal = 12.dp, vertical = 8.dp)
                ) { Text(m, color = if (method == m) DriverColors.Primary else DriverColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
        }
        
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp))
            .background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center
        ) { Text("提交上交申请", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

// ─── ADVANCE (预支) ───

@Composable
private fun AdvanceScreen(onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("预支工资", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        val f = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        val ts = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp)
        val cs = SolidColor(DriverColors.Primary)
        
        sectionLabel("预支金额")
        BasicTextField(value = amount, onValueChange = { amount = it }, modifier = f, textStyle = ts, cursorBrush = cs, singleLine = true,
            decorationBox = { inner -> Box { if (amount.isEmpty()) Text("0.00", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        sectionLabel("收款人姓名")
        BasicTextField(value = name, onValueChange = { name = it }, modifier = f, textStyle = ts, cursorBrush = cs, singleLine = true,
            decorationBox = { inner -> Box { if (name.isEmpty()) Text("姓名", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        sectionLabel("收款账号")
        BasicTextField(value = number, onValueChange = { number = it }, modifier = f, textStyle = ts, cursorBrush = cs, singleLine = true,
            decorationBox = { inner -> Box { if (number.isEmpty()) Text("银行账号/手机号", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp))
            .background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center
        ) { Text("提交预支申请", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

// ─── WITHDRAW (提现) ───

@Composable
private fun WithdrawScreen(onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("提现", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        val f = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        sectionLabel("提现金额")
        BasicTextField(value = amount, onValueChange = { amount = it }, modifier = f,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 16.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (amount.isEmpty()) Text("0.00", color = DriverColors.TextDisabled, fontSize = 16.sp); inner() } })
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp))
            .background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center
        ) { Text("提交提现申请", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

// ─── FUEL LOG (开销报备) ───

@Composable
private fun FuelLogScreen(onBack: () -> Unit) {
    var expenseType by remember { mutableStateOf("加油") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("开销报备", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Expense Report", color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
        }
        
        val f = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        val ts = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp)
        val cs = SolidColor(DriverColors.Primary)
        
        sectionLabel("类型")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("加油", "Autopass充值", "维修", "保养", "轮胎", "其他").forEach { t ->
                Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    .background(if (expenseType == t) DriverColors.PrimaryBg else DriverColors.Surface)
                    .border(1.dp, if (expenseType == t) DriverColors.Primary else DriverColors.Border, RoundedCornerShape(10.dp))
                    .clickable { expenseType = t }.padding(horizontal = 10.dp, vertical = 8.dp)
                ) { Text(t, color = if (expenseType == t) DriverColors.Primary else DriverColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
        }
        sectionSpacer()
        sectionLabel("金额 (${if (expenseType == "Autopass充值") "SGD" else "RM"})")
        BasicTextField(value = amount, onValueChange = { amount = it }, modifier = f, textStyle = ts, cursorBrush = cs, singleLine = true,
            decorationBox = { inner -> Box { if (amount.isEmpty()) Text("0.00", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        sectionLabel("日期")
        BasicTextField(value = date, onValueChange = { date = it }, modifier = f, textStyle = ts, cursorBrush = cs, singleLine = true,
            decorationBox = { inner -> Box { if (date.isEmpty()) Text("YYYY-MM-DD", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        sectionLabel("备注")
        BasicTextField(value = note, onValueChange = { note = it }, modifier = f.height(80.dp), textStyle = ts, cursorBrush = cs,
            decorationBox = { inner -> Box { if (note.isEmpty()) Text("备注说明...", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp))
            .background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center
        ) { Text("确认上传开销", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

@Composable
private fun sectionLabel(text: String) {
    Text(text, color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
}

@Composable
private fun sectionSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}
