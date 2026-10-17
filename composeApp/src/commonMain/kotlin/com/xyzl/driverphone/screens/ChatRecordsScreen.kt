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
import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.exportChatRecordsPdf

/** 聊天记录管理页：导出 PDF / 清空本地聊天记录 */
@Composable
fun ChatRecordsScreen(onBack: () -> Unit = {}, userId: String? = null) {
    val strings = LocalStrings.current
    var confirmClear by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageTitle by remember { mutableStateOf("") }

    fun allLines(): List<String> {
        val lines = mutableListOf<String>()
        ChatListStore.driver.forEach { lines += "[" + it.time + "] " + it.name + ": " + it.message }
        ChatListStore.official.forEach { lines += "[" + it.time + "] " + it.name + ": " + it.message }
        ChatStore.messages.forEach { msg ->
            val body = when (val b = msg.body) {
                is ChatBody.Text -> b.text
                is ChatBody.Album -> "[图片 x" + b.uris.size + "]"
                is ChatBody.File -> "[文件] " + b.name
                is ChatBody.Location -> "[位置] " + b.name + " " + (b.coords ?: "")
                is ChatBody.Card -> "[名片] " + b.title
                is ChatBody.Voice -> "[语音] " + b.seconds + "s"
            }
            lines += "[" + msg.date + " " + msg.time + "] " + msg.senderName + ": " + body
        }
        return lines
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = strings.chatRecords, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormNote(LocalStrings.current.s0579)
        Spacer(Modifier.height(12.dp))

        FormPrimaryButton(
            text = strings.exportChats,
            onClick = {
                val path = exportChatRecordsPdf(allLines())
                messageTitle = if (path == null) strings.failed else strings.exportedTitle
                message = if (path == null) LocalStrings.current.s0367 else strings.exportedText + "\n" + path
            },
        )
        Spacer(Modifier.height(14.dp))
        FormPrimaryButton(
            text = strings.clearChats,
            onClick = { confirmClear = true },
            danger = true,
        )
        Spacer(Modifier.height(110.dp))
    }

    val shown = message
    if (shown != null) {
        AlertDialog(
            onDismissRequest = { message = null },
            containerColor = FormCard,
            title = { Text(messageTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(shown, fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { message = null }) {
                    Text(strings.confirm, color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = FormCard,
            title = { Text(LocalStrings.current.s0162, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(LocalStrings.current.s0577, fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = {
                    ChatStore.clearAll()
                    ChatListStore.driver.clear()
                    ChatListStore.official.clear()
                    confirmClear = false
                    messageTitle = strings.clearedTitle
                    message = strings.clearedText
                }) {
                    Text(strings.clear, color = FormRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text(strings.cancel, color = FormMuted)
                }
            },
        )
    }
}
