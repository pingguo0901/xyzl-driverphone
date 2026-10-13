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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QrCode
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
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.LedgerEntries
import com.xyzl.driverphone.model.LedgerKind

private val WalletCard: Color @Composable get() = LocalAppColors.current.card
private val WalletChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val WalletInk: Color @Composable get() = LocalAppColors.current.ink
private val WalletMuted: Color @Composable get() = LocalAppColors.current.muted
private val WalletGreen = Color(0xFF3B82F6)

/** 账户索引：0 钱包余额 / 1 代收 SGD / 2 代收 MYR */
private fun walletAccount(index: Int): WalletAccount = when (index) {
    0 -> WalletAccount(
        title = "钱包余额",
        accountNo = "8821 0000 1234 5678",
        currency = "MYR",
        available = "MYR 1,672.90",
        current = "MYR 1,758.90",
        entries = LedgerEntries,
    )
    1 -> WalletAccount(
        title = "代收 SGD",
        accountNo = "8821 0000 5678 1234",
        currency = "SGD",
        available = "SGD 386.50",
        current = "SGD 386.50",
        entries = LedgerEntries.filter { it.currency == "SGD" && it.kind == LedgerKind.TRANSFER },
    )
    else -> WalletAccount(
        title = "代收 MYR",
        accountNo = "8821 0000 4321 8765",
        currency = "MYR",
        available = "MYR 1,286.40",
        current = "MYR 1,286.40",
        entries = LedgerEntries.filter { it.currency == "MYR" && it.kind == LedgerKind.TRANSFER },
    )
}

/** 我的钱包：首页 / 概述 / 扫码 / 流水 / 申请 五个板块 */
@Composable
fun WalletScreen(onBack: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) }
    var accountIndex by remember { mutableStateOf<Int?>(null) }

    val openIndex = accountIndex
    if (openIndex != null) {
        WalletAccountScreen(account = walletAccount(openIndex), onBack = { accountIndex = null })
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "我的钱包", onBack = onBack)

        Box(Modifier.fillMaxWidth().weight(1f)) {
            when (tab) {
                0 -> WalletHomeTab(onOpenAccount = { accountIndex = it })
                1 -> WalletTransferTab()
                2 -> WalletScanTab()
                3 -> LedgerScreen(onBack = onBack, embedded = true, showPrint = true)
                4 -> WalletApplyTab()
            }
        }

        WalletBottomBar(selected = tab, onSelect = { tab = it })
    }
}

/** 首页：钱包余额 + 代收SGD / 代收MYR 两张并排卡片 */
@Composable
private fun WalletHomeTab(onOpenAccount: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(8.dp))
        BalanceCard(label = "钱包余额", amount = "MYR 1,672.90", big = true, onClick = { onOpenAccount(0) })
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            BalanceCard("代收 SGD", "SGD 386.50", modifier = Modifier.weight(1f), onClick = { onOpenAccount(1) })
            BalanceCard("代收 MYR", "MYR 1,286.40", modifier = Modifier.weight(1f), onClick = { onOpenAccount(2) })
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BalanceCard(
    label: String,
    amount: String,
    big: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth().let { if (onClick != null) it.clickable { onClick() } else it },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WalletCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = if (big) 26.dp else 20.dp)) {
            Text(label, fontSize = 13.sp, color = WalletMuted)
            Spacer(Modifier.height(if (big) 12.dp else 10.dp))
            Text(
                amount,
                fontSize = if (big) 30.sp else 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = WalletInk,
            )
        }
    }
}

