package com.xyzl.driverphone.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.brandLogoPainter
import com.xyzl.driverphone.model.Trip
import com.xyzl.driverphone.model.formatRinggit
import com.xyzl.driverphone.openUrl

private val OnlineGreen = Color(0xFF206A4E)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)
private val DividerGray = Color(0xFFE8E8EC)

@Composable
fun TripDetailScreen(trip: Trip, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 左上角返回按钮 + 标题
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F2F4))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = InkDark,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text("行程详情", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        }

        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(20.dp)) {
                // 日期 时间
                DetailRow("日期 时间", "${trip.date} ${trip.time}")

                DetailDivider()

                // 联系方式 WhatsApp / WeChat
                Text("联系方式", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                Spacer(Modifier.height(10.dp))
                ContactLine("WhatsApp", trip.whatsapp, isWhatsApp = true)
                Spacer(Modifier.height(12.dp))
                ContactLine("WeChat", trip.wechat, isWhatsApp = false)

                DetailDivider()

                // 人数
                Text("人数", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth()) {
                    CountCell("大人", trip.adults, Modifier.weight(1f))
                    CountCell("小孩", trip.children, Modifier.weight(1f))
                    CountCell("行李", trip.luggage, Modifier.weight(1f))
                }

                DetailDivider()

                // 起点 / 目的地
                DetailRow("起点", trip.origin)
                Spacer(Modifier.height(12.dp))
                DetailRow("目的地", trip.destination)

                DetailDivider()

                // 收金额 / 薪资 / 状态
                Row(Modifier.fillMaxWidth()) {
                    DetailRow("收金额", formatRinggit(trip.fare), Modifier.weight(1f))
                    DetailRow("薪资", formatRinggit(trip.salary), Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("状态", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                    Spacer(Modifier.width(12.dp))
                    StatusBadge(trip.status)
                }

                DetailDivider()

                // 接人 / 放人时间
                DetailRow("当时接人时间", trip.pickupTime)
                Spacer(Modifier.height(12.dp))
                DetailRow("当时放人时间", trip.dropoffTime)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
    }
}

@Composable
private fun CountCell(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
        Spacer(Modifier.height(4.dp))
        Text(value.toString(), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
    }
}

@Composable
private fun ContactLine(label: String, value: String, isWhatsApp: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .clickable {
                    if (isWhatsApp) {
                        val digits = value.filter { it.isDigit() }
                        openUrl("https://wa.me/" + digits + "?text=Hi%2C%20I%20am%20driver.")
                    } else {
                        openUrl("weixin://")
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = brandLogoPainter(isWhatsApp),
                contentDescription = label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        }
    }
}

@Composable
private fun DetailDivider() {
    Spacer(Modifier.height(16.dp))
    HorizontalDivider(color = DividerGray, thickness = 1.dp)
    Spacer(Modifier.height(16.dp))
}
