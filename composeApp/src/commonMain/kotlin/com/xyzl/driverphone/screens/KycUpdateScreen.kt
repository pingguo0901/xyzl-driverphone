package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

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
    onPickDate: ((String) -> Unit) -> Unit = {},
) {
    val repo = remember { ProfileEditRepository() }
    val scope = rememberCoroutineScope()

    var uri by remember { mutableStateOf<String?>(null) }
    var expiry by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val title = KycUploadTitles[docType] ?: LocalStrings.current.s0232

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = LocalStrings.current.s0523, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            FormUpload(
                label = title,
                hint = LocalStrings.current.s0462,
                uri = uri,
                onPicked = { uri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormPickerField(
                label = LocalStrings.current.s0315,
                text = expiry,
                onClick = { onPickDate { expiry = it } },
            )
            FormNote(LocalStrings.current.s0316)
            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = if (saving) LocalStrings.current.s0062 else LocalStrings.current.s0615,
            enabled = uri != null && expiry.isNotBlank() && !saving,
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
                } else if (expiry.isBlank()) {
                    dialogTitle = "还没填到期日期"
                    dialogBody = "请选择证件的到期日期（YYYY-MM-DD）"
                    showDialog = true
                } else {
                    saving = true
                    scope.launch {
                        val accountId = repo.accountId(id)
                        val profile = repo.myPrivacy(id, accountId, repo.userRole(id))
                        val err = repo.submitDocument(
                            userId = id,
                            accountId = accountId,
                            profileId = profile?.profileId,
                            type = docType,
                            uri = file,
                            expiryDate = expiry,
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
                    Text(LocalStrings.current.confirm, color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
