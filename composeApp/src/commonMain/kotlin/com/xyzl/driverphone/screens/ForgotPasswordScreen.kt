package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.isEmailValid
import com.xyzl.driverphone.sanitizeEmail

/** 忘记密码页：邮箱地址 + 发送验证码 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSendCode: (String) -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "忘记密码", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Text(
            "请输入注册时使用的邮箱地址，我们会把重置密码的链接发到该邮箱。",
            color = FormMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 16.dp),
        )

        FormTextField(
            label = "邮箱地址",
            value = email,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { email = sanitizeEmail(it) },
        )

        if (sent) {
            Text(
                "重置邮件已发送到 $email。请到邮箱点开里面的链接，回到 App 就能设置新密码。",
                color = FormMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(start = 2.dp, top = 6.dp, bottom = 12.dp),
            )
            FormPrimaryButton(
                text = "返回登录",
                onClick = onBack,
                enabled = true,
            )
        } else {
            FormPrimaryButton(
                text = "发送重置邮件",
                onClick = { onSendCode(email); sent = true },
                enabled = isEmailValid(email),
            )
        }
        Spacer(Modifier.height(110.dp))
    }
}
