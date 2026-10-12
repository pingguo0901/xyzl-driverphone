package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.VersionInfo
import kotlinx.coroutines.launch

/** 版本更新页：当前版本 / 最新版本 / 检测版本 */
@Composable
fun VersionUpdateScreen(
    currentVersion: String,
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onApplyUpdate: (suspend (VersionInfo, (Long, Long) -> Unit) -> String?)? = null,
    onBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var latestVersion by remember { mutableStateOf("未检测") }
    var checking by remember { mutableStateOf(false) }
    var showUpToDate by remember { mutableStateOf(false) }
    var showFailed by remember { mutableStateOf(false) }
    var pendingUpdate by remember { mutableStateOf<VersionInfo?>(null) }

    fun runCheck() {
        val check = onCheckUpdate
        if (check == null) {
            showFailed = true
            return
        }
        checking = true
        scope.launch {
            val info = check()
            checking = false
            if (info == null) {
                latestVersion = currentVersion
                showUpToDate = true
            } else {
                latestVersion = info.versionName
                pendingUpdate = info
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "版本更新", onBack = onBack)
        Spacer(Modifier.height(20.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                FormValueRow(label = "当前版本", value = currentVersion, onClick = {})
                HorizontalDivider(color = FormDivide)
                FormValueRow(label = "最新版本", value = latestVersion, onClick = {})
            }
        }

        Spacer(Modifier.height(28.dp))
        FormPrimaryButton(
            text = if (checking) "检测中…" else "检测版本",
            onClick = { if (!checking) runCheck() },
            enabled = !checking,
        )
    }

    if (showUpToDate) {
        AlertDialog(
            onDismissRequest = { showUpToDate = false },
            containerColor = FormCard,
            title = { Text("已是最新版本", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("当前版本 $currentVersion 已是最新版本。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { showUpToDate = false }) {
                    Text("完成", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    val update = pendingUpdate
    if (update != null) {
        AlertDialog(
            onDismissRequest = { pendingUpdate = null },
            containerColor = FormCard,
            title = { Text("请更新至最新版本", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("发现新版本 ${update.versionName}，请更新后继续使用。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = {
                    val target = update
                    pendingUpdate = null
                    scope.launch { onApplyUpdate?.invoke(target) { _, _ -> } }
                }) {
                    Text("更新", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingUpdate = null }) { Text("取消", color = FormMuted) }
            },
        )
    }

    if (showFailed) {
        AlertDialog(
            onDismissRequest = { showFailed = false },
            containerColor = FormCard,
            title = { Text("检测失败", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("网络异常或暂不支持检测，请稍后重试。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { showFailed = false }) {
                    Text("完成", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
