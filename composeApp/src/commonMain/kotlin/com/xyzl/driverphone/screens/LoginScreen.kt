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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.brandLogoPainter

private val NightTop = Color(0xFF0A1A33)
private val NightMid = Color(0xFF0D2547)
private val NightBottom = Color(0xFF123A63)
private val Gold = Color(0xFFD9B36A)
private val GoldBright = Color(0xFFF0D08A)
private val FieldBg = Color(0x1AFFFFFF)
private val FieldBorder = Color(0x33FFFFFF)

/** 登录 / 注册页面：深蓝星空 + 金色品牌 + 账号密码 + 第三方登录 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) } // 0=登录 1=注册
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var agreed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NightTop, NightMid, NightBottom),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(72.dp))

            // 品牌标志：金色流星 + 星星
            BrandMark()

            Spacer(Modifier.height(18.dp))

            Text(
                "星域臻旅",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright,
                letterSpacing = 6.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "尊荣邀制 · 至尊之旅",
                fontSize = 13.sp,
                color = Gold.copy(alpha = 0.85f),
                letterSpacing = 3.sp,
            )

            Spacer(Modifier.height(40.dp))

            // 登录 / 注册 选项卡
            AuthTabs(selected = tab, onSelect = { tab = it })

            Spacer(Modifier.height(28.dp))

            // 账号输入
            AuthField(
                value = account,
                onValueChange = { account = it },
                placeholder = "请输入手机号 / 邮箱",
                leadingIcon = Icons.Filled.Person,
            )

            Spacer(Modifier.height(16.dp))

            // 密码输入
            AuthField(
                value = password,
                onValueChange = { password = it },
                placeholder = "请输入密码",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                showPassword = showPassword,
                onTogglePassword = { showPassword = !showPassword },
            )

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "忘记密码？",
                    fontSize = 13.sp,
                    color = Gold.copy(alpha = 0.9f),
                    modifier = Modifier.clickable { },
                )
            }

            Spacer(Modifier.height(24.dp))

            // 主按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Gold, GoldBright)),
                    )
                    .clickable {
                        if (agreed) onLoginSuccess()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (tab == 0) "登 录" else "注 册",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1206),
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(28.dp))

            // 其他登录方式
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f).height(1.dp).background(Color(0x33FFFFFF)))
                Text(
                    "其他登录方式",
                    fontSize = 12.sp,
                    color = Color(0x99FFFFFF),
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Box(Modifier.weight(1f).height(1.dp).background(Color(0x33FFFFFF)))
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SocialButton(painter = brandLogoPainter(isWhatsApp = false), label = "微信")
                SocialButton(painter = brandLogoPainter(isWhatsApp = true), label = "WhatsApp")
            }

            Spacer(Modifier.height(32.dp))

            // 协议勾选
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (agreed) Gold else Color.Transparent)
                        .clickable { agreed = !agreed },
                    contentAlignment = Alignment.Center,
                ) {
                    if (agreed) {
                        Text("✓", fontSize = 12.sp, color = Color(0xFF1A1206), fontWeight = FontWeight.Bold)
                    } else {
                        Box(
                            Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF)),
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "我已阅读并同意《用户协议》与《隐私政策》",
                    fontSize = 12.sp,
                    color = Color(0xB3FFFFFF),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/** 金色流星 + 星星品牌标志 */
@Composable
private fun BrandMark() {
    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center,
    ) {
        // 光晕
        Box(
            Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Gold.copy(alpha = 0.28f), Color.Transparent),
                    ),
                ),
        )
        // 流星轨迹
        Box(
            Modifier
                .width(64.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Gold.copy(alpha = 0.7f), GoldBright),
                    ),
                ),
        )
        // 星点
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(GoldBright),
        )
    }
}

/** 登录 / 注册 选项卡 */
@Composable
private fun AuthTabs(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        listOf("登录", "注册").forEachIndexed { index, label ->
            val active = index == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelect(index) }
                    .padding(horizontal = 24.dp),
            ) {
                Text(
                    label,
                    fontSize = 17.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (active) GoldBright else Color(0x80FFFFFF),
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (active) GoldBright else Color.Transparent),
                )
            }
        }
    }
}

/** 账号 / 密码输入框 */
@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = {
            Text(placeholder, fontSize = 14.sp, color = Color(0x80FFFFFF))
        },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = Gold.copy(alpha = 0.9f), modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (isPassword) {
            {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier.size(20.dp).clickable { onTogglePassword() },
                )
            }
        } else null,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedBorderColor = Gold.copy(alpha = 0.8f),
            unfocusedBorderColor = FieldBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = GoldBright,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

/** 第三方登录圆形按钮 */
@Composable
private fun SocialButton(painter: androidx.compose.ui.graphics.painter.Painter, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0x1AFFFFFF))
                .clickable { },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painter,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = Color(0x99FFFFFF))
    }
}
