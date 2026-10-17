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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.WalletRepository
import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.formatMoney

private val AcctCard: Color @Composable get() = LocalAppColors.current.card
private val AcctChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val AcctInk: Color @Composable get() = LocalAppColors.current.ink
private val AcctMuted: Color @Composable get() = LocalAppColors.current.muted
private val AcctGreen = Color(0xFF3B82F6)
private val AcctDivider: Color @Composable get() = LocalAppColors.current.surfaceVariant

/** 钱包账户（钱包余额 / 代收 SGD / 代收 MYR） */
data class WalletAccount(
    val title: String,
    val accountNo: String,
    val currency: String,
    val available: String,
    val current: String,
    val entries: List<LedgerEntry>,
)

/** 钱包账户页：标题 + 帐号(复制) + 可用/当前余额 + 该账户交易流水 */
@Composable
fun WalletAccountScreen(
    account: WalletAccount,
    onBack: () -> Unit = {},
    accountId: String? = null,
    categoryFilter: List<String>? = null,
) {
    var selected by remember { mutableStateOf<LedgerEntry?>(null) }
    var entries by remember { mutableStateOf(account.entries) }

    LaunchedEffect(accountId, account.currency) {
        if (accountId != null) {
            entries = WalletRepository().ledger(accountId, account.currency, categoryFilter)
        }
    }

    val currentEntry = selected
    if (currentEntry != null) {
        TransactionDetailScreen(entry = currentEntry, onBack = { selected = null })
        return
    }

    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = account.title, onBack = onBack)

        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(14.dp))

            // 帐号 + 复制
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(LocalStrings.current.s0395, fontSize = 13.sp, color = AcctMuted)
                Spacer(Modifier.width(10.dp))
                Text(
                    account.accountNo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AcctInk,
                    modifier = Modifier.weight(1f),
                )
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AcctChip)
                        .clickable {
                            clipboard.setText(AnnotatedString(account.accountNo))
                            copied = true
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = LocalStrings.current.s0025, tint = AcctGreen, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(LocalStrings.current.s0025, fontSize = 12.sp, color = AcctGreen, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 可用余额 / 当前余额
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AcctCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(LocalStrings.current.s0333.fill(account.currency), fontSize = 12.sp, color = AcctMuted)
                        Spacer(Modifier.height(8.dp))
                        Text(account.available, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = AcctInk)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(LocalStrings.current.s0414.fill(account.currency), fontSize = 12.sp, color = AcctMuted)
                        Spacer(Modifier.height(8.dp))
                        Text(account.current, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = AcctInk)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 该账户交易流水记录
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AcctCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        LocalStrings.current.s0252,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AcctInk,
                        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                    )
                    if (entries.isEmpty()) {
                        Text(LocalStrings.current.s0513, fontSize = 13.sp, color = AcctMuted, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        entries.forEachIndexed { index, entry ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { selected = entry }
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(entry.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AcctInk)
                                    Spacer(Modifier.height(4.dp))
                                    Text(entry.date, fontSize = 12.sp, color = AcctMuted)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        formatMoney(entry.currency, entry.amount),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AcctInk,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(entry.status, fontSize = 12.sp, color = AcctGreen)
                                }
                            }
                            if (index != entries.lastIndex) {
                                HorizontalDivider(color = AcctDivider, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (copied) {
        AlertDialog(
            onDismissRequest = { copied = false },
            containerColor = AcctCard,
            title = { Text(LocalStrings.current.s0375, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AcctInk) },
            text = { Text(LocalStrings.current.s0396, fontSize = 14.sp, color = AcctMuted) },
            confirmButton = { TextButton(onClick = { copied = false }) { Text(LocalStrings.current.confirm, color = AcctGreen) } },
        )
    }
}
