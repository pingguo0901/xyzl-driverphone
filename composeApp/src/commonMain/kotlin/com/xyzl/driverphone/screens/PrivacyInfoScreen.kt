package com.xyzl.driverphone.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.GenderOptions
import com.xyzl.driverphone.KycRow
import com.xyzl.driverphone.MyPrivacy
import com.xyzl.driverphone.ProfileEditRepository
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEnglish
import com.xyzl.driverphone.sanitizeEnglishName
import com.xyzl.driverphone.sanitizeEnglishPunct
import kotlinx.coroutines.launch

/** KYC 三种证件的显示名 */
private val KycTypeLabels = listOf(
    "ic" to "身份证",
    "license" to "驾照",
    "passport" to "护照",
)

/** 隐私信息页：se_driver_profiles + KYC（se_driver_documents） */
@Composable
fun PrivacyInfoScreen(
    userId: String? = null,
    onBack: () -> Unit = {},
    onPickBirthday: ((String) -> Unit) -> Unit = {},
    onUpdateKyc: (String) -> Unit = {},
    inviteCode: String = "",
    companyName: String = "",
) {
    val repo = remember { ProfileEditRepository() }
    val scope = rememberCoroutineScope()

    var loaded by remember { mutableStateOf(false) }
    var profileId by remember { mutableStateOf<String?>(null) }
    var initial by remember { mutableStateOf(MyPrivacy()) }

    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(GenderOptions.first()) }
    var birthday by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var garden by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var kyc by remember { mutableStateOf<List<KycRow>>(emptyList()) }
    var saving by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val id = userId ?: return@LaunchedEffect
        val accountId = repo.accountId(id)
        val info = repo.myPrivacy(id, accountId)
        if (info != null) {
            initial = info
            profileId = info.profileId
            fullName = info.fullName
            gender = info.gender?.takeIf { g -> GenderOptions.contains(g) } ?: GenderOptions.first()
            birthday = info.birthday.orEmpty()
            street = info.street
            garden = info.garden
            postcode = info.postcode
            city = info.city
            state = info.state
        }
        kyc = repo.kycDocs(id, info?.profileId)
        loaded = true
    }

    val changed = loaded && (
        fullName != initial.fullName ||
            gender != (initial.gender ?: GenderOptions.first()) ||
            birthday != initial.birthday.orEmpty() ||
            street != initial.street ||
            garden != initial.garden ||
            postcode != initial.postcode ||
            city != initial.city ||
            state != initial.state
        )

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "隐私信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        if (inviteCode.isNotBlank()) {
            FormSectionTitle("邀请码")
            Card(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FormCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ReadOnlyRow("邀请码", inviteCode)
                    ReadOnlyRow("归属公司", companyName.ifBlank { "-" })
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        FormTextField(
            label = "姓名",
            value = fullName,
            placeholder = "仅英文和空格",
            onValueChange = { fullName = sanitizeEnglishName(it) },
        )

        FormSectionTitle("性别")
        SegmentedTabs(
            options = GenderOptions,
            selectedIndex = GenderOptions.indexOf(gender).coerceAtLeast(0),
            onSelect = { gender = GenderOptions[it] },
        )
        Spacer(Modifier.height(18.dp))

        FormPickerField(
            label = "生日日期",
            text = birthday,
            onClick = { onPickBirthday { birthday = it } },
        )

        FormSectionTitle("居住地")
        FormTextField(
            label = "门牌及街道",
            value = street,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { street = sanitizeEnglishPunct(it) },
        )
        FormTextField(
            label = "花园",
            value = garden,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { garden = sanitizeEnglishPunct(it) },
        )
        FormTextField(
            label = "邮编",
            value = postcode,
            placeholder = "仅数字",
            onValueChange = { postcode = sanitizeDigits(it) },
            keyboardNumber = true,
        )
        FormTextField(
            label = "城市",
            value = city,
            placeholder = "仅英文",
            onValueChange = { city = sanitizeEnglish(it) },
        )
        FormTextField(
            label = "州属",
            value = state,
            placeholder = "仅英文",
            onValueChange = { state = sanitizeEnglish(it) },
        )

        FormSectionTitle("KYC认证")
        Card(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                KycTypeLabels.forEach { (type, label) ->
                    val row = kyc.firstOrNull { it.type == type }
                    KycStatusRow(
                        name = label,
                        valid = row?.valid == true,
                        onUpdate = { onUpdateKyc(type) },
                        detail = when {
                            row == null -> "未上传"
                            row.valid -> row.expiryDate?.let { "有效 · 至 " + it } ?: "有效"
                            row.expiryDate != null -> "已过期 · " + row.expiryDate
                            else -> "待审核"
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        FormPrimaryButton(
            text = if (saving) "保存中…" else "保存更改",
            enabled = changed && !saving,
            onClick = {
                val pid = profileId
                if (pid == null) {
                    dialogTitle = "无法保存"
                    dialogBody = "找不到你的司机档案（se_driver_profiles 里没有你的记录）"
                    showDialog = true
                } else {
                    saving = true
                    scope.launch {
                        val err = repo.savePrivacy(
                            profileId = pid,
                            fullName = fullName,
                            gender = gender,
                            birthday = birthday.ifBlank { null },
                            street = street,
                            garden = garden,
                            postcode = postcode,
                            city = city,
                            state = state,
                        )
                        saving = false
                        if (err == null) {
                            initial = initial.copy(
                                fullName = fullName,
                                gender = gender,
                                birthday = birthday.ifBlank { null },
                                street = street,
                                garden = garden,
                                postcode = postcode,
                                city = city,
                                state = state,
                            )
                            dialogTitle = "保存成功"
                            dialogBody = "隐私信息已更新"
                        } else {
                            dialogTitle = "保存失败"
                            dialogBody = err
                        }
                        showDialog = true
                    }
                }
            },
        )
        Spacer(Modifier.height(110.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = FormCard,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(dialogBody, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    val success = dialogTitle == "保存成功"
                    showDialog = false
                    if (success) onBack()
                }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

/** 只读信息行（标签 + 值） */
@Composable
private fun ReadOnlyRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = FormMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FormInk)
    }
}

/** KYC 证件状态行：有效（approved 且未过期）/ 已过期，过期时有「更新」按钮 */
@Composable
private fun KycStatusRow(name: String, valid: Boolean, detail: String, onUpdate: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(name, fontSize = 15.sp, color = FormInk)
            Text(detail, fontSize = 12.sp, color = FormMuted)
        }
        if (valid) {
            Text("有效", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FormGreen)
        } else {
            Text(
                "更新",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF43F5E),
                modifier = Modifier.clickable { onUpdate() },
            )
        }
    }
}
