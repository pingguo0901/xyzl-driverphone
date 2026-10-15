package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.AuthRepository
import com.xyzl.driverphone.sanitizePassword
import kotlinx.coroutines.launch

/** 密码管理页：全走 Supabase Auth（updateUser { password }），不上表 */
@Composable
fun ChangePasswordScreen(
    email: String? = null,
    onBack: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    val auth = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var oldPwd by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val canSubmit = oldPwd.length >= 6 && newPwd.length >= 8 &&
        confirmPwd.isNotEmpty() && confirmPwd == newPwd && newPwd != oldPwd

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "密码管理", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
        ) {

            FormPasswordField(
                label = "原密码",
                value = oldPwd,
                placeholder = "请输入原密码",
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
            if (newPwd.isNotEmpty() && newPwd == oldPwd) {
                Text(
                    "新密码不能和原密码一样",
                    color = FormRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
                )
            }

            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("忘记密码？", color = FormGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
        }

        FormPrimaryButton(
            text = if (saving) "更改中…" else "确认更改",
            onClick = {
                saving = true
                scope.launch {
                    val ok = auth.verifyPassword(email, oldPwd)
                    if (!ok) {
                        saving = false
                        dialogTitle = "原密码不正确"
                        dialogBody = "请重新输入原密码，或点「忘记密码？」用邮箱重置。"
                        showDialog = true
                        return@launch
                    }
                    val result = auth.updatePassword(newPwd)
                    saving = false
                    if (result.isSuccess) {
                        dialogTitle = "更改成功"
                        dialogBody = "密码已更新，下次登录用新密码。"
                    } else {
                        dialogTitle = "更改失败"
                        dialogBody = result.exceptionOrNull()?.message?.take(120) ?: "稍后重试"
                    }
                    showDialog = true
                }
            },
            enabled = canSubmit && !saving,
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = FormCard,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(dialogBody, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    val success = dialogTitle == "更改成功"
                    showDialog = false
                    if (success) onBack()
                }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
