package com.xyzl.driverphone.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 个人详情页：司机 / 客户 / 公司员工 / 公司，底部「发送消息」 */
@Composable
fun PersonDetailScreen(
    personName: String,
    onBack: () -> Unit = {},
    onSendMessage: () -> Unit = {},
) {
    val person = personOf(personName)

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
            Box(Modifier.size(96.dp).clip(CircleShape).background(FormChip), contentAlignment = Alignment.Center) {
                Text(person.name.take(1), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text(person.name, color = FormInk, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(person.kind.label, color = FormMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text("ID：" + person.id, color = FormMuted, fontSize = 13.sp)

            Spacer(Modifier.height(18.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FormCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    person.company?.let {
                        FormValueRow(label = "所属公司", value = it, onClick = {})
                        Spacer(Modifier.height(10.dp))
                    }
                    if (person.kind == PersonKind.Driver || person.kind == PersonKind.Company) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(person.rating ?: "-", color = FormGreen, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Text("平均评分", color = FormMuted, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(person.goodRate ?: "-", color = FormInk, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text("好评率", color = FormMuted, fontSize = 12.sp)
                            }
                        }
                    }
                    if (person.kind == PersonKind.Customer) {
                        Text("最近评价该司机的记录", color = FormInk, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        myRecentReviews.forEach { r ->
                            Text("· " + r, color = FormMuted, fontSize = 13.sp, lineHeight = 19.sp)
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
