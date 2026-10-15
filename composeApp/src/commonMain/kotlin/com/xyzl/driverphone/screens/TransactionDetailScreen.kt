package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.LedgerKind
import com.xyzl.driverphone.model.formatMoney
import com.xyzl.driverphone.model.signedMoney
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.shareText

private val InkDark: Color @Composable get() = LocalAppColors.current.ink
private val MutedGray: Color @Composable get() = LocalAppColors.current.muted
private val DividerGray: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val AmountGreen = Color(0xFF3B82F6)

/** 交易详情页：金额 + 明细 + 收据分享 */
@Composable
fun TransactionDetailScreen(entry: LedgerEntry, onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderAction(
            title = "交易详情",
            actionLabel = "收据",
            onBack = onBack,
            onAction = {
                val url = entry.receiptUrl
                if (!url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    openUrl(url)
                } else {
                    shareText(receiptText(entry))
                }
            },
        )

        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(28.dp))

            // 金额（货币）
            Text("金额（${entry.currency}）", fontSize = 13.sp, color = MutedGray, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            // -/+ 金额
            Text(
                signedMoney(entry.currency, entry.amount, entry.income),
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (entry.income) AmountGreen else InkDark,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = DividerGray, thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            DetailItem("日期时间", entry.date)
            DetailItem("来自", entry.from)
            DetailItem("类型", entry.kind.label)
            if (entry.kind == LedgerKind.REIMBURSEMENT) {
                entry.expenseType?.let { DetailItem("报销类型", it) }
            }
            entry.orderNo?.let { DetailItem("订单编号", it) }
            DetailItem("交易编号", entry.txNo ?: entry.id)
            entry.remark.takeIf { it.isNotBlank() }?.let { DetailItem("备注", it) }
            DetailItem("状态", entry.status)

            Spacer(Modifier.height(24.dp))
        }

        // 最底部完成按钮
        FormPrimaryButton(text = "完成", onClick = onBack)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 11.dp)) {
        Text(label, fontSize = 13.sp, color = MutedGray, modifier = Modifier.width(92.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = InkDark,
            modifier = Modifier.weight(1f),
        )
    }
}

/** 生成收据文本，供系统分享 / 打印 */
private fun receiptText(entry: LedgerEntry): String {
    val sb = StringBuilder()
    sb.append("星域臻旅 · 交易收据\n")
    sb.append("————————————\n")
    sb.append("金额（${entry.currency}）：${signedMoney(entry.currency, entry.amount, entry.income)}\n")
    sb.append("日期时间：${entry.date}\n")
    sb.append("来自：${entry.from}\n")
    sb.append("类型：${entry.kind.label}\n")
    if (entry.kind == LedgerKind.REIMBURSEMENT) {
        sb.append("报销类型：${entry.expenseType ?: "-"}\n")
    }
    entry.orderNo?.let { sb.append("订单编号：$it\n") }
    sb.append("交易编号：${entry.id}\n")
    if (entry.remark.isNotBlank()) {
        sb.append("备注：${entry.remark}\n")
    }
    sb.append("状态：${entry.status}\n")
    sb.append("————————————\n")
    sb.append("合计：${formatMoney(entry.currency, entry.amount)}")
    return sb.toString()
}
