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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xyzl.driverphone.PreferencesRepository
import com.xyzl.driverphone.UserPreferences
import kotlinx.coroutines.launch

/** 聊天设置页（读写 se_notification_preferences） */
@Composable
fun ChatSettingsScreen(onBack: () -> Unit = {}, userId: String? = null) {
    val repo = remember { PreferencesRepository() }
    val scope = rememberCoroutineScope()
    var prefs by remember { mutableStateOf(UserPreferences()) }

    LaunchedEffect(userId) {
        if (userId != null) prefs = repo.load(userId)
    }

    fun save(next: UserPreferences) {
        prefs = next
        val uid = userId ?: return
        scope.launch { repo.save(uid, next) }
    }

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
                checked = prefs.chatUseEarpiece,
                onCheckedChange = { save(prefs.copy(chatUseEarpiece = it)) },
            )
        }

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "使用独立的发送按钮",
                checked = prefs.chatUseStandaloneSend,
                onCheckedChange = { save(prefs.copy(chatUseStandaloneSend = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("开启后，键盘上的发送按钮会被替换成换行。")

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "自动下载在其他设备查看过的内容",
                checked = prefs.chatAutoDownloadViewed,
                onCheckedChange = { save(prefs.copy(chatAutoDownloadViewed = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("包括「图片、视频和文件」。")

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = "保留查看过的原图、原视频",
                checked = prefs.chatKeepOriginalMedia,
                onCheckedChange = { save(prefs.copy(chatKeepOriginalMedia = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote("开启后，保留「已发送」和「已接收并查看」的原图原视频。开启前的原图原视频不受影响。")

        Spacer(Modifier.height(110.dp))
    }
}
