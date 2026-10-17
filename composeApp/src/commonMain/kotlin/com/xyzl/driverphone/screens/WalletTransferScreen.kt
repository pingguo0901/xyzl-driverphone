package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.PayeeInfo
import com.xyzl.driverphone.WalletInfo
import com.xyzl.driverphone.WalletRepository
import com.xyzl.driverphone.pickBirthday
import com.xyzl.driverphone.walletBalanceText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TrCard: Color @Composable get() = LocalAppColors.current.card
private val TrChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val TrInk: Color @Composable get() = LocalAppColors.current.ink
private val TrMuted: Color @Composable get() = LocalAppColors.current.muted
private val TrGreen = Color(0xFF3B82F6)
private val TrDivide: Color @Composable get() = LocalAppColors.current.surfaceVariant

private data class TransferAccount(
    val label: String,
    val currency: String,
    val amount: String,
    val accountNo: String,
    val walletId: String,
    val accountId: String,
)

private fun transferAccounts(wallets: List<WalletInfo>): List<TransferAccount> = wallets.map { w ->
    TransferAccount(
        label = w.title,
        currency = w.currency,
        amount = walletBalanceText(w.currency, w.totalBalance),
        accountNo = w.accountNumber,
        walletId = w.id,
        accountId = w.accountId,
    )
}

/** 金额（分）→ 始终两位小数，如 1234 → 12.34 */
private fun formatCents(cents: Long): String {
    val c = if (cents < 0) 0L else cents
    return (c / 100).toString() + "." + (c % 100).toString().padStart(2, '0')
}

private fun centsLabel(currency: String, cents: Long): String = currency + " " + formatCents(cents)

