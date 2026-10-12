package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class DriverReview(
    val id: String,
    val customer: String,
    val stars: Int,
    val date: String,
    val comment: String,
    val trip: String,
)

private val reviews = listOf(
    DriverReview("r1", "陈太太", 5, "2026-10-11", "司机很准时，车也干净，全程很稳，满意！", "吉隆坡 → 槟城 · 送机"),
    DriverReview("r2", "Mr. Tan", 5, "2026-10-09", "帮忙搬行李，态度好，下次还会预约。", "布城 → 云顶高原"),
    DriverReview("r3", "李小姐", 4, "2026-10-05", "路线熟悉，沟通也顺畅，等待时间稍微久了一点。", "机场接机"),
    DriverReview("r4", "Ahmad", 5, "2026-09-30", "Nice driver, punctual and friendly.", "市区包车 4 小时"),
    DriverReview("r5", "王先生", 4, "2026-09-25", "整体不错，会再介绍给朋友。", "马六甲一日游"),
)

/** 我的评价页：客户对该司机的评价记录列表 */
@Composable
fun MyReviewsScreen(onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "我的评价", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("4.9", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = FormGreen)
                    Spacer(Modifier.height(2.dp))
                    Text("平均评分", fontSize = 12.sp, color = FormMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("共 128 条评价", fontSize = 15.sp, color = FormInk, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("好评率 96%", fontSize = 12.sp, color = FormMuted)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        FormSectionTitle("评价记录")

        reviews.forEach { review ->
            ReviewCard(review)
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun ReviewCard(review: DriverReview) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FormCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(FormGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        review.customer.take(1),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(review.customer, fontSize = 15.sp, color = FormInk, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(review.date, fontSize = 12.sp, color = FormMuted)
                }
                Text(
                    text = "★".repeat(review.stars) + "☆".repeat(5 - review.stars),
                    fontSize = 14.sp,
                    color = FormGreen,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(review.comment, fontSize = 14.sp, color = FormInk, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(review.trip, fontSize = 12.sp, color = FormMuted)
        }
    }
}
