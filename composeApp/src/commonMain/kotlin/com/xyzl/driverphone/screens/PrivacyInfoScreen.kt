package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.GenderOptions
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEnglish
import com.xyzl.driverphone.sanitizeEnglishName
import com.xyzl.driverphone.sanitizeEnglishPunct

/** 隐私信息页：字段校验规则与注册页一致 */
@Composable
fun PrivacyInfoScreen(
    onBack: () -> Unit = {},
    onPickBirthday: ((String) -> Unit) -> Unit = {},
    inviteCode: String = "",
    companyName: String = "",
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(GenderOptions.first()) }
    var birthday by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var garden by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "隐私信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        // 有邀请码（公司司机）才显示邀请码 + 归属公司；个人不显示
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
                KycDocs.forEach { doc ->
                    KycStatusRow(doc.name, doc.status)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        FormPrimaryButton(text = "保存更改", onClick = { showSaved = true })
        Spacer(Modifier.height(110.dp))
    }

    if (showSaved) {
        AlertDialog(
            onDismissRequest = { showSaved = false },
            containerColor = FormCard,
            title = { Text("保存成功", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("隐私信息已更新", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showSaved = false
                    onBack()
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

/** KYC 证件状态（示例数据，接后端后由后端返回的状态驱动）0=已过期 1=有效 */
private data class KycDoc(val name: String, val status: Int)

private val KycDocs = listOf(
    KycDoc("身份证", 1),
    KycDoc("驾照", 1),
    KycDoc("护照", 1),
)

/** KYC 证件状态行：只显示状态（有效 / 已过期），无按钮，状态来自数据 */
@Composable
private fun KycStatusRow(name: String, status: Int) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, fontSize = 15.sp, color = FormInk, modifier = Modifier.weight(1f))
        Text(
            if (status == 1) "有效" else "已过期",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (status == 1) FormGreen else androidx.compose.ui.graphics.Color(0xFFF43F5E),
        )
    }
}
