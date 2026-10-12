package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 聊天设置页 */
@Composable
fun ChatSettingsScreen(onBack: () -> Unit = {}) {
    var earpiece by remember { mutableStateOf(true) }
    var separateSend by remember { mutableStateOf(false) }
    var autoDownload by remember { mutableStateOf(true) }
    var keepOriginal by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "聊天", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormSwitchRow(
                label = "使用听筒播放消息",
                checked = earpiece,
                onCheckedChange = { earpiece = it },
            )
        }

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "使用独立的发送按钮",
                checked = separateSend,
                onCheckedChange = { separateSend = it },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("开启后，键盘上的发送按钮会被替换成换行。")

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "自动下载在其他设备查看过的内容",
                checked = autoDownload,
                onCheckedChange = { autoDownload = it },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("包括「图片、视频和文件」。")

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "保留查看过的原图、原视频",
                checked = keepOriginal,
                onCheckedChange = { keepOriginal = it },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("开启后，保留「已发送」和「已接收并查看」的原图原视频在 WeChat。开启前的原图原视频不受影响。")

        Spacer(Modifier.height(110.dp))
    }
}
