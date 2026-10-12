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
import com.xyzl.driverphone.sanitizePassword

/** 重建密码页：新密码 + 确认密码，确认后回到登录页 */
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    var newPwd by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }

    val canSubmit = newPwd.length >= 8 && confirmPwd.isNotEmpty() && confirmPwd == newPwd

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "重建密码", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormPasswordField(
            label = "新密码",
            value = newPwd,
            placeholder = "英文 / 数字 / 符号，至少 8 位",
            onValueChange = { newPwd = sanitizePassword(it) },
        )
        FormPasswordField(
            label = "确认密码",
            value = confirmPwd,
            placeholder = "再次输入新密码",
            onValueChange = { confirmPwd = sanitizePassword(it) },
        )

        if (confirmPwd.isNotEmpty() && confirmPwd != newPwd) {
            Text(
                "两次输入的密码不一致",
                color = FormRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
            )
        }

        Spacer(Modifier.height(8.dp))
        FormPrimaryButton(
            text = "确认",
            onClick = onDone,
            enabled = canSubmit,
        )
        Spacer(Modifier.height(110.dp))
    }
}
