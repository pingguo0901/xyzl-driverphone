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

private val LanguageOptions = listOf("中文", "英文")

/** 设置语言页：左上角取消 + 右上角完成 + 语言列表（选中右侧绿色打勾） */
@Composable
fun LanguageScreen(onBack: () -> Unit = {}) {
    var selected by remember { mutableStateOf(DisplayPrefs.language) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderCancelDone(
            title = "多语言",
            onCancel = onBack,
            onDone = {
                DisplayPrefs.language = selected
                onBack()
            },
        )
        Spacer(Modifier.height(16.dp))

        FormGroup {
            LanguageOptions.forEachIndexed { index, option ->
                FormCheckRow(
                    label = option,
                    selected = selected == option,
                    onClick = { selected = option },
                )
                if (index != LanguageOptions.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}
