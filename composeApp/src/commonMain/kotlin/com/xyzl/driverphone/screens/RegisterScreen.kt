package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.xyzl.driverphone.DialCodes
import com.xyzl.driverphone.RegisterPayload
import com.xyzl.driverphone.RegisterResult
import kotlinx.coroutines.launch
import com.xyzl.driverphone.GenderOptions
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEmail
import com.xyzl.driverphone.sanitizeEnglish
import com.xyzl.driverphone.sanitizeEnglishName
import com.xyzl.driverphone.sanitizeEnglishPunct
import com.xyzl.driverphone.sanitizeNickname
import com.xyzl.driverphone.sanitizeWechat

private val Gold = Color(0xFF3B82F6)
private val GoldLight = Color(0xFF3B82F6)
private val FieldBorder: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val LabelGray: Color @Composable get() = LocalAppColors.current.muted

private val OkGreen = Color(0xFF16A34A)
private val ErrRed = Color(0xFFDC2626)

/** 创建密码的每一条规则：label 文案 + check 判定 */
private class PasswordRule(val label: String, val check: (String) -> Boolean)

private val PasswordRules = listOf(
    PasswordRule("必须 8-14 个字") { pwd -> pwd.length in 8..14 },
    PasswordRule("必须包含 1 个大写字母") { pwd -> pwd.any { it.isUpperCase() } },
    PasswordRule("必须包含 1 个小写字母") { pwd -> pwd.any { it.isLowerCase() } },
    PasswordRule("必须包含 1 个数字") { pwd -> pwd.any { it.isDigit() } },
    PasswordRule("必须包含 1 个特殊标点符号") { pwd -> pwd.any { c -> !c.isLetterOrDigit() && !c.isWhitespace() } },
)

/** 全部规则通过才算合法密码 */
private fun isPasswordValid(pwd: String): Boolean = PasswordRules.all { it.check(pwd) }

