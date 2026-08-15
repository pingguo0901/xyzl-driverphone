package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.network.SupabaseClient
import com.stellarelite.driver.network.VehicleRow
import com.stellarelite.driver.ui.theme.DriverColors
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(user: DriverUser?, onLogout: () -> Unit, onNavigateToLogin: () -> Unit) {
    var showProfileEdit by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }
    var showGarage by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("zh") }

    if (showProfileEdit) {
        ProfileEditScreen(user = user, onBack = { showProfileEdit = false })
        return
    }
    if (showSecurity) {
        SecuritySettingsScreen(user = user, onBack = { showSecurity = false })
        return
    }
    if (showPrivacy) {
        KYCFlowScreen(onBack = { showPrivacy = false })
        return
    }
    if (showLanguage) {
        LanguageSettingsScreen(current = language, onSelect = { language = it; showLanguage = false }, onBack = { showLanguage = false })
        return
    }
    if (showGarage) {
        GarageSettingsScreen(user = user, onBack = { showGarage = false })
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("个人中心", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
        }

        // Profile Card
        item {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(DriverColors.Card)
                    .border(1.dp, DriverColors.Border, RoundedCornerShape(20.dp)).padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(DriverColors.PrimaryBg),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", fontSize = 32.sp) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user?.realName ?: user?.username ?: "未设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text(user?.email ?: "", color = DriverColors.TextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DriverColors.PrimaryBg).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("司机 · 已认证", color = DriverColors.Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(">", color = DriverColors.TextMuted, fontSize = 20.sp, modifier = Modifier.clickable { showProfileEdit = true })
                }
            }
        }

        // Quick Info
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                infoCard("📱", "手机", user?.phone ?: "未绑定", Modifier.weight(1f))
                infoCard("💬", "微信", user?.wechat ?: "未绑定", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                infoCard("📍", "地址", if (user?.city != null) "${user.city}, ${user.state}" else "未设置", Modifier.weight(1f))
                infoCard("🎂", "生日", user?.dob ?: "未设置", Modifier.weight(1f))
            }
        }

        // Menu items
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("账户管理", color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }
        item { menuItem("🔐", "安全设置", "邮箱、密码、手机") { showSecurity = true } }
        item { menuItem("🛡️", "隐私授权", "KYC证件、地址") { showPrivacy = true } }
        item { menuItem("🔔", "通知管理", "推送消息设置") { } }
        item { menuItem("🚗", "车库设置", "车辆信息管理") { showGarage = true } }
        item { menuItem("🌐", "多语言设置", if (language == "zh") "中文" else "English") { showLanguage = true } }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("其他", color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }
        item { menuItem("📖", "使用手册", "查看操作指南") { } }
        item { menuItem("ℹ️", "关于", "Version 1.0.0") { } }

        // Logout
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(DriverColors.Danger.copy(alpha = 0.1f))
                .border(1.dp, DriverColors.Danger.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .clickable { onLogout() }.padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text("退出登录", color = DriverColors.Danger, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun infoCard(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(12.dp)
    ) {
        Text("$emoji $label", color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = DriverColors.TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun menuItem(emoji: String, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.Surface), contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 18.sp) }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = DriverColors.TextMuted, fontSize = 11.sp)
        }
        Text(">", color = DriverColors.TextMuted, fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(2.dp))
}

// ─── PROFILE EDIT ───