/** 底部胶囊合并快捷栏：首页 概述 扫码（突出圆形） 流水 申请 */
@Composable
private fun WalletBottomBar(selected: Int, onSelect: (Int) -> Unit) {
    Box(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(50))
                .background(WalletCard),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalletBarItem(Icons.Filled.Home, "首页", selected == 0, Modifier.weight(1f)) { onSelect(0) }
            WalletBarItem(Icons.AutoMirrored.Filled.Send, "转账", selected == 1, Modifier.weight(1f)) { onSelect(1) }
            Spacer(Modifier.weight(1f))
            WalletBarItem(Icons.AutoMirrored.Filled.ReceiptLong, "流水", selected == 3, Modifier.weight(1f)) { onSelect(3) }
            WalletBarItem(Icons.Filled.Add, "申请", selected == 4, Modifier.weight(1f)) { onSelect(4) }
        }
        // 突出的圆形扫码按钮
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(WalletGreen)
                .clickable { onSelect(2) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.QrCode, contentDescription = "扫码", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun WalletBarItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = if (active) WalletGreen else WalletMuted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = if (active) WalletGreen else WalletMuted)
    }
}

/** 申请页：申请时间 + 申请类型（预支薪水 / 申请发薪 / 开销资金） + 确认申请 */
@Composable
private fun WalletApplyTab() {
    val timeParts = remember { currentTimeParts() }
    var type by remember { mutableStateOf<String?>(null) }
    var showTypeDialog by remember { mutableStateOf(false) }
    var currency by remember { mutableStateOf("MYR") }
    var amount by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val isExpense = type == "开销资金"
    val canSubmit = type != null && amount.isNotBlank()

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            FormSectionTitle("申请信息")
            FormGroup {
                ApplyInfoRow("申请时间", "${timeParts.first} ${timeParts.second}")
                ApplyInfoRow(
                    label = "申请类型",
                    value = type ?: "请选择",
                    arrow = true,
                    onClick = { showTypeDialog = true },
                )
            }
            if (type != null) {
                Spacer(Modifier.height(18.dp))
                FormSectionTitle(if (isExpense) "开销资金" else type ?: "")
                FormGroup {
                    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text("货币", fontSize = 13.sp, color = WalletMuted)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (isExpense) {
                                CurrencyChip("SGD", currency == "SGD") { currency = "SGD" }
                                CurrencyChip("MYR", currency == "MYR") { currency = "MYR" }
                            } else {
                                WalletCurrencyStatic("MYR")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                FormTextField(
                    label = "金额",
                    value = amount,
                    placeholder = "请输入金额",
                    onValueChange = { amount = it },
                    keyboardNumber = true,
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        FormPrimaryButton(text = "确认申请", enabled = canSubmit, onClick = { submitted = true })
        Spacer(Modifier.height(12.dp))
    }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            containerColor = WalletCard,
            title = { Text("申请类型", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = WalletInk) },
            text = {
                Column {
                    listOf("预支薪水", "申请发薪", "开销资金").forEach { option ->
                        Text(
                            option,
                            fontSize = 15.sp,
                            color = if (type == option) WalletGreen else WalletInk,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    type = option
                                    if (option != "开销资金") currency = "MYR"
                                    showTypeDialog = false
                                }
                                .padding(vertical = 14.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTypeDialog = false }) {
                    Text("取消", color = WalletMuted)
                }
            },
        )
    }

    if (submitted) {
        AlertDialog(
            onDismissRequest = { submitted = false },
            containerColor = WalletCard,
            title = { Text("已提交", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = WalletInk) },
            text = {
                Text(
                    if (isExpense) "申请「开销资金」${currency} ${amount} 已提交。" else "申请「${type}」已提交。",
                    fontSize = 14.sp,
                    color = WalletMuted,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    submitted = false
                    type = null
                    amount = ""
                }) {
                    Text("确定", color = WalletGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

@Composable
private fun ApplyInfoRow(
    label: String,
    value: String,
    arrow: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = WalletInk, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = if (value == "请选择") WalletMuted else WalletInk)
        if (arrow) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = WalletMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun WalletCurrencyStatic(label: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(WalletGreen)
            .padding(horizontal = 20.dp, vertical = 9.dp),
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun CurrencyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) WalletGreen else WalletChip)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 9.dp),
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else WalletMuted,
        )
    }
}
