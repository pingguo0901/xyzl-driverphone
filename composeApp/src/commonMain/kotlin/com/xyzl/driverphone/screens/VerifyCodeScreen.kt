package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.sanitizeCode
import kotlinx.coroutines.delay

/** 验证码页：显示邮箱 + 6 位验证码 + 重发（60 秒倒计时） */
@Composable
fun VerifyCodeScreen(
    email: String,
    onBack: () -> Unit = {},
    onConfirm: (String) -> Unit = {},
    onResend: (String) -> Unit = {},
) {
    var code by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf(60) }

    LaunchedEffect(seconds) {
        if (seconds > 0) {
            delay(1000)
            seconds -= 1
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "验证码", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Text("验证码已发送至", color = FormMuted, fontSize = 13.sp, modifier = Modifier.padding(start = 2.dp))
        Text(
            email.ifBlank { "—" },
            color = FormInk,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 2.dp, top = 4.dp, bottom = 16.dp),
        )

        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = sanitizeCode(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("6 位验证码", color = FormMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = formFieldColors(),
            )
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (seconds == 0) FormGreen else FormChip)
                    .clickable(enabled = seconds == 0) {
                        seconds = 60
                        onResend(email)
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (seconds > 0) "重发 " + seconds + "s" else "重发",
                    color = if (seconds == 0) androidx.compose.ui.graphics.Color.White else FormMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        FormPrimaryButton(
            text = "确认",
            onClick = { onConfirm(code) },
            enabled = code.length == 6,
        )
        Spacer(Modifier.height(110.dp))
    }
}
