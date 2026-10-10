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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OnlineGreen = Color(0xFF206A4E)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)
private val CardGray = Color(0xFFF7F7F8)

/** 财务页：今日薪资 + 今日报销/今日单数 + 代收新币/代收马币 + 提交报销 */
@Composable
fun FinanceScreen(
    onOpenLedger: () -> Unit = {},
    onSubmitExpense: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 标题居中 + 右上角流水按钮
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Text(
                "财务",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardGray)
                    .clickable { onOpenLedger() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ReceiptLong,
                    contentDescription = "流水记录",
                    tint = OnlineGreen,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 今日薪资（大卡）
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = OnlineGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("今日薪资", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(8.dp))
                Text("RM 386.00", fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(Modifier.height(14.dp))

        // 今日报销 + 今日单数（平均分布）
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard("今日报销", "RM 128.50", Modifier.weight(1f))
            StatCard("今日单数", "6 单", Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        // 代收新币 + 代收马币（平均分布）
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard("代收新币", "SGD 240.00", Modifier.weight(1f))
            StatCard("代收马币", "RM 1,860.00", Modifier.weight(1f))
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
                "提交报销",
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(label, fontSize = 12.sp, color = MutedGray)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        }
    }
}
