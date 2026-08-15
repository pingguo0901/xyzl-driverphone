package com.stellarelite.driver.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.components.AppIcon
import com.stellarelite.driver.ui.theme.DriverColors
import kotlinx.coroutines.delay

// ─── DATA MODELS ───

data class DriverUser(
    val id: String,
    val email: String,
    val driverId: String = "",
    val realName: String = "",
    val username: String = "",
    val phone: String = "",
    val gender: String = "男",
    val dob: String = "",
    val wechat: String = "",
    val street: String = "",
    val taman: String = "",
    val city: String = "",
    val state: String = "",
    val postcode: String = "",
    val bio: String = "",
    val faceUrl: String = "",
    val role: String = "DR",
    val status: String = "approved",
    val walletBalance: Double = 0.0,
    val collectedSgd: Double = 0.0,
    val collectedRm: Double = 0.0
)

data class DriverTrip(
    val id: String,
    val customerName: String = "",
    val pickupAddress: String = "",
    val destinationAddress: String = "",
    val status: String = "pending",
    val departureTime: String = "",
    val driverSalary: Double = 0.0,
    val cashReceivedSgd: Double = 0.0,
    val cashReceivedRm: Double = 0.0,
    val contactPhone: String = "",
    val contactWechat: String = ""
)

data class DriverVehicle(
    val id: String,
    val plateNo: String = "",
    val brand: String = "",
    val model: String = "",
    val isActive: Boolean = false,
    val seats: Int = 7,
    val status: String = "approved"
)

data class FinancialRequest(
    val id: String,
    val type: String = "SJ",
    val orderNo: String = "",
    val amount: Double = 0.0,
    val currency: String = "RM",
    val method: String = "CASH",
    val status: String = "pending",
    val accountName: String = "",
    val accountNumber: String = "",
    val remark: String = "",
    val createdAt: String = ""
)

data class LedgerEntry(
    val id: String,
    val type: String = "",
    val amount: Double = 0.0,
    val currency: String = "RM",
    val note: String = "",
    val balanceBefore: Double = 0.0,
    val balanceAfter: Double = 0.0,
    val createdAt: String = ""
)

data class ExpenseEntry(
    val id: String,
    val date: String = "",
    val type: String = "加油",
    val amount: Double = 0.0,
    val currency: String = "RM",
    val note: String = "",
    val receiptUrl: String = ""
)

// ─── SPLASH / LAUNCH SCREEN ───

@Composable
fun LaunchScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppIcon(size = 120)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "星域司导",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "司机端",
                color = DriverColors.Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )
        }
    }
}

// ─── LANDING SCREEN ───

