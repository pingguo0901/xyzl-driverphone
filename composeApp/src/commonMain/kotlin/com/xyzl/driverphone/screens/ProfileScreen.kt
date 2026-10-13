package com.xyzl.driverphone.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileCard = Color(0xFF1C1C1E)
private val ProfileChip = Color(0xFF27272A)
private val ProfileInk = Color(0xFFFFFFFF)
private val ProfileMuted = Color(0xFFA1A1AA)
private val ProfileDivider = Color(0xFF27272A)
private val ProfileRed = Color(0xFFF43F5E)

private val sections: List<Pair<String, List<String>>> = listOf(
    "我的信息" to listOf("个人信息", "隐私信息", "车辆信息"),
    "账号管理" to listOf("司机端密码", "登录设备管理", "更多安全设置"),
    "邀请码" to listOf("客户邀请码", "公司邀请码"),
    "支付与收款" to listOf("我的钱包"),
    "通用" to listOf("通知", "界面与显示", "聊天", "聊天记录管理", "我的评价"),
    "平台政策与协议" to listOf(
        "用户服务协议",
        "隐私政策",
        "KYC实名认证合规条款",
        "退款与交易合规政策",
        "数据留存与审计合规政策",
        "免责声明",
        "合规最终说明",
        "终端用户软件许可协议",
    ),
    "帮助与关于" to listOf("帮助与反馈", "关于我们", "关于平台"),
)

/** 我的页：头像卡（右侧二维码按钮）+ 分组菜单 + 退出登录 */
@Composable
fun ProfileScreen(
    onOpenNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenPersonalInfo: () -> Unit = {},
    onOpenPrivacyInfo: () -> Unit = {},
    onOpenVehicleInfo: () -> Unit = {},
    onOpenChangePassword: () -> Unit = {},
    onOpenDeviceManage: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onOpenDisplay: () -> Unit = {},
    onOpenChatSettings: () -> Unit = {},
    onOpenChatRecords: () -> Unit = {},
    onOpenMyReviews: () -> Unit = {},
    onOpenPolicy: (Int) -> Unit = {},
    onOpenQr: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenAboutUs: () -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val onItemClick: (String) -> Unit = { label ->
        when (label) {
            "个人信息" -> onOpenPersonalInfo()
            "隐私信息" -> onOpenPrivacyInfo()
            "车辆信息" -> onOpenVehicleInfo()
            "通知" -> onOpenNotificationSettings()
            "界面与显示" -> onOpenDisplay()
            "司机端密码" -> onOpenChangePassword()
            "登录设备管理" -> onOpenDeviceManage()
            "更多安全设置" -> onOpenSecurity()
            "聊天" -> onOpenChatSettings()
            "聊天记录管理" -> onOpenChatRecords()
            "我的评价" -> onOpenMyReviews()
            "用户服务协议" -> onOpenPolicy(0)
            "隐私政策" -> onOpenPolicy(1)
            "KYC实名认证合规条款" -> onOpenPolicy(2)
            "退款与交易合规政策" -> onOpenPolicy(3)
            "数据留存与审计合规政策" -> onOpenPolicy(4)
            "免责声明" -> onOpenPolicy(5)
            "合规最终说明" -> onOpenPolicy(6)
            "终端用户软件许可协议" -> onOpenPolicy(7)
            "关于平台" -> onOpenAbout()
            "关于我们" -> onOpenAboutUs()
            else -> { }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = ProfileCard,
            title = {
                Text("退出登录", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ProfileInk)
            },
            text = {
                Text("确定要退出当前账号吗？", fontSize = 14.sp, color = ProfileMuted)
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("退出", color = ProfileRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消", color = ProfileMuted)
                }
            },
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        ScreenHeader(title = "我的", onOpenNotifications = onOpenNotifications)
        Spacer(Modifier.height(14.dp))

        // 头像卡：右侧二维码按钮
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ProfileCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(64.dp).background(ProfileChip, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = ProfileMuted,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(DriverProfile.nickname, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ProfileInk)
                    Spacer(Modifier.height(4.dp))
                    Text("ID：" + DriverProfile.driverId, fontSize = 13.sp, color = ProfileMuted)
                    Text("所属公司：" + DriverProfile.company, fontSize = 13.sp, color = ProfileMuted)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ProfileChip)
                        .clickable { onOpenQr() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCode2,
                        contentDescription = "我的二维码",
                        tint = ProfileInk,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        sections.forEach { (title, items) ->
            Spacer(Modifier.height(18.dp))
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ProfileMuted,
                modifier = Modifier.padding(start = 6.dp),
            )
            Spacer(Modifier.height(8.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ProfileCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {
                    items.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(label) }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(label, fontSize = 15.sp, color = ProfileInk, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = ProfileMuted,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        if (index != items.lastIndex) {
                            HorizontalDivider(
                                color = ProfileDivider,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Card(
            Modifier.fillMaxWidth().clickable { showLogoutDialog = true },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ProfileCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ProfileRed)
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}
