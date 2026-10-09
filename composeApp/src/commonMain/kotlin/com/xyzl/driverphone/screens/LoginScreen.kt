package com.xyzl.driverphone.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.brandLogoPainter

// =====================  星域臻旅 登录页面  =====================
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onRegister: () -> Unit = {},
    onForgetPassword: () -> Unit = {},
    onWechatLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    var selectedTabIndex by remember { mutableStateOf(0) } // 0 登录， 1 注册
    var accountText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedPolicy by remember { mutableStateOf(false) }

    // 星空深蓝色渐变
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF081226),
            Color(0xFF0F2040),
            Color(0xFF102244),
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ==========  顶部品牌  ==========
            Spacer(Modifier.height(24.dp))
            Text(
                text = "星域臻旅",
                fontSize = 44.sp,
                color = Color(0xFFD4AF37),
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "— 尊荣邀制 · 至尊之旅 —",
                fontSize = 15.sp,
                color = Color(0xFFE2C260),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 36.dp),
            )

            // ==========  登录 / 注册 卡片容器  ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x22345688),
                ),
                border = BorderStroke(1.dp, Color(0xFF4A72BB)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                ) {
                    // Tab 登录 / 注册
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TabItem(
                            title = "登录",
                            selected = selectedTabIndex == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTabIndex = 0 },
                        )
                        TabItem(
                            title = "注册",
                            selected = selectedTabIndex == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTabIndex = 1 },
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 输入框：手机号 / 邮箱
                    OutlinedTextField(
                        value = accountText,
                        onValueChange = { accountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入手机号 / 邮箱", color = Color(0xFFAAAAAA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = Color(0xFFC8C8C8))
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD6B442),
                            unfocusedBorderColor = Color(0xFF5478AA),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码输入框（眼睛 显示 / 隐藏）
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入密码", color = Color(0xFFAAAAAA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color(0xFFC8C8C8))
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, "切换密码可见", tint = Color(0xFFCCCCCC))
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD6B442),
                            unfocusedBorderColor = Color(0xFF5478AA),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                        singleLine = true,
                    )

                    // 忘记密码 靠右
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "忘记密码？",
                            color = Color(0xFFE6C765),
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onForgetPassword() },
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 金色大登录按钮
                    Button(
                        onClick = {
                            if (selectedTabIndex == 0) {
                                onLoginSuccess()
                            } else {
                                onRegister()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD8B645),
                        ),
                        enabled = agreedPolicy,
                    ) {
                        Text(
                            if (selectedTabIndex == 0) "登录" else "注册",
                            fontSize = 20.sp,
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // 分割线 + 其他登录方式
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF496899)),
                        )
                        Text(
                            "其他登录方式",
                            color = Color(0xFFB0C4E2),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF496899)),
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 微信 + Apple 登录
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        // 微信
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF07C160))
                                .clickable { onWechatLogin() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = brandLogoPainter(isWhatsApp = false),
                                contentDescription = "微信",
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(36.dp))
                        // Apple
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF111111))
                                .clickable { onAppleLogin() },
                            contentAlignment = Alignment.Center,
                        ) {
                            AppleLogo(
                                modifier = Modifier.size(30.dp),
                                tint = Color.White,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // 复选框 用户协议、隐私政策
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = agreedPolicy,
                            onCheckedChange = { agreedPolicy = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFD6B442),
                                uncheckedColor = Color(0xFF889CC0),
                                checkmarkColor = Color(0xFF1A1A1A),
                            ),
                        )
                        Text("我已阅读并同意 ", color = Color(0xFFDDE6F7), fontSize = 12.sp)
                        Text(
                            "《用户协议》",
                            color = Color(0xFFE6C765),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onUserAgreementClick() },
                        )
                        Text(" 与 ", color = Color(0xFFDDE6F7), fontSize = 12.sp)
                        Text(
                            "《隐私政策》",
                            color = Color(0xFFE6C765),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onPrivacyPolicyClick() },
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// 登录 / 注册 Tab 子组件
@Composable
private fun TabItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            color = if (selected) Color.White else Color(0xFF889CC0),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(3.dp)
                    .background(Color(0xFFD8B645)),
            )
        }
    }
}

/** Apple 品牌 Logo（矢量） */
@Composable
private fun AppleLogo(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            // 苹果主体
            moveTo(w * 0.50f, h * 0.28f)
            cubicTo(w * 0.42f, h * 0.28f, w * 0.36f, h * 0.34f, w * 0.36f, h * 0.44f)
            cubicTo(w * 0.36f, h * 0.62f, w * 0.46f, h * 0.80f, w * 0.56f, h * 0.80f)
            cubicTo(w * 0.62f, h * 0.80f, w * 0.66f, h * 0.76f, w * 0.70f, h * 0.76f)
            cubicTo(w * 0.74f, h * 0.76f, w * 0.78f, h * 0.80f, w * 0.84f, h * 0.80f)
            cubicTo(w * 0.94f, h * 0.80f, w * 1.00f, h * 0.62f, w * 1.00f, h * 0.50f)
            cubicTo(w * 1.00f, h * 0.36f, w * 0.90f, h * 0.30f, w * 0.82f, h * 0.30f)
            cubicTo(w * 0.76f, h * 0.30f, w * 0.72f, h * 0.34f, w * 0.68f, h * 0.34f)
            cubicTo(w * 0.64f, h * 0.34f, w * 0.58f, h * 0.28f, w * 0.50f, h * 0.28f)
            close()
            // 叶子
            moveTo(w * 0.52f, h * 0.24f)
            cubicTo(w * 0.52f, h * 0.16f, w * 0.58f, h * 0.08f, w * 0.68f, h * 0.06f)
            cubicTo(w * 0.68f, h * 0.16f, w * 0.62f, h * 0.24f, w * 0.52f, h * 0.24f)
            close()
        }
        drawPath(path, color = tint)
    }
}
