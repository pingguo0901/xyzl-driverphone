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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 界面与显示设置（跨页面共享，简单本地状态） */
object DisplayPrefs {
    var followSystem by mutableStateOf(true)
    var darkMode by mutableStateOf(true)
    var fontSizeLevel by mutableStateOf(4)
    var language by mutableStateOf("中文")
    var translateTo by mutableStateOf("中文")
    var autoTranslateChat by mutableStateOf(false)

    val modeLabel: String
        get() = if (followSystem) "跟随系统" else if (darkMode) "深色" else "浅色"
}

/** 界面与显示页：模式 / 字体大小 / 多语言 / 翻译 */
@Composable
fun DisplayScreen(
    onBack: () -> Unit = {},
    onOpenDarkMode: () -> Unit = {},
    onOpenFontSize: () -> Unit = {},
    onOpenLanguage: () -> Unit = {},
    onOpenTranslation: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "界面与显示", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormValueRow(
                label = "深色 / 浅色模式",
                value = DisplayPrefs.modeLabel,
                onClick = onOpenDarkMode,
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormValueRow(
                label = "字体大小",
                value = com.xyzl.driverphone.AppFontStore.level.toString() + " / 7 档",
                onClick = onOpenFontSize,
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormValueRow(
                label = "多语言",
                value = com.xyzl.driverphone.AppLanguageStore.language.label,
                onClick = onOpenLanguage,
            )
            HorizontalDivider(color = FormDivide, thickness = 1.dp)
            FormValueRow(
                label = "翻译",
                value = "翻译为 " + DisplayPrefs.translateTo,
                onClick = onOpenTranslation,
            )
        }

        Spacer(Modifier.height(110.dp))
    }
}

/** 深色模式页：跟随系统开关 */
@Composable
fun DarkModeScreen(onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "深色模式", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormSwitchRow(
                label = "跟随系统",
                checked = DisplayPrefs.followSystem,
                onCheckedChange = { DisplayPrefs.followSystem = it },
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote("开启后，将跟随系统打开或关闭深色模式。")

        if (!DisplayPrefs.followSystem) {
            Spacer(Modifier.height(18.dp))
            FormSectionTitle("手动选择")
            SegmentedTabs(
                options = listOf("深色", "浅色"),
                selectedIndex = if (DisplayPrefs.darkMode) 0 else 1,
                onSelect = { DisplayPrefs.darkMode = it == 0 },
            )
        }

        Spacer(Modifier.height(110.dp))
    }
}
