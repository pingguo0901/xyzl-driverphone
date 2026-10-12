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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onRegister: () -> Unit = {},
    onForgetPassword: () -> Unit = {},
    onWechatLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    var accountText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedPolicy by remember { mutableStateOf(false) }

    // 星空深蓝色渐变
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF0A0A0A),
            Color(0xFF0A0A0A),
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
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ==========  顶部品牌  ==========
            Spacer(Modifier.height(24.dp))
            Text(
                text = "星域臻旅",
                fontSize = 44.sp,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "— 尊荣邀制 · 至尊之旅 —",
                fontSize = 15.sp,
                color = Color(0xFFA1A1AA),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 36.dp),
            )

            // ==========  登录 / 注册 卡片容器  ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1A3B82F6),
                ),
                border = BorderStroke(1.dp, Color(0xFF27272A)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                ) {
                    // 标题：登录（注册入口移到「其他登录方式」下面）
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "登录",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 输入框：手机号 / 邮箱
                    OutlinedTextField(
                        value = accountText,
                        onValueChange = { accountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入手机号 / 邮箱", color = Color(0xFFA1A1AA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = Color(0xFFA1A1AA))
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF27272A),
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
                        placeholder = { Text("请输入密码", color = Color(0xFFA1A1AA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color(0xFFA1A1AA))
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, "切换密码可见", tint = Color(0xFFA1A1AA))
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF27272A),
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
                            color = Color(0xFF3B82F6),
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onForgetPassword() },
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 金色大登录按钮
                    Button(
                        onClick = { onLoginSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                        ),
                        enabled = agreedPolicy,
                    ) {
                        Text(
                            "登录",
                            fontSize = 20.sp,
                            color = Color(0xFFFFFFFF),
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
                                .background(Color(0xFF18181B)),
                        )
                        Text(
                            "其他登录方式",
                            color = Color(0xFFA1A1AA),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF18181B)),
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 微信 + Apple 登录（Canvas 圆形按钮）
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        WeChatButton(onClick = { onWechatLogin() }, size = 64.dp, logoSize = 34.dp)
                        Spacer(modifier = Modifier.width(36.dp))
                        AppleButton(onClick = { onAppleLogin() }, size = 64.dp, logoSize = 34.dp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 没有帐号？注册
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("没有帐号？", color = Color(0xFFA1A1AA), fontSize = 13.sp)
                        Text(
                            "注册",
                            color = Color(0xFF3B82F6),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onRegister() },
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // 复选框 用户协议、隐私政策
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = agreedPolicy,
                            onCheckedChange = { agreedPolicy = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF3B82F6),
                                uncheckedColor = Color(0xFF71717A),
                                checkmarkColor = Color(0xFFFFFFFF),
                            ),
                        )
                        Text("我已阅读并同意 ", color = Color(0xFFFFFFFF), fontSize = 12.sp)
                        Text(
                            "《用户协议》",
                            color = Color(0xFF3B82F6),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onUserAgreementClick() },
                        )
                        Text(" 与 ", color = Color(0xFFFFFFFF), fontSize = 12.sp)
                        Text(
                            "《隐私政策》",
                            color = Color(0xFF3B82F6),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onPrivacyPolicyClick() },
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        // 右上角返回
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 4.dp, end = 8.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
            )
        }
    }
}

