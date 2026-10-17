package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

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
        FormPageHeader(title = LocalStrings.current.s0429, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Text(
            LocalStrings.current.s0677,
            color = FormMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 16.dp),
        )

        FormTextField(
            label = LocalStrings.current.s0079,
            value = email,
            placeholder = LocalStrings.current.s0004,
            onValueChange = { email = sanitizeEmail(it) },
        )

        if (sent) {
            Text(
                LocalStrings.current.s0742.fill(email),
                color = FormMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(start = 2.dp, top = 6.dp, bottom = 12.dp),
            )
            FormPrimaryButton(
                text = LocalStrings.current.s0712,
                onClick = onBack,
                enabled = true,
            )
        } else {
            FormPrimaryButton(
                text = LocalStrings.current.s0331,
                onClick = { onSendCode(email); sent = true },
                enabled = isEmailValid(email),
            )
        }
        Spacer(Modifier.height(110.dp))
    }
}
