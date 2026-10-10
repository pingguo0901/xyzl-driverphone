package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.xyzl.driverphone.AccountTypes
import com.xyzl.driverphone.DialCodes
import com.xyzl.driverphone.GenderOptions
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEmail
import com.xyzl.driverphone.sanitizeEnglish
import com.xyzl.driverphone.sanitizeEnglishName
import com.xyzl.driverphone.sanitizeEnglishPunct
import com.xyzl.driverphone.sanitizeNickname
import com.xyzl.driverphone.sanitizeWechat

private val Gold = Color(0xFFD8B645)
private val GoldLight = Color(0xFFE6C765)
private val FieldBorder = Color(0xFF5478AA)
private val LabelGray = Color(0xFFB0C4E2)

/** 注册页：账号资料 + KYC 认证 */
@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onPickBirthday: (onPicked: (String) -> Unit) -> Unit = {},
    onPickImage: (source: ImageSource, onPicked: (String) -> Unit) -> Unit = { _, _ -> },
    onPolicyClick: () -> Unit = {},
) {
    var accountType by remember { mutableStateOf(AccountTypes.first()) }
    var nickname by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var garden by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var dialCode by remember { mutableStateOf(DialCodes.first()) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var wechat by remember { mutableStateOf("") }
    var icUri by remember { mutableStateOf<String?>(null) }
    var licenseUri by remember { mutableStateOf<String?>(null) }
    var passportUri by remember { mutableStateOf<String?>(null) }
    var agreed by remember { mutableStateOf(false) }

    val canSubmit = nickname.isNotBlank() && fullName.isNotBlank() && gender.isNotBlank() &&
        birthday.isNotBlank() && street.isNotBlank() && garden.isNotBlank() &&
        postcode.isNotBlank() && city.isNotBlank() && state.isNotBlank() &&
        phone.isNotBlank() && email.isNotBlank() && icUri != null &&
        licenseUri != null && passportUri != null && agreed

    val bg = Brush.verticalGradient(listOf(Color(0xFF081226), Color(0xFF0F2040), Color(0xFF102244)))

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "返回", tint = Color.White)
                }
                Text("注册", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))

            SectionTitle("类型")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccountTypes.forEach { type ->
                    ChoiceChip(type, accountType == type, Modifier.weight(1f)) { accountType = type }
                }
            }
            Spacer(Modifier.height(18.dp))

            LabeledField(
                label = "昵称", required = true,
                value = nickname, placeholder = "中文 / 英文 / 数字 / 标点",
                onValueChange = { nickname = sanitizeNickname(it) },
            )
            LabeledField(
                label = "姓名", required = true,
                value = fullName, placeholder = "仅英文和空格",
                onValueChange = { fullName = sanitizeEnglishName(it) },
            )

            SectionTitle("性别", required = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GenderOptions.forEach { g ->
                    ChoiceChip(g, gender == g, Modifier.weight(1f)) { gender = g }
                }
            }
            Spacer(Modifier.height(18.dp))

            SectionTitle("生日日期", required = true)
            PickerField(
                text = birthday.ifBlank { "选择日期" },
                onClick = { onPickBirthday { birthday = it } },
            )
            Spacer(Modifier.height(18.dp))

            SectionTitle("居住地")
            LabeledField(
                label = "门牌及街道", required = true,
                value = street, placeholder = "英文 / 数字 / 标点",
                onValueChange = { street = sanitizeEnglishPunct(it) },
            )
            LabeledField(
                label = "花园", required = true,
                value = garden, placeholder = "英文 / 数字 / 标点",
                onValueChange = { garden = sanitizeEnglishPunct(it) },
            )
            LabeledField(
                label = "邮编", required = true,
                value = postcode, placeholder = "仅数字",
                onValueChange = { postcode = sanitizeDigits(it) },
                keyboardNumber = true,
            )
            LabeledField(
                label = "城市", required = true,
                value = city, placeholder = "仅英文",
                onValueChange = { city = sanitizeEnglish(it) },
            )
            LabeledField(
                label = "州属", required = true,
                value = state, placeholder = "仅英文",
                onValueChange = { state = sanitizeEnglish(it) },
            )

            SectionTitle("手机号", required = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                var showDial by remember { mutableStateOf(false) }
                Box(Modifier.width(96.dp)) {
                    PickerField(text = dialCode, onClick = { showDial = true })
                    if (showDial) {
                        Popup(
                            onDismissRequest = { showDial = false },
                            properties = PopupProperties(focusable = true),
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF16294A),
                                shadowElevation = 8.dp,
                            ) {
                                Column(Modifier.width(96.dp).padding(vertical = 4.dp)) {
                                    DialCodes.forEach { code ->
                                        Text(
                                            code,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            modifier = Modifier.fillMaxWidth()
                                                .clickable { dialCode = code; showDial = false }
                                                .padding(horizontal = 16.dp, vertical = 10.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = sanitizeDigits(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("仅数字", color = Color(0xFFAAAAAA)) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = fieldColors(),
                )
            }
            Spacer(Modifier.height(18.dp))

            LabeledField(
                label = "邮箱地址", required = true,
                value = email, placeholder = "英文 / 数字 / 标点",
                onValueChange = { email = sanitizeEmail(it) },
            )
            LabeledField(
                label = "微信号", required = false,
                value = wechat, placeholder = "选填 · 英文 / 数字 / 标点",
                onValueChange = { wechat = sanitizeWechat(it) },
            )

            Spacer(Modifier.height(10.dp))
            Text("KYC认证", fontSize = 20.sp, color = GoldLight, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            UploadBox(
                title = "上传马来西亚身份证",
                hint = "仅支持拍照",
                uri = icUri,
                allowGallery = false,
                onPick = { source -> onPickImage(source) { icUri = it } },
            )
            UploadBox(
                title = "上传马来西亚驾照",
                hint = "支持拍照 / 相册",
                uri = licenseUri,
                allowGallery = true,
                onPick = { source -> onPickImage(source) { licenseUri = it } },
            )
            UploadBox(
                title = "上传马来西亚护照",
                hint = "仅支持拍照",
                uri = passportUri,
                allowGallery = false,
                onPick = { source -> onPickImage(source) { passportUri = it } },
            )

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Gold,
                        uncheckedColor = Color(0xFF889CC0),
                        checkmarkColor = Color(0xFF1A1A1A),
                    ),
                )
                Text("我已阅读并同意", color = Color(0xFFDDE6F7), fontSize = 12.sp)
                Text(
                    "《司机入驻KYC实名认证服务政策》",
                    color = GoldLight, fontSize = 12.sp,
                    modifier = Modifier.clickable { onPolicyClick() },
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    disabledContainerColor = Color(0xFF3A4A66),
                ),
            ) {
                Text(
                    "注 册",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSubmit) Color(0xFF1A1206) else Color(0xFF8899B0),
                    letterSpacing = 4.sp,
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String, required: Boolean = false) {
    Row(Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        if (required) Text(" *必填", color = GoldLight, fontSize = 12.sp)
    }
}

@Composable
private fun LabeledField(
    label: String,
    required: Boolean,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    keyboardNumber: Boolean = false,
) {
    Column(Modifier.padding(bottom = 14.dp)) {
        SectionTitle(label, required)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFFAAAAAA)) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (keyboardNumber) KeyboardType.Number else KeyboardType.Text,
            ),
            colors = fieldColors(),
        )
    }
}

