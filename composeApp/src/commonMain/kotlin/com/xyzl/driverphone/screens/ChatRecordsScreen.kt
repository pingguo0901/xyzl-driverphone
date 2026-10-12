package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 聊天记录管理页：清空 / 导出全部聊天记录 */
@Composable
fun ChatRecordsScreen(onBack: () -> Unit = {}) {
    var confirmClear by remember { mutableStateOf(false) }
    var showExported by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "聊天记录管理", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormNote("清空或导出会作用于本机保存的全部聊天记录。")
        Spacer(Modifier.height(12.dp))

        FormPrimaryButton(
            text = "导出全部聊天记录",
            onClick = { showExported = true },
        )
        Spacer(Modifier.height(14.dp))
        FormPrimaryButton(
            text = "清空全部聊天记录",
            onClick = { confirmClear = true },
            danger = true,
        )
        Spacer(Modifier.height(110.dp))
    }

    if (showExported) {
        AlertDialog(
            onDismissRequest = { showExported = false },
            containerColor = FormCard,
            title = { Text("导出完成", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("聊天记录已导出到本机文件。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { showExported = false }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = FormCard,
            title = { Text("清空聊天记录", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("清空后本机全部聊天记录将被删除且无法恢复。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("清空", color = FormRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("取消", color = FormMuted)
                }
            },
        )
    }
}
