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
        FormPageHeader(title = LocalStrings.current.tabChat, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormSwitchRow(
                label = LocalStrings.current.s0285,
                checked = prefs.chatUseEarpiece,
                onCheckedChange = { save(prefs.copy(chatUseEarpiece = it)) },
            )
        }

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = LocalStrings.current.s0286,
                checked = prefs.chatUseStandaloneSend,
                onCheckedChange = { save(prefs.copy(chatUseStandaloneSend = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote(LocalStrings.current.s0407)

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = LocalStrings.current.s0639,
                checked = prefs.chatAutoDownloadViewed,
                onCheckedChange = { save(prefs.copy(chatAutoDownloadViewed = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote(LocalStrings.current.s0322)

        Spacer(Modifier.height(16.dp))
        FormGroup {
            FormSwitchRow(
                label = LocalStrings.current.s0292,
                checked = prefs.chatKeepOriginalMedia,
                onCheckedChange = { save(prefs.copy(chatKeepOriginalMedia = it)) },
            )
        }
        Spacer(Modifier.height(8.dp))
        FormNote(LocalStrings.current.s0404)

        Spacer(Modifier.height(110.dp))
    }
}
