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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ChatRepository
import com.xyzl.driverphone.ChatThreadInfo
import kotlinx.coroutines.launch

/** 聊天详情页（聊天窗右上角三个点）：查找聊天内容 / 免打扰 / 置顶 / 清空记录 */
@Composable
fun ChatDetailScreen(
    peerName: String,
    onBack: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onCleared: () -> Unit = {},
    userId: String? = null,
) {
    val repo = remember { ChatRepository() }
    val scope = rememberCoroutineScope()
    var thread by remember { mutableStateOf<ChatThreadInfo?>(null) }
    var quiet by remember { mutableStateOf(false) }
    var pinned by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(userId, peerName) {
        if (userId == null) return@LaunchedEffect
        val info = repo.open(userId, peerName)?.first
        thread = info
        quiet = info?.isMuted ?: false
        pinned = info?.isPinned ?: false
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "聊天详情", onBack = onBack)
        Spacer(Modifier.height(14.dp))

        FormGroup {
            FormValueRow(label = "查找聊天内容", value = "", onClick = onOpenSearch)
        }

        Spacer(Modifier.height(14.dp))
        FormGroup {
            FormSwitchRow(label = "消息免打扰", checked = quiet, onCheckedChange = { on ->
                quiet = on
                val id = thread?.id ?: return@FormSwitchRow
                scope.launch { repo.setMuted(id, on) }
            })
        }

        Spacer(Modifier.height(14.dp))
        FormGroup {
            FormSwitchRow(label = "置顶聊天", checked = pinned, onCheckedChange = { on ->
                pinned = on
                val id = thread?.id ?: return@FormSwitchRow
                scope.launch { repo.setPinned(id, on) }
            })
        }

        Spacer(Modifier.height(14.dp))
        FormGroup {
            FormValueRow(
                label = "清空当前聊天记录",
                value = "",
                onClick = { confirmClear = true },
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote("清空只对你自己隐藏（user_cleared_at），服务器上的聊天记录不会被删。")
        Spacer(Modifier.height(40.dp))
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = FormCard,
            title = { Text("清空聊天记录", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("确定清空与「" + peerName + "」的聊天记录吗？", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    val id = thread?.id
                    if (id != null) scope.launch { repo.clearForUser(id) }
                    onCleared()
                }) { Text("确定", color = FormRed, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("取消", color = FormMuted) }
            },
        )
    }
}
