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
import androidx.compose.material3.HorizontalDivider
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
import com.xyzl.driverphone.TranslateLanguage
import com.xyzl.driverphone.UserPreferences
import kotlinx.coroutines.launch

/** 翻译页：将文字翻译为 + 自动翻译开关（读写 se_notification_preferences） */
@Composable
fun TranslationScreen(
    onBack: () -> Unit = {},
    onOpenTranslateTo: () -> Unit = {},
    userId: String? = null,
) {
    val repo = remember { PreferencesRepository() }
    val scope = rememberCoroutineScope()
    var prefs by remember { mutableStateOf(UserPreferences()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            prefs = repo.load(userId)
            DisplayPrefs.translateTo = prefs.translateTarget.label
        }
    }

    fun save(next: UserPreferences) {
        prefs = next
        DisplayPrefs.translateTo = next.translateTarget.label
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
        FormPageHeader(title = "翻译", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormValueRow(
                label = LocalStrings.current.s0127,
                value = prefs.translateTarget.label,
                onClick = onOpenTranslateTo,
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote(LocalStrings.current.s0348)

        Spacer(Modifier.height(18.dp))
        FormGroup {
            FormSwitchRow(
                label = LocalStrings.current.s0641,
                checked = prefs.autoTranslateChat,
                onCheckedChange = { save(prefs.copy(autoTranslateChat = it)) },
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote(LocalStrings.current.s0406)

        Spacer(Modifier.height(110.dp))
    }
}

/** 将文字翻译为页：zh / en / ms / ta */
@Composable
fun TranslateToScreen(onBack: () -> Unit = {}, userId: String? = null) {
    val repo = remember { PreferencesRepository() }
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf(TranslateLanguage.of(null)) }

    LaunchedEffect(userId) {
        if (userId != null) selected = repo.load(userId).translateTarget
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderCancelDone(
            title = LocalStrings.current.s0127,
            onCancel = onBack,
            onDone = {
                DisplayPrefs.translateTo = selected.label
                val uid = userId
                if (uid != null) {
                    scope.launch {
                        val current = repo.load(uid)
                        repo.save(uid, current.copy(translateTarget = selected))
                    }
                }
                onBack()
            },
        )
        Spacer(Modifier.height(16.dp))

        FormGroup {
            TranslateLanguage.entries.forEachIndexed { index, option ->
                FormCheckRow(
                    label = option.label,
                    selected = selected == option,
                    onClick = { selected = option },
                )
                if (index != TranslateLanguage.entries.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}
