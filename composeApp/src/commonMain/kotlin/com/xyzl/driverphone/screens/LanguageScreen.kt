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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xyzl.driverphone.AppLanguage
import com.xyzl.driverphone.AppLanguageStore
import com.xyzl.driverphone.LocalStrings

/** 设置语言页：中文 / 英文，选择后全局文案立即切换 */
@Composable
fun LanguageScreen(onBack: () -> Unit = {}) {
    val strings = LocalStrings.current
    var selected by remember { mutableStateOf(AppLanguageStore.language) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderCancelDone(
            title = strings.language,
            onCancel = onBack,
            onDone = {
                AppLanguageStore.set(selected)
                onBack()
            },
        )
        Spacer(Modifier.height(16.dp))

        FormGroup {
            AppLanguage.entries.forEachIndexed { index, option ->
                FormCheckRow(
                    label = if (option == AppLanguage.ZH) "中文" else "English",
                    selected = selected == option,
                    onClick = { selected = option },
                )
                if (index != AppLanguage.entries.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}