@Composable
private fun PickerField(text: String, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x1AFFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text, color = Color.White, fontSize = 15.sp)
    }
}

@Composable
private fun ChoiceChip(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Gold else Color(0x1AFFFFFF))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) Color(0xFF1A1206) else Color(0xFFDDE6F7),
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun UploadBox(
    title: String,
    hint: String,
    uri: String?,
    allowGallery: Boolean,
    onPick: (ImageSource) -> Unit,
) {
    var showSheet by remember { mutableStateOf(false) }
    Column(Modifier.padding(bottom = 16.dp)) {
        SectionTitle(title, required = true)
        Box(
            Modifier.fillMaxWidth().height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x1AFFFFFF))
                .clickable { showSheet = true },
            contentAlignment = Alignment.Center,
        ) {
            if (uri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CameraAlt, null, tint = GoldLight, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(hint, color = LabelGray, fontSize = 12.sp)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, null, tint = Color(0xFF25D366))
                    Spacer(Modifier.width(8.dp))
                    Text("已上传", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF16294A),
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable { showSheet = false; onPick(ImageSource.Camera) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, tint = GoldLight)
                        Spacer(Modifier.width(12.dp))
                        Text("拍照", color = Color.White)
                    }
                    if (allowGallery) {
                        Row(
                            Modifier.fillMaxWidth().clickable { showSheet = false; onPick(ImageSource.Gallery) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, null, tint = GoldLight)
                            Spacer(Modifier.width(12.dp))
                            Text("从相册选择", color = Color.White)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = GoldLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Gold,
)
