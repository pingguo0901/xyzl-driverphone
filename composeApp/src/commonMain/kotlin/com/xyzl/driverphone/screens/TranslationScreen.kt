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

/** 翻译页：将文字翻译为 + 自动翻译开关 */
@Composable
fun TranslationScreen(
    onBack: () -> Unit = {},
    onOpenTranslateTo: () -> Unit = {},
) {
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
                label = "将文字翻译为",
                value = DisplayPrefs.translateTo,
                onClick = onOpenTranslateTo,
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote("在聊天、网页及图片中使用翻译功能时，文字会被翻译为所选语言。")

        Spacer(Modifier.height(18.dp))
        FormGroup {
            FormSwitchRow(
                label = "自动翻译聊天中收到的消息",
                checked = DisplayPrefs.autoTranslateChat,
                onCheckedChange = { DisplayPrefs.autoTranslateChat = it },
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote("开启后，所有聊天收到的消息会被自动翻译。")

        Spacer(Modifier.height(110.dp))
    }
}

/** 将文字翻译为页：左上角取消 + 右上角完成 + 语言列表 */
@Composable
fun TranslateToScreen(onBack: () -> Unit = {}) {
    var selected by remember { mutableStateOf(DisplayPrefs.translateTo) }
    val options = listOf("中文", "英文")

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderCancelDone(
            title = "将文字翻译为",
            onCancel = onBack,
            onDone = {
                DisplayPrefs.translateTo = selected
                onBack()
            },
        )
        Spacer(Modifier.height(16.dp))

        FormGroup {
            options.forEachIndexed { index, option ->
                FormCheckRow(
                    label = option,
                    selected = selected == option,
                    onClick = { selected = option },
                )
                if (index != options.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}
