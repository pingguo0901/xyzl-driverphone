package com.xyzl.driverphone.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.PersonProfile
import com.xyzl.driverphone.ProfileRepository
import com.xyzl.driverphone.ReviewLine
import com.xyzl.driverphone.formatRating
import com.xyzl.driverphone.formatReview
import com.xyzl.driverphone.loadImageBitmap

/** 个人详情页：对方（聊天里点开某人）/ 自己（带最近 3 条评价） */
@Composable
fun PersonDetailScreen(
    personName: String,
    onBack: () -> Unit = {},
    onSendMessage: () -> Unit = {},
    userId: String? = null,
) {
    val repo = remember { ProfileRepository() }
    var profile by remember { mutableStateOf<PersonProfile?>(null) }
    var reviews by remember { mutableStateOf<List<ReviewLine>>(emptyList()) }
    val isSelf = personName == "我" || personName == "我自己"
    val mock = personOf(personName)

    LaunchedEffect(personName, userId) {
        val loaded = if (isSelf) userId?.let { repo.byUserId(it) } else repo.byName(personName)
        profile = loaded
        val target = if (isSelf) userId else loaded?.userId
        reviews = if (target == null) emptyList() else repo.reviews(target, 3)
    }

    val data = profile
    val name = data?.name?.takeIf { it.isNotBlank() } ?: mock.name
    val avatarUrl = data?.avatarUrl
    val roleLabel = data?.roleLabel ?: mock.kind.label
    val displayId = data?.displayId ?: mock.id
    val company = data?.companyName ?: mock.company

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "个人详情", onBack = onBack)
        Spacer(Modifier.height(10.dp))

        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))
            val bitmap = remember(avatarUrl) { avatarUrl?.let { loadImageBitmap(it) } }
            Box(Modifier.size(96.dp).clip(CircleShape).background(FormChip), contentAlignment = Alignment.Center) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(96.dp).clip(CircleShape),
                    )
                } else {
                    Text(name.take(1), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(name, color = FormInk, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(roleLabel, color = FormMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text("ID：" + displayId, color = FormMuted, fontSize = 13.sp)

            Spacer(Modifier.height(18.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FormCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    if (company != null) {
                        FormValueRow(label = "所属公司", value = company, onClick = {})
                        Spacer(Modifier.height(10.dp))
                    }
                    if (data?.rating != null || data?.totalTrips != null) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(formatRating(data?.rating), color = FormGreen, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Text("平均评分", color = FormMuted, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text((data?.totalTrips ?: 0).toString(), color = FormInk, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text("完成单数", color = FormMuted, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Text(
                        if (isSelf) "最近评价我的记录" else "最近评价该司机的记录",
                        color = FormInk, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (reviews.isEmpty()) {
                        Text("暂无评价", color = FormMuted, fontSize = 13.sp)
                    } else {
                        reviews.forEach { line ->
                            Text("· " + formatReview(line), color = FormMuted, fontSize = 13.sp, lineHeight = 19.sp)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        FormPrimaryButton(text = "发送消息", onClick = onSendMessage)
        Spacer(Modifier.height(14.dp))
    }
}
