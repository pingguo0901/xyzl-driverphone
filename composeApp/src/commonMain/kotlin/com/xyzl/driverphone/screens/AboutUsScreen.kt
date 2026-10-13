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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 关于我们页：公司简介、合规运营、集团背景、联系方式 */
@Composable
fun AboutUsScreen(onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "关于我们", onBack = onBack)

        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))

            FormSectionTitle("关于我们")
            AboutCard {
                Text(
                    "星域臻旅（Stellar Elite Travel）隶属于 STELLAR ELITE ENTERPRISE，企业注册于马来西亚柔佛，是面向新马两地的跨境出行服务平台。",
                    fontSize = 14.sp,
                    color = FormInk,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "依托新山独特的地理位置，我们聚焦马来西亚 ↔ 新加坡跨境接送、定点包车、预约接送业务。平台打通乘客下单、智能派单、司机调度、线上核验、行程跟踪与线上支付完整链路。",
                    fontSize = 14.sp,
                    color = FormMuted,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(16.dp))
                Text("我们坚持本地合规运营：", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(8.dp))
                AboutBullet("1", "所有平台入驻司机必须完成身份资料提交以及 KYC 实名认证")
                AboutBullet("2", "平台仅作为出行信息调度与订单撮合工具")
                AboutBullet("3", "严格按照马来西亚相关法规处理用户、司机个人资料")
                Spacer(Modifier.height(14.dp))
                Text(
                    "平台本身不直接拥有、运营任何车辆，车辆以及司机均为经过平台审核的注册合作司机。",
                    fontSize = 13.sp,
                    color = FormMuted,
                    lineHeight = 20.sp,
                )
            }

            Spacer(Modifier.height(22.dp))
            FormSectionTitle("集团背景")
            AboutCard {
                Text(
                    "星域臻旅为星域集团旗下业务板块；集团同时覆盖餐饮、科技开发、数字化系统研发等多个业务，旗下科技团队自研整套平台、司机端、乘客端与后台管理系统。",
                    fontSize = 14.sp,
                    color = FormMuted,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(22.dp))
            FormSectionTitle("联系我们")
            AboutCard {
                AboutInfoRow("公司主体", "STELLAR ELITE ENTERPRISE")
                AboutInfoRow("注册编号", "202603215560 (003879535-K)")
                AboutInfoRow("注册地址", "2313, Jalan Dato Sulaiman, Taman Abad, 80250 Johor Bahru, Johor")
                AboutInfoRow("业务地点", "Johor Bahru，柔佛")
                AboutInfoRow("业务", "跨境包车、出行预约、线上出行平台运营")
                AboutInfoRow("WhatsApp", "+601167936058")
                AboutInfoRow("邮箱", "stellarelitexingyuzhenlv@gmail.com")
                AboutInfoRow("国际网站", "www.stellarelite-xingyuzhenlv.com")
                AboutInfoRow("中国网站", "www.cn.stellarelite-xingyuzhenlv.com")
                AboutInfoRow("Facebook", "星域臻旅 Stellar Elite Travel & Transport", last = true)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutCard(content: @Composable () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FormCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun AboutBullet(index: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text("$index.", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = FormGreen)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = FormMuted, lineHeight = 21.sp)
    }
}

@Composable
private fun AboutInfoRow(label: String, value: String, last: Boolean = false) {
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
            Text(label, fontSize = 12.sp, color = FormMuted)
            Spacer(Modifier.height(3.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FormInk, lineHeight = 20.sp)
        }
        if (!last) {
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
        }
    }
}
