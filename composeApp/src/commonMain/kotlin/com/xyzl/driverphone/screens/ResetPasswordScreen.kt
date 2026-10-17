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
import com.xyzl.driverphone.sanitizePassword

/** 重建密码页：新密码 + 确认密码，确认后回到登录页 */
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit = {},
    onDone: (String) -> Unit = {},
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
        FormPageHeader(title = LocalStrings.current.s0741, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormPasswordField(
            label = LocalStrings.current.s0140,
            value = newPwd,
            placeholder = LocalStrings.current.s0175,
            onValueChange = { newPwd = sanitizePassword(it) },
        )
        FormPasswordField(
            label = LocalStrings.current.s0072,
            value = confirmPwd,
            placeholder = LocalStrings.current.s0115,
            onValueChange = { confirmPwd = sanitizePassword(it) },
        )

        if (confirmPwd.isNotEmpty() && confirmPwd != newPwd) {
            Text(
                LocalStrings.current.s0096,
                color = FormRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
            )
        }

        Spacer(Modifier.height(8.dp))
        FormPrimaryButton(
            text = LocalStrings.current.s0071,
            onClick = { onDone(newPwd) },
            enabled = canSubmit,
        )
        Spacer(Modifier.height(110.dp))
    }
}