/** 转账：表单（4 步）→ 确认详情 → 时间线 */
@Composable
fun WalletTransferTab(userId: String? = null, wallets: List<WalletInfo> = emptyList()) {
    val accounts = transferAccounts(wallets)
    val repo = remember { WalletRepository() }
    val scope = rememberCoroutineScope()
    var payees by remember { mutableStateOf<List<PayeeInfo>>(emptyList()) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) payees = repo.payees(userId)
    }

    var phase by remember { mutableStateOf(0) } // 0 表单, 1 确认, 2 时间线
    var step by remember { mutableStateOf(0) } // 当前展开步骤 0=全收起
    var fromIndex by remember { mutableStateOf<Int?>(null) }
    var toPayee by remember { mutableStateOf<PayeeInfo?>(null) }
    var toName by remember { mutableStateOf<String?>(null) }
    var toNo by remember { mutableStateOf<String?>(null) }
    var currency by remember { mutableStateOf("MYR") }
    var amountCents by remember { mutableStateOf(0L) }
    var remark by remember { mutableStateOf("") }
    var payDate by remember { mutableStateOf(currentTimeParts().first) }
    var showNewTransfer by remember { mutableStateOf(false) }
    var doneSteps by remember { mutableStateOf(false) }

    val canComplete = payDate.isNotBlank() && toNo != null && fromIndex != null && amountCents > 0

    if (phase == 0 && showNewTransfer) {
        NewTransferView(
            payees = payees,
            onBack = { showNewTransfer = false },
            onPicked = { payee ->
                toPayee = payee
                toName = payee.name
                toNo = payee.accountNumber
                showNewTransfer = false
                step = 3
            },
        )
        return
    }

    when (phase) {
        0 -> Column(Modifier.fillMaxSize()) {
            Card(
                Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TrCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
                        if (doneSteps) {
                            TransferSummary(
                                fromAcc = fromIndex?.let { accounts.getOrNull(it) },
                                toNo = toNo,
                                currency = currency,
                                amountCents = amountCents,
                                remark = remark,
                                payDate = payDate,
                                onEdit = { doneSteps = false; step = 1 },
                            )
                        } else {
                            StepRow(1, "从", fromSummary(fromIndex, accounts), step == 1, { step = if (step == 1) 0 else 1 }) {
                                if (accounts.isEmpty()) {
                                    Text(LocalStrings.current.s0518, fontSize = 13.sp, color = TrMuted, modifier = Modifier.padding(vertical = 12.dp))
                                }
                                accounts.forEachIndexed { index, acc ->
                                    PayeeRow(
                                        title = acc.label,
                                        subtitle = acc.currency + " ｜ " + acc.amount,
                                    ) {
                                        fromIndex = index
                                        currency = acc.currency
                                        step = 2
                                    }
                                }
                            }
                            StepRow(2, "给", toSummary(toName), step == 2, { step = if (step == 2) 0 else 2 }) {
                                Row(
                                    Modifier.fillMaxWidth().clickable { showNewTransfer = true }.padding(vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = TrGreen, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(LocalStrings.current.s0485, fontSize = 14.sp, color = TrGreen, fontWeight = FontWeight.Medium)
                                }
                                if (payees.isEmpty()) {
                                    Text(LocalStrings.current.s0511, fontSize = 13.sp, color = TrMuted, modifier = Modifier.padding(vertical = 12.dp))
                                }
                                payees.forEach { payee ->
                                    PayeeRow(title = payee.name, subtitle = payee.accountNumber) {
                                        toPayee = payee
                                        toName = payee.name
                                        toNo = payee.accountNumber
                                        step = 3
                                    }
                                }
                            }
                            StepRow(3, "转账金额", amountSummary(currency, amountCents), step == 3, { step = if (step == 3) 0 else 3 }) {
                                TransferAmountPad(
                                    currency = currency,
                                    amountCents = amountCents,
                                    onDigit = { d -> amountCents = (amountCents * 10 + d).coerceAtMost(99_999_999L) },
                                    onDelete = { amountCents /= 10 },
                                    onDone = { if (amountCents > 0) step = 4 },
                                )
                            }
                            StepRow(4, "其他详情", payDate, step == 4, { step = if (step == 4) 0 else 4 }) {
                                FormTextField(
                                    label = LocalStrings.current.s0123,
                                    value = remark,
                                    placeholder = LocalStrings.current.s0673,
                                    onValueChange = { remark = it },
                                )
                                Row(
                                    Modifier.fillMaxWidth().clickable { pickBirthday { payDate = it } }.padding(vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(LocalStrings.current.s0268, fontSize = 15.sp, color = TrInk, modifier = Modifier.weight(1f))
                                    Text(
                                        payDate.ifBlank { LocalStrings.current.s0041 },
                                        fontSize = 14.sp,
                                        color = if (payDate.isBlank()) TrMuted else TrInk,
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                    // 其他详情的完成按钮：固定在卡片最底部
                    if (!doneSteps && step == 4) {
                        FormPrimaryButton(
                            text = "完成",
                            enabled = canComplete,
                            onClick = { doneSteps = true; step = 0 },
                            modifier = Modifier.padding(horizontal = 6.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            if (doneSteps) {
                Spacer(Modifier.height(12.dp))
                FormPrimaryButton(text = LocalStrings.current.s0628, onClick = { phase = 1 })
            }
            Spacer(Modifier.height(12.dp))
        }
        1 -> TransferConfirm(
            fromAcc = fromIndex?.let { accounts.getOrNull(it) },
            toName = toName,
            toNo = toNo,
            currency = currency,
            amountCents = amountCents,
            submitting = submitting,
            onConfirm = {
                val from = fromIndex?.let { accounts.getOrNull(it) }
                val to = toPayee
                val uid = userId
                if (from == null || to == null || uid == null) {
                    error = "转账信息不完整，请返回检查"
                } else {
                    submitting = true
                    scope.launch {
                        val result = repo.createTransfer(
                            userId = uid,
                            from = WalletInfo(from.walletId, from.accountId, "", from.currency, 0.0, 0.0, from.accountNo),
                            to = to,
                            amount = amountCents / 100.0,
                            remark = remark,
                            paymentDate = payDate,
                        )
                        submitting = false
                        result.fold(
                            onSuccess = { phase = 2 },
                            onFailure = { error = it.message ?: "转账提交失败" },
                        )
                    }
                }
            },
        )
        else -> TransferTimeline(
            onFinish = {
                phase = 0
                doneSteps = false
                step = 0
                fromIndex = null
                toPayee = null
                toName = null
                toNo = null
                amountCents = 0
                remark = ""
                payDate = currentTimeParts().first
                currency = "MYR"
            },
        )
    }

    val message = error
    if (message != null) {
        AlertDialog(
            onDismissRequest = { error = null },
            containerColor = TrCard,
            title = { Text(LocalStrings.current.s0709, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TrInk) },
            text = { Text(message, fontSize = 14.sp, color = TrMuted) },
            confirmButton = { TextButton(onClick = { error = null }) { Text(LocalStrings.current.confirm, color = TrGreen) } },
        )
    }
}

private fun fromSummary(index: Int?, accounts: List<TransferAccount>): String? =
    if (index == null) null else accounts.getOrNull(index)?.label

private fun toSummary(name: String?): String? = name

private fun amountSummary(currency: String, cents: Long): String? =
    if (cents <= 0) null else centsLabel(currency, cents)

@Composable
private fun StepNumber(index: Int) {
    Box(
        Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TrChip)
            .border(1.dp, TrGreen, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text("$index", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TrGreen)
    }
}

@Composable
private fun StepRow(
    index: Int,
    title: String,
    summary: String?,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().clickable { onToggle() }.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepNumber(index)
            Spacer(Modifier.width(12.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TrInk, modifier = Modifier.weight(1f))
            if (summary != null) {
                Text(summary, fontSize = 13.sp, color = TrMuted)
                Spacer(Modifier.width(8.dp))
            }
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TrMuted,
                modifier = Modifier.size(20.dp).rotate(if (expanded) 180f else 0f),
            )
        }
        if (expanded) {
            Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) { content() }
        }
        HorizontalDivider(color = TrDivide, thickness = 1.dp)
    }
}

@Composable
private fun PayeeRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TrInk)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, fontSize = 12.sp, color = TrMuted)
        }
    }
}

/** 步骤3 数字键盘：从右到左输入，金额始终两位小数 */
@Composable
private fun TransferAmountPad(
    currency: String,
    amountCents: Long,
    onDigit: (Long) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(LocalStrings.current.s0076, fontSize = 12.sp, color = TrMuted)
        Spacer(Modifier.height(4.dp))
        Text(currency, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
        Spacer(Modifier.height(12.dp))
        Text(LocalStrings.current.s0042, fontSize = 12.sp, color = TrMuted)
        Spacer(Modifier.height(4.dp))
        Text(formatCents(amountCents), fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = TrDivide, thickness = 1.dp)
        Spacer(Modifier.height(10.dp))
        val rows = listOf(
            listOf("7", "8", "9"),
            listOf("4", "5", "6"),
            listOf("1", "2", "3"),
            listOf("<", "0", "OK"),
        )
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (key == "OK") TrGreen else TrChip)
                            .clickable {
                                when (key) {
                                    "<" -> onDelete()
                                    "OK" -> onDone()
                                    else -> onDigit(key.toLong())
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key == "<") {
                            Icon(Icons.Filled.Close, contentDescription = LocalStrings.current.s0024, tint = TrInk, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                key,
                                fontSize = if (key == "OK") 15.sp else 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (key == "OK") Color.White else TrInk,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

/** 新转账页：帐户号码搜索 */
@Composable
private fun NewTransferView(
    payees: List<PayeeInfo>,
    onBack: () -> Unit,
    onPicked: (PayeeInfo) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PayeeInfo?>(null) }
    var searched by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        FormTextField(
            label = LocalStrings.current.s0397,
            value = query,
            placeholder = LocalStrings.current.s0675,
            onValueChange = { query = it },
            keyboardNumber = true,
        )
        FormPrimaryButton(
            text = LocalStrings.current.s0064,
            enabled = query.isNotBlank(),
            onClick = {
                searched = true
                val q = query.trim()
                result = payees.firstOrNull { it.accountNumber.replace(" ", "").contains(q.replace(" ", "")) }
            },
        )
        Spacer(Modifier.height(16.dp))
        if (searched) {
            val found = result
            if (found != null) {
                PayeeRow(title = found.name, subtitle = found.accountNumber) { onPicked(found) }
            } else {
                Text(LocalStrings.current.s0533, fontSize = 13.sp, color = TrMuted)
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) { Text("返回", color = TrMuted) }
    }
}

@Composable
private fun TransferSummary(
    fromAcc: TransferAccount?,
    toNo: String?,
    currency: String,
    amountCents: Long,
    remark: String,
    payDate: String,
    onEdit: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        SummaryRow("1  从", listOfNotNull(fromAcc?.label, fromAcc?.accountNo))
        SummaryRow("2  给", listOfNotNull(toNo))
        SummaryRow("3  转账金额", listOf(centsLabel(currency, amountCents)))
        SummaryRow("4  其他详情", listOfNotNull(remark.takeIf { it.isNotBlank() }, payDate.takeIf { it.isNotBlank() }))
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = onEdit) { Text(LocalStrings.current.s0294, color = TrGreen) }
    }
}

@Composable
private fun SummaryRow(label: String, lines: List<String>) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TrGreen, modifier = Modifier.width(96.dp))
        Column(Modifier.weight(1f)) {
            lines.forEach { line ->
                Text(line, fontSize = 14.sp, color = TrInk)
                Spacer(Modifier.height(3.dp))
            }
        }
    }
    HorizontalDivider(color = TrDivide, thickness = 1.dp)
}

