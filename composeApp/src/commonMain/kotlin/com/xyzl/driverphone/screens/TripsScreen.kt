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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.TripItem
import com.xyzl.driverphone.TripRepository
import com.xyzl.driverphone.formatHomeTimestamp

private val OnlineGreen = Color(0xFF3B82F6)
private val InkDark: Color @Composable get() = LocalAppColors.current.ink
private val MutedGray: Color @Composable get() = LocalAppColors.current.muted
private val CancelledRed = Color(0xFFF43F5E)
private val DoneGreen = Color(0xFF22C55E)

private enum class TripFilter(val label: String) {
    ALL("全部"),
    COMPLETED("已完成"),
    INCOMPLETE("未完成"),
    CANCELLED("已取消"),
}

/**
 * 行程页（《行程页面接线表》）
 * 列表：se_order_parents where assigned_driver_id = 我 order by accepted_at desc
 * 起点 / 目的地：se_bookings　·　金额：se_order_bookings 的 settlement_method / customer_pay_amount / customer_pay_currency
 */
@Composable
fun TripsScreen(userId: String?, onOpenNotifications: () -> Unit = {}) {
    val repo = remember { TripRepository() }
    var filter by remember { mutableStateOf(TripFilter.ALL) }
    var trips by remember { mutableStateOf<List<TripItem>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) trips = repo.list(userId)
    }

    val filtered = trips.filter { t ->
        when (filter) {
            TripFilter.ALL -> true
            TripFilter.COMPLETED -> t.completed
            TripFilter.INCOMPLETE -> !t.completed && !t.cancelled
            TripFilter.CANCELLED -> t.cancelled
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
        ScreenHeader(title = "行程", onOpenNotifications = onOpenNotifications)

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = OnlineGreen.copy(alpha = 0.55f), thickness = 1.5.dp)

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TripFilter.entries.forEach { f ->
                FilterChip(
                    label = f.label,
                    selected = filter == f,
                    onClick = { filter = f },
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) {
            Text("暂无行程", fontSize = 14.sp, color = MutedGray, modifier = Modifier.padding(top = 32.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.orderId }) { t -> TripRow(item = t) }
            }
        }
    }
}

@Composable
private fun TripRow(item: TripItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LocalAppColors.current.card)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                formatHomeTimestamp(item.acceptedAt) ?: "—",
                fontSize = 13.sp,
                color = MutedGray,
                modifier = Modifier.weight(1f),
            )
            StatusTag(item)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            item.pickup ?: "—",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "→ " + (item.dropoff ?: "—"),
            fontSize = 15.sp,
            color = InkDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                (item.customerName ?: "客户") + " · " + (item.customerPhone ?: "—"),
                fontSize = 13.sp,
                color = MutedGray,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val money = item.moneyText
            Text(
                money,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (money == "无需收钱") MutedGray else OnlineGreen,
            )
        }
    }
}

@Composable
private fun StatusTag(item: TripItem) {
    val label = if (item.cancelled) "已取消" else (item.executionStatus ?: "待完成")
    val color = when {
        item.cancelled -> CancelledRed
        item.completed -> DoneGreen
        else -> OnlineGreen
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) OnlineGreen else LocalAppColors.current.surfaceVariant
    val fg = if (selected) Color.White else MutedGray
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = fg,
        )
    }
}
