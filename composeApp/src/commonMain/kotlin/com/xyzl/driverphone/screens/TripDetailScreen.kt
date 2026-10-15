package com.xyzl.driverphone.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.TripDetail
import com.xyzl.driverphone.TripRepository
import com.xyzl.driverphone.brandLogoPainter
import com.xyzl.driverphone.formatHomeTimestamp
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.waDigits

private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark: Color @Composable get() = LocalAppColors.current.ink
private val MutedGray: Color @Composable get() = LocalAppColors.current.muted
private val DividerGray: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val CancelledRed = Color(0xFFF43F5E)
private val DoneGreen = Color(0xFF22C55E)

/**
 * 行程详情（《行程详情页面接线表》）
 * 日期时间 accepted_at · WhatsApp customer_whatsapp · WeChat customer_wechat_id
 * 大人 adult_count · 小孩 child_count · 行李 se_bookings.luggage_count
 * 起点 se_bookings.pickup_points · 目的地 dropoff_points · 薪资 driver_income · 状态 execution_status
 * 收金额 customer_pay_amount + customer_pay_currency（在 se_order_bookings）
 */
@Composable
fun TripDetailScreen(orderId: String?, onBack: () -> Unit) {
    val repo = remember { TripRepository() }
    var detail by remember { mutableStateOf<TripDetail?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        if (orderId == null) {
            loaded = true
        } else {
            detail = repo.detail(orderId)
            loaded = true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LocalAppColors.current.surfaceVariant)
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
            colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(20.dp)) {
                val data = detail
                if (data == null) {
                    Text(
                        if (loaded) "找不到这单" else "加载中…",
                        fontSize = 14.sp,
                        color = MutedGray,
                    )
                } else {
                    DetailRow("日期 时间", formatHomeTimestamp(data.acceptedAt) ?: "—")

                    DetailDivider()

                    Text("联系方式", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                    Spacer(Modifier.height(10.dp))
                    ContactLine("WhatsApp", data.whatsapp ?: "—", isWhatsApp = true)
                    Spacer(Modifier.height(12.dp))
                    ContactLine("WeChat", data.wechat ?: "—", isWhatsApp = false)

                    DetailDivider()

                    Text("人数", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth()) {
                        CountCell("大人", data.adults ?: 0, Modifier.weight(1f))
                        CountCell("小孩", data.children ?: 0, Modifier.weight(1f))
                        CountCell("行李", data.luggage ?: 0, Modifier.weight(1f))
                    }

                    DetailDivider()

                    PlaceList("起点", data.pickups)
                    Spacer(Modifier.height(12.dp))
                    PlaceList("目的地", data.dropoffs)

                    DetailDivider()

                    Row(Modifier.fillMaxWidth()) {
                        DetailRow("收金额", data.moneyText, Modifier.weight(1f))
                        DetailRow("薪资", data.salaryText, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("状态", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
                        Spacer(Modifier.width(12.dp))
                        StatusBadge(label = data.statusLabel, cancelled = data.cancelled, completed = data.completed)
                    }

                    DetailDivider()

                    DetailRow("当时接人时间", formatHomeTimestamp(data.acceptedAt) ?: "—")
                    Spacer(Modifier.height(12.dp))
                    DetailRow("当时放人时间", formatHomeTimestamp(data.completedAt) ?: "—")
                }
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
private fun PlaceList(label: String, places: List<String>, modifier: Modifier = Modifier) {
    val shown = places.take(10)
    Column(modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = MutedGray)
        Spacer(Modifier.height(4.dp))
        if (shown.isEmpty()) {
            Text("—", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        } else if (shown.size == 1) {
            Text(shown.first(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                shown.forEachIndexed { index, place ->
                    Text(
                        (index + 1).toString() + ". " + place,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InkDark,
                    )
                }
            }
        }
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
private fun StatusBadge(label: String, cancelled: Boolean, completed: Boolean) {
    val color = when {
        cancelled -> CancelledRed
        completed -> DoneGreen
        else -> OnlineGreen
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
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
                    val number = waDigits(value)
                    if (isWhatsApp) {
                        if (number != null) openUrl("https://wa.me/" + number + "?text=Hi%2C%20I%20am%20driver.")
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