/** 确认详情页 */
@Composable
private fun TransferConfirm(
    fromAcc: TransferAccount?,
    toName: String?,
    toNo: String?,
    currency: String,
    amountCents: Long,
    submitting: Boolean = false,
    onConfirm: () -> Unit,
) {
    val payDate = remember { currentTimeParts().first }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            ConfirmRow(left = "从", right = fromAcc?.label ?: "-", sub = fromAcc?.accountNo)
            ConfirmRow(left = "给", right = toName ?: "-", sub = toNo)
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = TrDivide, thickness = 1.dp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(LocalStrings.current.s0711, fontSize = 14.sp, color = TrMuted, modifier = Modifier.weight(1f))
                Text(payDate, fontSize = 14.sp, color = TrInk)
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(LocalStrings.current.s0184, fontSize = 14.sp, color = TrMuted, modifier = Modifier.weight(1f))
                Text(centsLabel(currency, amountCents), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
            }
        }
        FormPrimaryButton(text = if (submitting) LocalStrings.current.s0062 else LocalStrings.current.s0617, enabled = !submitting, onClick = onConfirm)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ConfirmRow(left: String, right: String, sub: String?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(left, fontSize = 14.sp, color = TrMuted, modifier = Modifier.width(60.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(right, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
            if (sub != null) {
                Spacer(Modifier.height(3.dp))
                Text(sub, fontSize = 12.sp, color = TrMuted)
            }
        }
    }
}

/** 转账时间线页 */
@Composable
private fun TransferTimeline(onFinish: () -> Unit) {
    var progress by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (progress < 4) {
            delay(800)
            progress += 1
        }
    }
    val steps = listOf("我方待发送", "我方已发送", "对方待接收", "对方已接收", "完成")
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            steps.forEachIndexed { index, label ->
                val done = index < progress || (index == 4 && progress >= 4)
                Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(label, fontSize = 15.sp, color = if (done) TrInk else TrMuted, modifier = Modifier.weight(1f))
                    if (done) {
                        Icon(Icons.Filled.Check, contentDescription = "完成", tint = TrGreen, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = LocalStrings.current.s0077, tint = TrMuted, modifier = Modifier.size(20.dp))
                    }
                }
                HorizontalDivider(color = TrDivide, thickness = 1.dp)
            }
        }
        FormPrimaryButton(text = "完成", onClick = onFinish)
        Spacer(Modifier.height(12.dp))
    }
}
