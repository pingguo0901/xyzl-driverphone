package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.sanitizePassword

/** 司机端密码页：原密码 / 新密码 / 确认密码（规则与注册页一致） */
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    var oldPwd by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }

    val canSubmit = oldPwd.length >= 8 && newPwd.length >= 8 &&
        confirmPwd.isNotEmpty() && confirmPwd == newPwd && newPwd != oldPwd

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "司机端密码", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormPasswordField(
            label = "原密码",
            value = oldPwd,
            placeholder = "请输入原密码（至少 8 位）",
            onValueChange = { oldPwd = sanitizePassword(it) },
        )
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
                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
            )
        }

        // 确认密码正下方的「忘记密码？」
        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("忘记密码？", color = FormGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(8.dp))
        FormPrimaryButton(
            text = "确认更改",
            onClick = { showSaved = true },
            enabled = canSubmit,
        )
        Spacer(Modifier.height(110.dp))
    }

    if (showSaved) {
        AlertDialog(
            onDismissRequest = { showSaved = false },
            containerColor = FormCard,
            title = { Text("更改成功", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("司机端密码已更新", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showSaved = false
                    onBack()
                }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
