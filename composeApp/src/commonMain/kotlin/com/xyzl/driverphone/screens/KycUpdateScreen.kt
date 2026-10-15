package com.xyzl.driverphone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.ProfileEditRepository
import kotlinx.coroutines.launch

/** 证件类型 → 页面标题里的证件名 */
private val KycUploadTitles = mapOf(
    "ic" to "上传马来西亚身份证",
    "license" to "上传驾照",
    "passport" to "上传护照",
)

/**
 * 更新 KYC 认证页：上传框 + 最底部「确认更新」
 * 确认后：上传 Storage se_kyc-drivers → 写 se_driver_documents(file_url / status='pending')
 */
@Composable
fun KycUpdateScreen(
    userId: String? = null,
    docType: String = "ic",
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    val repo = remember { ProfileEditRepository() }
    val scope = rememberCoroutineScope()

    var uri by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val title = KycUploadTitles[docType] ?: "上传证件"

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "更新 KYC 认证", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            FormUpload(
                label = title,
                hint = "拍照或从相册选择证件照片",
                uri = uri,
                onPicked = { uri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormNote("请上传清晰的证件正面照，四角完整、文字可读。提交后状态变为「待审核」。")
            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = if (saving) "提交中…" else "确认更新",
            enabled = uri != null && !saving,
            onClick = {
                val id = userId
                val file = uri
                if (id == null) {
                    dialogTitle = "请先登录"
                    dialogBody = "登录后才能更新 KYC 认证"
                    showDialog = true
                } else if (file == null) {
                    dialogTitle = "还没选文件"
                    dialogBody = "请先拍照或从相册选一张证件照片"
                    showDialog = true
                } else {
                    saving = true
                    scope.launch {
                        val accountId = repo.accountId(id)
                        val profile = repo.myPrivacy(id, accountId)
                        val err = repo.submitDocument(
                            userId = id,
                            accountId = accountId,
                            profileId = profile?.profileId,
                            type = docType,
                            uri = file,
                        )
                        saving = false
                        if (err == null) {
                            dialogTitle = "提交成功"
                            dialogBody = "证件已提交，等待审核"
                        } else {
                            dialogTitle = "提交失败"
                            dialogBody = err
                        }
                        showDialog = true
                    }
                }
            },
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = FormCard,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(dialogBody, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    val success = dialogTitle == "提交成功"
                    showDialog = false
                    if (success) onBack()
                }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
