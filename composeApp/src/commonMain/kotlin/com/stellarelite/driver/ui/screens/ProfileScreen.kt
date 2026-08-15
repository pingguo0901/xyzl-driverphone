package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors

@Composable
fun ProfileScreen(user: DriverUser?, onLogout: () -> Unit, onNavigateToLogin: () -> Unit) {
    var showSettings by remember { mutableStateOf(false) }
    var showProfileEdit by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }
    var showGarage by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("zh") }

    if (showSettings) {
        SettingsScreen(
            language = language,
            onBack = { showSettings = false },
            onSecurity = { showSecurity = true; showSettings = false },
            onPrivacy = { showPrivacy = true; showSettings = false },
            onLanguage = { showLanguage = true; showSettings = false },
            onLogout = onLogout
        )
        return
    }
    if (showProfileEdit) {
        ProfileEditScreen(user = user, onBack = { showProfileEdit = false })
        return
    }
    if (showSecurity) {
        SecuritySettingsScreen(onBack = { showSecurity = false; showSettings = true })
        return
    }
    if (showPrivacy) {
        KYCFlowScreen(onBack = { showPrivacy = false })
        return
    }
    if (showLanguage) {
        LanguageSettingsScreen(current = language, onSelect = { language = it; showLanguage = false; showSettings = true }, onBack = { showLanguage = false; showSettings = true })
        return
    }
    if (showGarage) {
        GarageSettingsScreen(onBack = { showGarage = false })
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("个人中心", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(DriverColors.Surface)
                    .clickable { showSettings = true }, contentAlignment = Alignment.Center
                ) { Text("⚙️", fontSize = 18.sp) }
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

// ─── SETTINGS SCREEN ───

@Composable
private fun SettingsScreen(
    language: String,
    onBack: () -> Unit,
    onSecurity: () -> Unit,
    onPrivacy: () -> Unit,
    onLanguage: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("系统设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        LazyColumn {
            item { menuItem("🔐", "安全设置", "邮箱、密码、手机", onSecurity) }
            item { menuItem("🛡️", "隐私授权", "KYC证件", onPrivacy) }
            item { menuItem("🌐", "语言设置", if (language == "zh") "中文" else "English", onLanguage) }
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(DriverColors.Danger.copy(alpha = 0.1f))
                    .clickable { onLogout() }.padding(14.dp),
                    contentAlignment = Alignment.Center
                ) { Text("退出登录", color = DriverColors.Danger, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
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
private fun SecuritySettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("安全设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        val f = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
        val ts = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp)
        sectionLabel("邮箱")
        BasicTextField(value = remember { mutableStateOf("") }.value, onValueChange = {}, modifier = f, textStyle = ts, cursorBrush = SolidColor(DriverColors.Primary),
            decorationBox = { inner -> Box { Text("your@email.com", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        sectionLabel("新密码")
        BasicTextField(value = remember { mutableStateOf("") }.value, onValueChange = {}, modifier = f, textStyle = ts, cursorBrush = SolidColor(DriverColors.Primary),
            decorationBox = { inner -> Box { Text("留空不修改", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        sectionSpacer()
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp)).background(DriverColors.Primary).clickable { onBack() },
            contentAlignment = Alignment.Center) { Text("保存安全设置", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black) }
    }
}

// ─── GARAGE SETTINGS ───

@Composable
private fun GarageSettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("车库设置", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("🚗", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("车辆管理功能开发中", color = DriverColors.TextMuted, fontSize = 14.sp)
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
