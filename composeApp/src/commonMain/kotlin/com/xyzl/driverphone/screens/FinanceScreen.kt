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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.xyzl.driverphone.FinanceRepository
import com.xyzl.driverphone.FinanceSummary
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.model.formatMoney
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark: Color @Composable get() = LocalAppColors.current.ink
private val MutedGray: Color @Composable get() = LocalAppColors.current.muted
private val CardGray: Color @Composable get() = LocalAppColors.current.surfaceVariant

/** 财务页：今日薪资 + 今日报销/今日单数 + 代收新币/代收马币 + 提交报销 */
@Composable
fun FinanceScreen(
    userId: String? = null,
    onOpenLedger: () -> Unit = {},
    onSubmitExpense: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
) {
    val repo = remember { FinanceRepository() }
    var summary by remember { mutableStateOf(FinanceSummary()) }

    LaunchedEffect(userId) {
        if (userId == null) return@LaunchedEffect
        val account = repo.accountId(userId)
        summary = repo.summary(userId, account)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 标题居中 + 右上角流水按钮 + 通知铃铛
        ScreenHeader(
            title = LocalStrings.current.tabFinance,
            onOpenNotifications = onOpenNotifications,
            extraEnd = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardGray)
                        .clickable { onOpenLedger() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = LocalStrings.current.s0159,
                        tint = OnlineGreen,
                        modifier = Modifier.size(22.dp),
                    )
                }
            },
        )

        Spacer(Modifier.height(16.dp))

        // 今日薪资（大卡）
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = OnlineGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(LocalStrings.current.s0104, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(8.dp))
                Text(formatMoney("MYR", summary.todaySalary), fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(Modifier.height(14.dp))

        // 今日报销 + 今日单数（平均分布）
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard("今日报销", formatMoney("MYR", summary.todayExpense), Modifier.weight(1f))
            StatCard("今日单数", summary.todayTrips.toString() + " 单", Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        // 当月累计报销 + 当月累计薪资（平均分布）
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard("当月累计报销", formatMoney("MYR", summary.monthExpense), Modifier.weight(1f))
            StatCard("当月累计薪资", formatMoney("MYR", summary.monthSalary), Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        // 代收新币 + 代收马币（平均分布）
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard("代收新币", formatMoney("SGD", summary.sgdCollection), Modifier.weight(1f))
            StatCard("代收马币", formatMoney("MYR", summary.myrCollection), Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // 提交报销按钮
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(OnlineGreen)
                .clickable { onSubmitExpense() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                LocalStrings.current.s0138,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 2.sp,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(label, fontSize = 12.sp, color = MutedGray)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        }
    }
}
