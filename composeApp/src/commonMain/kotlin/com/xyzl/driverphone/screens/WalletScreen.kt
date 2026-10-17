package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

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
import androidx.compose.runtime.LaunchedEffect
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
import com.xyzl.driverphone.WalletInfo
import com.xyzl.driverphone.WalletRepository
import com.xyzl.driverphone.walletBalanceText

private val WalletCard: Color @Composable get() = LocalAppColors.current.card
private val WalletChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val WalletInk: Color @Composable get() = LocalAppColors.current.ink
private val WalletMuted: Color @Composable get() = LocalAppColors.current.muted
private val WalletGreen = Color(0xFF3B82F6)

/** 账户索引：0 钱包余额 / 1 代收 SGD / 2 代收 MYR（余额读 se_wallets） */
private fun walletForIndex(index: Int, wallets: List<WalletInfo>): WalletInfo? = when (index) {
    0 -> wallets.firstOrNull { it.walletType == "main" && it.currency == "MYR" }
    1 -> wallets.firstOrNull { it.walletType == "collection" && it.currency == "SGD" }
    else -> wallets.firstOrNull { it.walletType == "collection" && it.currency == "MYR" }
}

private fun walletAccount(index: Int, wallets: List<WalletInfo>): WalletAccount {
    val currency = if (index == 1) "SGD" else "MYR"
    val w = walletForIndex(index, wallets)
    return WalletAccount(
        title = when (index) { 0 -> LocalStrings.current.s0192; 1 -> LocalStrings.current.s0272; else -> LocalStrings.current.s0271 },
        accountNo = w?.accountNumber ?: "-",
        currency = currency,
        available = walletBalanceText(currency, w?.availableBalance ?: 0.0),
        current = walletBalanceText(currency, w?.totalBalance ?: 0.0),
        entries = emptyList(),
    )
}

/** 我的钱包：首页 / 概述 / 扫码 / 流水 / 申请 五个板块 */
@Composable
fun WalletScreen(
    onBack: () -> Unit = {},
    isCompanyDriver: Boolean = true,
    userId: String? = null,
) {
    var tab by remember { mutableStateOf(0) }
    var accountIndex by remember { mutableStateOf<Int?>(null) }
    var wallets by remember { mutableStateOf<List<WalletInfo>>(emptyList()) }
    val repo = remember { WalletRepository() }

    LaunchedEffect(userId) {
        if (userId != null) wallets = repo.wallets(userId)
    }

    val openIndex = accountIndex
    if (openIndex != null) {
        WalletAccountScreen(
            account = walletAccount(openIndex, wallets),
            onBack = { accountIndex = null },
            accountId = walletForIndex(openIndex, wallets)?.accountId,
            categoryFilter = if (openIndex == 0) null else listOf("collection", "transfer"),
        )
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = LocalStrings.current.s0059, onBack = onBack)

        Box(Modifier.fillMaxWidth().weight(1f)) {
            when (tab) {
                0 -> WalletHomeTab(accounts = (0..2).map { walletAccount(it, wallets) }, onOpenAccount = { accountIndex = it })
                1 -> WalletTransferTab(userId = userId, wallets = wallets)
                2 -> WalletScanTab(userId = userId)
                3 -> LedgerScreen(userId = userId, onBack = onBack, embedded = true, showPrint = true)
                4 -> if (isCompanyDriver) WalletApplyTab() else WalletHomeTab(accounts = (0..2).map { walletAccount(it, wallets) }, onOpenAccount = { accountIndex = it })
            }
        }

        WalletBottomBar(selected = tab, onSelect = { tab = it }, showApply = isCompanyDriver)
    }
}

/** 首页：钱包余额 + 代收SGD / 代收MYR 两张并排卡片 */
@Composable
private fun WalletHomeTab(accounts: List<WalletAccount>, onOpenAccount: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(8.dp))
        BalanceCard(label = accounts[0].title, amount = accounts[0].current, big = true, onClick = { onOpenAccount(0) })
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            BalanceCard(accounts[1].title, accounts[1].current, modifier = Modifier.weight(1f), onClick = { onOpenAccount(1) })
            BalanceCard(accounts[2].title, accounts[2].current, modifier = Modifier.weight(1f), onClick = { onOpenAccount(2) })
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
private fun WalletBottomBar(selected: Int, onSelect: (Int) -> Unit, showApply: Boolean) {
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
            if (showApply) {
                WalletBarItem(Icons.Filled.Add, "申请", selected == 4, Modifier.weight(1f)) { onSelect(4) }
            } else {
                Spacer(Modifier.weight(1f))
            }
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
            Icon(Icons.Filled.QrCode, contentDescription = LocalStrings.current.s0137, tint = Color.White, modifier = Modifier.size(28.dp))
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

    var handInAccount by remember { mutableStateOf("代收SGD") }

    val isExpense = type == "开销资金"
    val isHandIn = type == "申请上交"
    val canSubmit = type != null && amount.isNotBlank()

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            FormSectionTitle(LocalStrings.current.s0596)
            FormGroup {
                ApplyInfoRow(LocalStrings.current.s0599, "${timeParts.first} ${timeParts.second}")
                ApplyInfoRow(
                    label = LocalStrings.current.s0168,
                    value = type ?: LocalStrings.current.s0180,
                    arrow = true,
                    onClick = { showTypeDialog = true },
                )
            }
            if (type != null) {
                Spacer(Modifier.height(18.dp))
                FormSectionTitle(type ?: "")
                FormGroup {
                    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(if (isHandIn) LocalStrings.current.s0274 else LocalStrings.current.s0076, fontSize = 13.sp, color = WalletMuted)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            when {
                                isExpense -> {
                                    CurrencyChip("SGD", currency == "SGD") { currency = "SGD" }
                                    CurrencyChip("MYR", currency == "MYR") { currency = "MYR" }
                                }
                                isHandIn -> {
                                    CurrencyChip("代收SGD", handInAccount == "代收SGD") { handInAccount = "代收SGD" }
                                    CurrencyChip("代收MYR", handInAccount == "代收MYR") { handInAccount = "代收MYR" }
                                }
                                else -> WalletCurrencyStatic("MYR")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                FormTextField(
                    label = LocalStrings.current.s0042,
                    value = amount,
                    placeholder = LocalStrings.current.s0179,
                    onValueChange = { amount = it },
                    keyboardNumber = true,
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        FormPrimaryButton(text = LocalStrings.current.s0616, enabled = canSubmit, onClick = { submitted = true })
        Spacer(Modifier.height(12.dp))
    }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            containerColor = WalletCard,
            title = { Text(LocalStrings.current.s0168, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = WalletInk) },
            text = {
                Column {
                    listOf(LocalStrings.current.s0755, LocalStrings.current.s0597, LocalStrings.current.s0165, LocalStrings.current.s0054).forEach { option ->
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
            title = { Text(LocalStrings.current.s0379, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = WalletInk) },
            text = {
                Text(
                    when {
                        isExpense -> LocalStrings.current.s0595.fill(currency, amount)
                        isHandIn -> "申请「上交${handInAccount}」${amount} 已提交。"
                        else -> "申请「${type}」已提交。"
                    },
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
                    Text(LocalStrings.current.confirm, color = WalletGreen, fontWeight = FontWeight.SemiBold)
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
        Text(value, fontSize = 14.sp, color = if (value == LocalStrings.current.s0180) WalletMuted else WalletInk)
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