@Composable
fun LandingScreen(onEnterSystem: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        // Background cover with logo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(size = 160)
        }

        // Bottom overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000), Color(0xFF000000))
                    )
                )
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "星域司导",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "STAR REACH VIBE DRIVER",
                color = DriverColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(DriverColors.Primary)
                    .clickable { onEnterSystem() },
                contentAlignment = Alignment.Center
            ) {
                Text("进入系统", color = Color.Black, fontSize = 17.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

// ─── LOGIN SCREEN ───

@Composable
fun LoginScreen(
    onLoginSuccess: (DriverUser) -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showPwd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Logo
        AppIcon(
            size = 80,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "欢迎回来",
            color = DriverColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            "登录你的司机账号",
            color = DriverColors.TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Email
        Text(
            "邮箱 / Email",
            color = DriverColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        BasicTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DriverColors.Surface)
                .padding(16.dp),
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(DriverColors.Primary),
            singleLine = true,
            decorationBox = { inner ->
                Box {
                    if (email.isEmpty()) Text("your@email.com", color = DriverColors.TextDisabled, fontSize = 15.sp)
                    inner()
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password
        Text(
            "密码 / Password",
            color = DriverColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DriverColors.Surface)
                .padding(16.dp),
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(DriverColors.Primary),
            singleLine = true,
            decorationBox = { inner ->
                Box {
                    if (password.isEmpty()) Text("输入密码", color = DriverColors.TextDisabled, fontSize = 15.sp)
                    inner()
                }
            }
        )
        
        // Forgot password
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onForgotPassword() }
                .padding(top = 12.dp, bottom = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text("忘记密码？", color = DriverColors.Primary, fontSize = 13.sp)
        }
        
        // Login button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (email.isNotEmpty() && password.isNotEmpty()) DriverColors.Primary 
                    else DriverColors.SurfaceVariant
                )
                .clickable(enabled = email.isNotEmpty() && password.isNotEmpty()) {
                    loading = true
                    // Simulate login
                    onLoginSuccess(DriverUser(
                        id = "demo-driver-001",
                        email = email,
                        driverId = "demo-driver-001",
                        realName = "Demo Driver",
                        username = "demo",
                        walletBalance = 1250.0,
                        collectedSgd = 340.0,
                        collectedRm = 890.0
                    ))
                },
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                Text("登录中...", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
            } else {
                Text("登 录", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Register link
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("还没有账号？", color = DriverColors.TextMuted, fontSize = 13.sp)
            Text(
                "立即注册",
                color = DriverColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegister() }.padding(start = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ─── REGISTER SCREEN ───

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var realName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("男") }
    var dob by remember { mutableStateOf("") }
    var wechat by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("司机注册", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        Text("加入星域司导车队", color = DriverColors.TextMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Form fields
        val fieldStyle = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DriverColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
        
        // Name
        sectionLabel("真实姓名 / Real Name")
        BasicTextField(value = realName, onValueChange = { realName = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (realName.isEmpty()) Text("护照英文全名", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        sectionSpacer()
        sectionLabel("邮箱 / Email")
        BasicTextField(value = email, onValueChange = { email = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (email.isEmpty()) Text("your@email.com", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        sectionSpacer()
        sectionLabel("手机号")
        BasicTextField(value = phone, onValueChange = { phone = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (phone.isEmpty()) Text("+60 xxx xxxx", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        sectionSpacer()
        sectionLabel("昵称 / Username")
        BasicTextField(value = username, onValueChange = { username = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (username.isEmpty()) Text("显示名称", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        // Gender & DOB
        sectionSpacer()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                sectionLabel("性别")
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)) {
                    listOf("男", "女").forEach { g ->
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                            .background(if (gender == g) DriverColors.Primary else Color.Transparent)
                            .clickable { gender = g }.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(g, color = if (gender == g) Color.Black else DriverColors.TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                sectionLabel("出生日期")
                BasicTextField(value = dob, onValueChange = { dob = it }, modifier = fieldStyle,
                    textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
                    decorationBox = { inner -> Box { if (dob.isEmpty()) Text("YYYY-MM-DD", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
            }
        }
        
        sectionSpacer()
        sectionLabel("微信号")
        BasicTextField(value = wechat, onValueChange = { wechat = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (wechat.isEmpty()) Text("WeChat ID", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        // Password
        sectionSpacer()
        sectionLabel("密码 (8-14位，含大小写+数字+符号)")
        BasicTextField(value = password, onValueChange = { password = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (password.isEmpty()) Text("创建密码", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        sectionSpacer()
        sectionLabel("确认密码")
        BasicTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (confirmPassword.isEmpty()) Text("再次输入", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        // Address
        sectionSpacer()
        sectionLabel("地址搜索 (Google Maps)")
        BasicTextField(value = street, onValueChange = { street = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (street.isEmpty()) Text("输入地址自动填充...", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        sectionSpacer()
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                sectionLabel("城市")
                BasicTextField(value = city, onValueChange = { city = it }, modifier = fieldStyle,
                    textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
                    decorationBox = { inner -> Box { if (city.isEmpty()) Text("城市", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
            }
            Column(modifier = Modifier.weight(1f)) {
                sectionLabel("州")
                BasicTextField(value = state, onValueChange = { state = it }, modifier = fieldStyle,
                    textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
                    decorationBox = { inner -> Box { if (state.isEmpty()) Text("州", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
            }
        }
        
        sectionSpacer()
        sectionLabel("邮编")
        BasicTextField(value = postcode, onValueChange = { postcode = it }, modifier = fieldStyle,
            textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
            cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
            decorationBox = { inner -> Box { if (postcode.isEmpty()) Text("邮编", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
        
        // KYC upload placeholders
        sectionSpacer()
        sectionLabel("身份证/护照（拍照）")
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(14.dp))
            .background(DriverColors.Surface).clickable { },
            contentAlignment = Alignment.Center
        ) { Text("📷 点击拍照上传", color = DriverColors.TextMuted, fontSize = 13.sp) }
        
        sectionSpacer()
        sectionLabel("驾驶证")
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(14.dp))
            .background(DriverColors.Surface).clickable { },
            contentAlignment = Alignment.Center
        ) { Text("📄 点击上传驾驶证", color = DriverColors.TextMuted, fontSize = 13.sp) }
        
        sectionSpacer()
        sectionLabel("人脸拍照")
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(14.dp))
            .background(DriverColors.Surface).clickable { },
            contentAlignment = Alignment.Center
        ) { Text("🤳 点击自拍", color = DriverColors.TextMuted, fontSize = 13.sp) }
        
        sectionSpacer()
        
        // Submit
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(DriverColors.Primary)
                .clickable { loading = true; onRegisterSuccess() },
            contentAlignment = Alignment.Center
        ) {
            Text("提交申请", color = Color.Black, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        
        Text(
            "* 提交后等待后台审核",
            color = DriverColors.TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}

// ─── RESET PASSWORD SCREEN ───

@Composable
fun ResetPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverColors.Background)
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = DriverColors.Primary, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("重置密码", color = DriverColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        if (sent) {
            Text("✅ 重置邮件已发送", color = DriverColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("请检查您的邮箱（包括垃圾箱）", color = DriverColors.TextMuted, fontSize = 14.sp)
        } else {
            sectionLabel("注册邮箱")
            val fieldStyle = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(DriverColors.Surface).padding(horizontal = 16.dp, vertical = 14.dp)
            BasicTextField(value = email, onValueChange = { email = it }, modifier = fieldStyle,
                textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 14.sp),
                cursorBrush = SolidColor(DriverColors.Primary), singleLine = true,
                decorationBox = { inner -> Box { if (email.isEmpty()) Text("your@email.com", color = DriverColors.TextDisabled, fontSize = 14.sp); inner() } })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp))
                .background(if (email.isNotEmpty()) DriverColors.Primary else DriverColors.SurfaceVariant)
                .clickable(enabled = email.isNotEmpty()) { sent = true },
                contentAlignment = Alignment.Center
            ) {
                Text("发送重置邮件", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

// ─── HELPERS ───

@Composable
private fun sectionLabel(text: String) {
    Text(
        text,
        color = DriverColors.TextMuted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun sectionSpacer() {
    Spacer(modifier = Modifier.height(14.dp))
}
