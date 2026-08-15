package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.network.SupabaseClient
import com.stellarelite.driver.network.TripDetail
import com.stellarelite.driver.ui.theme.DriverColors

@Composable
fun TripDetailScreen(
    orderNo: String,
    driverId: String,
    onBack: () -> Unit
) {
    var detail by remember { mutableStateOf<TripDetail?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(orderNo, driverId) {
        detail = SupabaseClient.getTripDetail(orderNo, driverId)
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background)) {
        // 顶栏
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(DriverColors.Surface)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = DriverColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("行程详情", color = DriverColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("加载中…", color = DriverColors.TextMuted, fontSize = 13.sp)
            }
            return
        }

        val d = detail
        if (d == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("未找到该行程", color = DriverColors.TextMuted, fontSize = 13.sp)
            }
            return
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ) {
            // 状态 + 金额
            SectionCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("单号 ${d.order_no}", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(statusLabel(d.status), color = statusColor(d.status), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("RM ${formatAmount(d.final_amount)}", color = DriverColors.Primary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("订单金额", color = DriverColors.TextMuted, fontSize = 10.sp)
                    }
                }
            }

            // 路线（多地点）
            SectionCard(title = "行程路线") {
                d.departure_addresses.forEachIndexed { i, addr ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(DriverColors.Primary),
                            contentAlignment = Alignment.Center
                        ) { Text("${i + 1}", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (i == 0) "出发：$addr" else "途经$i：$addr", color = DriverColors.TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                d.destination_addresses.forEachIndexed { i, addr ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(DriverColors.Danger),
                            contentAlignment = Alignment.Center
                        ) { Text("终", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (i == 0) "目的地：$addr" else "送抵$i：$addr", color = DriverColors.TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (d.trips_date.isNotBlank()) {
                    DetailRow("出行日期", d.trips_date)
                }
                if (d.pickup_time.isNotBlank()) DetailRow("接客时间", d.pickup_time)
                if (d.dropoff_time.isNotBlank()) DetailRow("送达时间", d.dropoff_time)
            }

            // 乘客信息（user_profile，必带）
            SectionCard(title = "乘客信息") {
                if (d.customer_name.isNotBlank()) DetailRow("姓名", d.customer_name)
                if (d.customer_email.isNotBlank()) DetailRow("邮箱", d.customer_email)
                if (d.whatsapp.isNotBlank()) DetailRow("WhatsApp", d.whatsapp)
                if (d.wechat.isNotBlank()) DetailRow("微信", d.wechat)
                DetailRow("人数", "成人 ${d.adult} · 儿童 ${d.child}")
                DetailRow("行李", "${d.luggage} 件")
            }

            // 车辆信息
            SectionCard(title = "车辆要求") {
                if (d.vehicle_type.isNotBlank()) DetailRow("车型", d.vehicle_type)
                DetailRow("车辆数", "${d.vehicle_count} 辆")
            }

            // 金额明细
            SectionCard(title = "金额明细") {
                DetailRow("基础价格", "RM ${formatAmount(d.base_price)}")
                if (d.car_upgrade_fee != 0.0) DetailRow("车型升级费", "RM ${formatAmount(d.car_upgrade_fee)}")
                if (d.car_reduce_fee != 0.0) DetailRow("车型降级抵扣", "-RM ${formatAmount(d.car_reduce_fee)}")
                if (d.discount != 0.0) DetailRow("优惠", "-RM ${formatAmount(d.discount)}")
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("实付金额", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("RM ${formatAmount(d.final_amount)}", color = DriverColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            // 我的薪资（salary_records）
            SectionCard(title = "我的薪资", accent = true) {
                val s = d.salary
                DetailRow("底薪", "RM ${formatAmount(s.base_salary)}")
                DetailRow("提成", "RM ${formatAmount(s.bonus)}")
                DetailRow("补助", "RM ${formatAmount(s.allowance)}")
                if (s.deduction != 0.0) DetailRow("扣款", "-RM ${formatAmount(s.deduction)}")
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("应付总额", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("RM ${formatAmount(s.total_payable)}", color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("实发金额", color = DriverColors.TextMuted, fontSize = 13.sp)
                    Text("RM ${formatAmount(s.actual_paid)}", color = DriverColors.Primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                if (s.pay_status.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("发放状态：${s.pay_status}", color = DriverColors.TextMuted, fontSize = 11.sp)
                }
            }

            // 备注
            if (d.notes.isNotBlank()) {
                SectionCard(title = "备注") {
                    Text(d.notes, color = DriverColors.TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun statusLabel(status: String): String = when (status) {
    "completed" -> "已完成"
    "pending" -> "待执行"
    "confirmed" -> "已确认"
    "cancelled" -> "已取消"
    else -> status
}

private fun statusColor(status: String): Color = when (status) {
    "completed" -> DriverColors.Primary
    "pending", "confirmed" -> DriverColors.Warning
    "cancelled" -> DriverColors.Danger
    else -> DriverColors.TextMuted
}

@Composable
private fun SectionCard(title: String? = null, accent: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp).clip(RoundedCornerShape(16.dp))
            .background(if (accent) DriverColors.PrimaryBg else DriverColors.Card)
            .border(1.dp, if (accent) DriverColors.Primary.copy(alpha = 0.3f) else DriverColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        if (title != null) {
            Text(title, color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }
        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = DriverColors.TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
