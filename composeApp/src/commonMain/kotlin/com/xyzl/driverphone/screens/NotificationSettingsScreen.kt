package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xyzl.driverphone.PermissionScene
import com.xyzl.driverphone.requestScenePermission

/** 通知设置页：司机端未打开时 / 司机端打开时 */
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit = {}) {
    var systemMessage by remember { mutableStateOf(true) }
    var callWhenClosed by remember { mutableStateOf(true) }
    var inAppBanner by remember { mutableStateOf(true) }
    var messageSound by remember { mutableStateOf(true) }
    var callWhenOpen by remember { mutableStateOf(true) }
    var vibrate by remember { mutableStateOf(true) }

    // 进通知设置时申请通知权限（Android 13+ 才需要）
    LaunchedEffect(Unit) { requestScenePermission(PermissionScene.Notification) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "通知", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormSectionTitle("司机端未打开时")
        FormGroup {
            FormSwitchRow(
                label = "系统消息通知",
                checked = systemMessage,
                onCheckedChange = { systemMessage = it },
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormSwitchRow(
                label = "语音通话通知",
                checked = callWhenClosed,
                onCheckedChange = { callWhenClosed = it },
            )
        }

        Spacer(Modifier.height(18.dp))
        FormSectionTitle("司机端打开时")
        FormGroup {
            FormSwitchRow(
                label = "司机端内消息横幅",
                checked = inAppBanner,
                onCheckedChange = { inAppBanner = it },
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormSwitchRow(
                label = "消息提示音",
                checked = messageSound,
                onCheckedChange = { messageSound = it },
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormSwitchRow(
                label = "语音通话通知",
                checked = callWhenOpen,
                onCheckedChange = { callWhenOpen = it },
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormSwitchRow(
                label = "振动",
                checked = vibrate,
                onCheckedChange = { vibrate = it },
            )
        }

        Spacer(Modifier.height(110.dp))
    }
}