/** 注册页：账号资料 + KYC 认证 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onSubmit: suspend (RegisterPayload) -> Result<RegisterResult> = { Result.success(RegisterResult("", null)) },
    onRegistered: () -> Unit = {},
    onPickBirthday: (onPicked: (String) -> Unit) -> Unit = {},
    onPickImage: (source: ImageSource, onPicked: (String) -> Unit) -> Unit = { _, _ -> },
    onOpenPolicy: (Int) -> Unit = {},
) {
    var inviteCode by remember { mutableStateOf("") }
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
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val passwordOk = isPasswordValid(password)
    val confirmMismatch = confirmPassword.isNotEmpty() && confirmPassword != password
    var submitting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var icUri by remember { mutableStateOf<String?>(null) }
    var licenseUri by remember { mutableStateOf<String?>(null) }
    var passportUri by remember { mutableStateOf<String?>(null) }
    var agreed by remember { mutableStateOf(false) }

    val canSubmit = nickname.isNotBlank() && fullName.isNotBlank() && gender.isNotBlank() &&
        birthday.isNotBlank() && street.isNotBlank() && garden.isNotBlank() &&
        postcode.isNotBlank() && city.isNotBlank() && state.isNotBlank() &&
        phone.isNotBlank() && email.isNotBlank() && passwordOk && !confirmMismatch &&
        icUri != null && licenseUri != null && passportUri != null && agreed

    val bg = Brush.verticalGradient(listOf(LocalAppColors.current.background, LocalAppColors.current.background, LocalAppColors.current.background))

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = LocalAppColors.current.ink)
                }
                Text("注册", fontSize = 22.sp, color = LocalAppColors.current.ink, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))

            LabeledField(
                label = "邀请码",
                required = false,
                value = inviteCode,
                placeholder = "选填 · 填写后归属该公司，不填为个人",
                onValueChange = { input -> inviteCode = input.filter { c -> c.isLetterOrDigit() || c == '-' } },
            )
            Spacer(Modifier.height(4.dp))

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
                                color = LocalAppColors.current.surfaceVariant,
                                shadowElevation = 8.dp,
                            ) {
                                Column(Modifier.width(96.dp).padding(vertical = 4.dp)) {
                                    DialCodes.forEach { code ->
                                        Text(
                                            code,
                                            color = LocalAppColors.current.ink,
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
                    placeholder = { Text("仅数字", color = LocalAppColors.current.muted) },
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

            SectionTitle("创建密码", required = true)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("请输入 8-14 位密码", color = LocalAppColors.current.muted) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = "切换密码可见",
                            tint = LocalAppColors.current.muted,
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = fieldColors(),
            )

            // 规则逐条显示：满足打绿 ✅，不满足打红 ✖️
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                PasswordRules.forEach { rule ->
                    val ok = rule.check(password)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (ok) "✅" else "✖️",
                            fontSize = 12.sp,
                            color = if (ok) OkGreen else ErrRed,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = rule.label,
                            color = if (ok) OkGreen else ErrRed,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            SectionTitle("确认密码", required = true)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("请再次输入密码", color = LocalAppColors.current.muted) },
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            imageVector = if (confirmVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = "切换密码可见",
                            tint = LocalAppColors.current.muted,
                        )
                    }
                },
                visualTransformation = if (confirmVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = confirmMismatch,
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = fieldColors(),
            )
            if (confirmMismatch) {
                Text(
                    text = "确认密码与创建密码不符合",
                    color = ErrRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

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
                        uncheckedColor = LocalAppColors.current.textMuted,
                        checkmarkColor = Color(0xFFFFFFFF),
                    ),
                )
                FlowRow(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("我已阅读并同意", color = LocalAppColors.current.ink, fontSize = 12.sp)
                    PolicyLink("《KYC实名认证合规条款》") { onOpenPolicy(2) }
                    Text("、", color = LocalAppColors.current.ink, fontSize = 12.sp)
                    PolicyLink("《用户服务协议》") { onOpenPolicy(0) }
                    Text("、", color = LocalAppColors.current.ink, fontSize = 12.sp)
                    PolicyLink("《隐私政策》") { onOpenPolicy(1) }
                    Text("与", color = LocalAppColors.current.ink, fontSize = 12.sp)
                    PolicyLink("《终端用户软件许可》") { onOpenPolicy(7) }
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "",
                    color = ErrRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMsg = null
                    submitting = true
                    scope.launch {
                        val result = onSubmit(
                            RegisterPayload(
                                email = email,
                                password = password,
                                nickname = nickname,
                                fullName = fullName,
                                gender = gender,
                                birthday = birthday,
                                street = street,
                                garden = garden,
                                postcode = postcode,
                                city = city,
                                state = state,
                                phone = dialCode.filter { c -> c.isDigit() } + phone,
                                wechatId = wechat,
                                inviteCode = inviteCode.ifBlank { null },
                                icUri = icUri,
                                licenseUri = licenseUri,
                                passportUri = passportUri,
                            )
                        )
                        submitting = false
                        result
                            .onSuccess { onRegistered() }
                            .onFailure { errorMsg = it.message ?: "注册失败，请重试" }
                    }
                },
                enabled = canSubmit && !submitting,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    disabledContainerColor = LocalAppColors.current.surfaceVariant,
                ),
            ) {
                Text(
                    "注 册",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSubmit) Color(0xFFFFFFFF) else LocalAppColors.current.textDisabled,
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
        Text(text, color = LocalAppColors.current.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
            placeholder = { Text(placeholder, color = LocalAppColors.current.muted) },
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
            .background(LocalAppColors.current.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text, color = LocalAppColors.current.ink, fontSize = 15.sp)
    }
}

@Composable
private fun ChoiceChip(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Gold else LocalAppColors.current.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) Color(0xFFFFFFFF) else LocalAppColors.current.ink,
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
                .background(LocalAppColors.current.surfaceVariant)
                .clickable { showSheet = true },
            contentAlignment = Alignment.Center,
        ) {
            if (uri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CameraAlt, null, tint = LocalAppColors.current.muted, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(hint, color = LabelGray, fontSize = 12.sp)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, null, tint = Color(0xFF25D366))
                    Spacer(Modifier.width(8.dp))
                    Text("已上传", color = LocalAppColors.current.ink, fontSize = 14.sp)
                }
            }
        }
    }
    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LocalAppColors.current.surfaceVariant,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(title, color = LocalAppColors.current.ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable { showSheet = false; onPick(ImageSource.Camera) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, tint = LocalAppColors.current.muted)
                        Spacer(Modifier.width(12.dp))
                        Text("拍照", color = LocalAppColors.current.ink)
                    }
                    if (allowGallery) {
                        Row(
                            Modifier.fillMaxWidth().clickable { showSheet = false; onPick(ImageSource.Gallery) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, null, tint = GoldLight)
                            Spacer(Modifier.width(12.dp))
                            Text("从相册选择", color = LocalAppColors.current.ink)
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
    focusedTextColor = LocalAppColors.current.ink,
    unfocusedTextColor = LocalAppColors.current.ink,
    cursorColor = Gold,
)
