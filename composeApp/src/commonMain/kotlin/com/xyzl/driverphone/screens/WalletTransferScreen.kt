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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.pickBirthday
import kotlinx.coroutines.delay

private val TrCard = Color(0xFF1C1C1E)
private val TrChip = Color(0xFF27272A)
private val TrInk = Color(0xFFFFFFFF)
private val TrMuted = Color(0xFFA1A1AA)
private val TrGreen = Color(0xFF3B82F6)
private val TrDivide = Color(0xFF27272A)

private data class TransferAccount(
    val label: String,
    val currency: String,
    val amount: String,
    val accountNo: String,
)

private val TransferAccounts = listOf(
    TransferAccount("我的钱包", "MYR", "MYR 1,672.90", "8821 0000 1234 5678"),
    TransferAccount("代收 SGD", "SGD", "SGD 386.50", "8821 0000 5678 1234"),
    TransferAccount("代收 MYR", "MYR", "MYR 1,286.40", "8821 0000 4321 8765"),
)

private val PastPayees = listOf(
    "陈女士" to "8821 0000 2222 3333",
    "张先生" to "8821 0000 4444 5555",
    "王先生" to "8821 0000 6666 7777",
)

/** 转账：表单（4 步）→ 确认详情 → 时间线 */
@Composable
fun WalletTransferTab() {
    var phase by remember { mutableStateOf(0) } // 0 表单, 1 确认, 2 时间线
    var step by remember { mutableStateOf(0) } // 当前展开步骤 0=全收起
    var fromIndex by remember { mutableStateOf<Int?>(null) }
    var toName by remember { mutableStateOf<String?>(null) }
    var toNo by remember { mutableStateOf<String?>(null) }
    var currency by remember { mutableStateOf("MYR") }
    var amount by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var payDate by remember { mutableStateOf("") }
    var showNewTransfer by remember { mutableStateOf(false) }
    var doneSteps by remember { mutableStateOf(false) }

    if (phase == 0 && showNewTransfer) {
        NewTransferView(
            onBack = { showNewTransfer = false },
            onPicked = { name, no ->
                toName = name
                toNo = no
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
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    if (doneSteps) {
                        TransferSummary(
                            from = fromIndex,
                            toNo = toNo,
                            currency = currency,
                            amount = amount,
                            remark = remark,
                            payDate = payDate,
                            onEdit = { doneSteps = false; step = 1 },
                        )
                    } else {
                        StepRow(1, "从", fromSummary(fromIndex), step == 1, { step = if (step == 1) 0 else 1 }) {
                            TransferAccounts.forEachIndexed { index, acc ->
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
                                Text("新转账", fontSize = 14.sp, color = TrGreen, fontWeight = FontWeight.Medium)
                            }
                            PastPayees.forEach { (name, no) ->
                                PayeeRow(title = name, subtitle = no) {
                                    toName = name
                                    toNo = no
                                    step = 3
                                }
                            }
                        }
                        StepRow(3, "转账金额", amountSummary(currency, amount), step == 3, { step = if (step == 3) 0 else 3 }) {
                            TransferAmountPad(
                                currency = currency,
                                amount = amount,
                                onDigit = { d -> amount += d },
                                onDelete = { amount = amount.dropLast(1) },
                                onDone = { if (amount.isNotBlank()) step = 4 },
                            )
                        }
                        StepRow(4, "其他详情", if (payDate.isBlank()) null else payDate, step == 4, { step = if (step == 4) 0 else 4 }) {
                            FormTextField(
                                label = "备注",
                                value = remark,
                                placeholder = "请输入备注（选填）",
                                onValueChange = { remark = it },
                            )
                            Row(
                                Modifier.fillMaxWidth().clickable { pickBirthday { payDate = it } }.padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("付款日期", fontSize = 15.sp, color = TrInk, modifier = Modifier.weight(1f))
                                Text(if (payDate.isBlank()) "选择日期" else payDate, fontSize = 14.sp, color = TrMuted)
                            }
                            FormPrimaryButton(
                                text = "完成",
                                enabled = payDate.isNotBlank() && toNo != null && fromIndex != null,
                                onClick = { doneSteps = true; step = 0 },
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
            if (doneSteps) {
                Spacer(Modifier.height(12.dp))
                FormPrimaryButton(text = "继续", onClick = { phase = 1 })
            }
            Spacer(Modifier.height(12.dp))
        }
        1 -> TransferConfirm(
            from = fromIndex,
            toName = toName,
            toNo = toNo,
            currency = currency,
            amount = amount,
            onBack = { phase = 0 },
            onConfirm = { phase = 2 },
        )
        else -> TransferTimeline(
            from = fromIndex,
            toName = toName,
            toNo = toNo,
            currency = currency,
            amount = amount,
            onBack = { phase = 1 },
            onFinish = {
                phase = 0
                doneSteps = false
                step = 0
                fromIndex = null
                toName = null
                toNo = null
                amount = ""
                remark = ""
                payDate = ""
                currency = "MYR"
            },
        )
    }
}

private fun fromSummary(index: Int?): String? =
    if (index == null) null else TransferAccounts.getOrNull(index)?.label

private fun toSummary(name: String?): String? = name

private fun amountSummary(currency: String, amount: String): String? =
    if (amount.isBlank()) null else currency + " " + amount

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
            Text("$index", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TrGreen, modifier = Modifier.width(22.dp))
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

/** 步骤3 数字键盘 */
@Composable
private fun TransferAmountPad(
    currency: String,
    amount: String,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text("货币", fontSize = 12.sp, color = TrMuted)
        Spacer(Modifier.height(4.dp))
        Text(currency, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
        Spacer(Modifier.height(12.dp))
        Text("金额", fontSize = 12.sp, color = TrMuted)
        Spacer(Modifier.height(4.dp))
        Text(if (amount.isBlank()) "0.00" else amount, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
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
                                    else -> onDigit(key)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key == "<") {
                            Icon(Icons.Filled.Close, contentDescription = "删除", tint = TrInk, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                key,
                                fontSize = if (key == "OK") 15.sp else 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
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
private fun NewTransferView(onBack: () -> Unit, onPicked: (String, String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Pair<String, String>?>(null) }
    var searched by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        FormTextField(
            label = "帐户号码",
            value = query,
            placeholder = "请输入对方帐户号码",
            onValueChange = { query = it },
            keyboardNumber = true,
        )
        FormPrimaryButton(
            text = "搜索",
            enabled = query.isNotBlank(),
            onClick = {
                searched = true
                result = if (query.isNotBlank()) "何先生" to query else null
            },
        )
        Spacer(Modifier.height(16.dp))
        if (searched) {
            val found = result
            if (found != null) {
                PayeeRow(title = found.first, subtitle = found.second) { onPicked(found.first, found.second) }
            } else {
                Text("未找到该帐户", fontSize = 13.sp, color = TrMuted)
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) { Text("返回", color = TrMuted) }
    }
}

@Composable
private fun TransferSummary(
    from: Int?,
    toNo: String?,
    currency: String,
    amount: String,
    remark: String,
    payDate: String,
    onEdit: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        SummaryRow("1  从", listOfNotNull(from?.let { TransferAccounts.getOrNull(it)?.label }, from?.let { TransferAccounts.getOrNull(it)?.accountNo }))
        SummaryRow("2  给", listOfNotNull(toNo))
        SummaryRow("3  转账金额", listOf("$currency $amount"))
        SummaryRow("4  其他详情", listOfNotNull(remark.takeIf { it.isNotBlank() }, payDate.takeIf { it.isNotBlank() }))
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = onEdit) { Text("修改", color = TrGreen) }
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
    from: Int?,
    toName: String?,
    toNo: String?,
    currency: String,
    amount: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    val fromAcc = from?.let { TransferAccounts.getOrNull(it) }
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
                Text("转账日期", fontSize = 14.sp, color = TrMuted, modifier = Modifier.weight(1f))
                Text(payDate, fontSize = 14.sp, color = TrInk)
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("转账金额", fontSize = 14.sp, color = TrMuted, modifier = Modifier.weight(1f))
                Text("$currency $amount", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TrInk)
            }
        }
        FormPrimaryButton(text = "确认转账", onClick = onConfirm)
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
private fun TransferTimeline(
    from: Int?,
    toName: String?,
    toNo: String?,
    currency: String,
    amount: String,
    onBack: () -> Unit,
    onFinish: () -> Unit,
) {
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
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "进行中", tint = TrMuted, modifier = Modifier.size(20.dp))
                    }
                }
                HorizontalDivider(color = TrDivide, thickness = 1.dp)
            }
        }
        FormPrimaryButton(text = "完成", onClick = onFinish)
        Spacer(Modifier.height(12.dp))
    }
}