@Composable
private fun ProfileEditScreen(user: DriverUser?, onBack: () -> Unit) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var gender by remember { mutableStateOf(user?.gender ?: "男") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("编辑资料", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        // Avatar
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(DriverColors.PrimaryBg).align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center) { Text("👤", fontSize = 44.sp) }
        Spacer(modifier = Modifier.height(24.dp))
        val f = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        val ts = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp)
        sectionLabel("昵称")
        BasicTextField(value = username, onValueChange = { username = it }, modifier = f, textStyle = ts, cursorBrush = SolidColor(DriverColors.Primary))
        sectionSpacer()
        sectionLabel("性别")
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)) {
            listOf("男", "女").forEach { g ->
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                    .background(if (gender == g) DriverColors.Primary else Color.Transparent)
                    .clickable { gender = g }.padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Text(g, color = if (gender == g) Color.Black else DriverColors.TextMuted, fontWeight = FontWeight.Bold) }
            }
        }
        sectionSpacer()
        sectionLabel("简介")
        BasicTextField(value = bio, onValueChange = { bio = it }, modifier = f.height(80.dp), textStyle = ts, cursorBrush = SolidColor(DriverColors.Primary),
            decorationBox = { inner -> Box { if (bio.isEmpty()) Text("说点什么吧...", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp)).background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center) { Text("保存修改", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

// ─── SECURITY SETTINGS ───

@Composable
private fun SecuritySettingsScreen(user: DriverUser?, onBack: () -> Unit) {
    var name by remember { mutableStateOf(user?.realName ?: user?.username ?: "") }
    var whatsapp by remember { mutableStateOf(user?.phone ?: "") }
    var wechat by remember { mutableStateOf(user?.wechat ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("安全设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // 头像
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(DriverColors.PrimaryBg).align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) { Text("👤", fontSize = 36.sp) }
            Spacer(Modifier.height(16.dp))

            // 资料信息（除 ID 外均可编辑）
            editField("名称", name) { name = it }
            infoRow("ID", user?.driverId ?: user?.id ?: "")
            editField("WhatsApp", whatsapp) { whatsapp = it }
            editField("微信", wechat) { wechat = it }
            editField("邮箱", email) { email = it }

            // 更改密码
            Spacer(Modifier.height(12.dp))
            Text("更改密码", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            passwordField("原始密码", oldPassword) { oldPassword = it }
            passwordField("新密码", newPassword) { newPassword = it }
            passwordField("确认密码", confirmPassword) { confirmPassword = it }

            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp)).background(DriverColors.Primary).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("保存修改", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun infoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(16.dp)) {
        Text(label, color = DriverColors.TextMuted, fontSize = 13.sp, modifier = Modifier.width(90.dp))
        Text(value, color = DriverColors.TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun editField(label: String, value: String, onChange: (String) -> Unit) {
    Text(label, color = DriverColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
    BasicTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(16.dp),
        textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
        cursorBrush = SolidColor(DriverColors.Primary),
        singleLine = true,
        decorationBox = { inner -> Box { if (value.isEmpty()) Text(label, color = DriverColors.TextDisabled, fontSize = 15.sp); inner() } }
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun passwordField(label: String, value: String, onChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(16.dp),
        textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
        cursorBrush = SolidColor(DriverColors.Primary),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        decorationBox = { inner -> Box { if (value.isEmpty()) Text(label, color = DriverColors.TextDisabled, fontSize = 15.sp); inner() } }
    )
    Spacer(Modifier.height(10.dp))
}

// ─── GARAGE SETTINGS ───

@Composable
private fun GarageSettingsScreen(user: DriverUser?, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val driverId = user?.driverId ?: user?.id ?: ""
    var vehicles by remember { mutableStateOf<List<VehicleRow>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var requestVehicleId by remember { mutableStateOf("") }
    var companyId by remember { mutableStateOf("") }
    var requesting by remember { mutableStateOf(false) }
    var requestResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(driverId) {
        if (driverId.isNotBlank()) {
            vehicles = SupabaseClient.getDriverVehicles(driverId)
        }
        loading = false
    }

    fun addVehicle() {
        if (requestVehicleId.isBlank() || companyId.isBlank() || requesting) return
        scope.launch {
            requesting = true
            val ok = SupabaseClient.addVehicle(requestVehicleId.trim(), companyId.trim())
            requesting = false
            requestResult = if (ok) "✓ 车辆已添加" else "添加失败，请重试"
            if (ok) { requestVehicleId = ""; companyId = "" }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("车库设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // ── 已有权限车辆 ──
            item {
                Text("已有权限车辆", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
            }
            if (loading) {
                item { Text("加载中…", color = DriverColors.TextMuted, fontSize = 13.sp) }
            } else if (vehicles.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🚗", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("请向所属公司请求授权", color = DriverColors.TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(vehicles, key = { it.vehicle_id ?: "" }) { v ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(DriverColors.PrimaryBg), contentAlignment = Alignment.Center
                        ) { Text("🚗", fontSize = 20.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.vehicle_plate ?: "未登记车牌", color = DriverColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text(
                                listOfNotNull(v.vehicle_brand, v.vehicle_model).joinToString(" "),
                                color = DriverColors.TextMuted, fontSize = 12.sp
                            )
                            if (!v.vehicle_type.isNullOrBlank()) Text("${v.vehicle_type}${if (!v.vehicle_color.isNullOrBlank()) " · ${v.vehicle_color}" else ""}", color = DriverColors.TextMuted, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── 请求权限车辆 ──
            item {
                Spacer(Modifier.height(16.dp))
                Text("添加车辆", color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
            }
            item {
                Column {
                    BasicTextField(
                        value = requestVehicleId,
                        onValueChange = { requestVehicleId = it },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(16.dp),
                        textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
                        cursorBrush = SolidColor(DriverColors.Primary),
                        singleLine = true,
                        decorationBox = { inner -> Box { if (requestVehicleId.isEmpty()) Text("输入 vehicle_id", color = DriverColors.TextDisabled, fontSize = 15.sp); inner() } }
                    )
                    Spacer(Modifier.height(10.dp))
                    BasicTextField(
                        value = companyId,
                        onValueChange = { companyId = it },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(16.dp),
                        textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
                        cursorBrush = SolidColor(DriverColors.Primary),
                        singleLine = true,
                        decorationBox = { inner -> Box { if (companyId.isEmpty()) Text("输入 company_id", color = DriverColors.TextDisabled, fontSize = 15.sp); inner() } }
                    )
                    Spacer(Modifier.height(12.dp))
                    val canAdd = requestVehicleId.isNotBlank() && companyId.isNotBlank() && !requesting
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(if (canAdd) DriverColors.Primary else DriverColors.SurfaceVariant)
                            .clickable(enabled = canAdd) { addVehicle() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (requesting) "添加中…" else "确认添加", color = if (canAdd) Color.Black else DriverColors.TextDisabled, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                requestResult?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = if (it.startsWith("✓")) DriverColors.Primary else DriverColors.Danger, fontSize = 12.sp)
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

// ─── LANGUAGE SETTINGS ───

@Composable
private fun LanguageSettingsScreen(current: String, onSelect: (String) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("多语言设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        listOf("zh" to "🇨🇳 中文", "en" to "🇬🇧 English").forEach { (code, label) ->
            val selected = current == code
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(if (selected) DriverColors.PrimaryBg else DriverColors.Surface)
                .clickable { onSelect(code) }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = if (selected) DriverColors.Primary else DriverColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                if (selected) Text("✓", color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun sectionLabel(text: String) {
    Text(text, color = DriverColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
}

@Composable
private fun sectionSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}
