package com.stellarelite.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.driver.ui.theme.DriverColors
import kotlinx.coroutines.delay

val docTypes = listOf("国际护照", "马来西亚身份证", "马来西亚居住证", "马来西亚工作证")
val genderOptions = listOf("男", "女", "其他")
val countryList = listOf(
    "马来西亚", "新加坡", "中国", "印度", "印度尼西亚", "泰国", "越南", "菲律宾",
    "日本", "韩国", "美国", "英国", "加拿大", "澳大利亚", "法国", "德国",
    "俄罗斯", "巴西", "南非", "沙特阿拉伯", "阿联酋", "巴基斯坦", "孟加拉国",
    "缅甸", "柬埔寨", "老挝", "文莱", "尼泊尔", "斯里兰卡", "蒙古国",
    "荷兰", "意大利", "西班牙", "瑞士", "瑞典", "丹麦", "挪威", "芬兰",
    "新西兰", "墨西哥", "埃及", "土耳其", "卡塔尔", "科威特", "伊朗", "伊拉克"
)

fun kycStepLabel(step: Int) = when (step) {
    1 -> "上传证件"
    2 -> "个人资料"
    3 -> "联系资料"
    4 -> "人脸识别"
    5 -> "确认资料"
    else -> ""
}

@Composable
fun KYCFlowScreen(onBack: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(1) }
    val scrollState = rememberScrollState()

    // Page 1
    var docType by remember { mutableStateOf("") }
    var docTypeIndex by remember { mutableIntStateOf(-1) }
    var docFrontSelected by remember { mutableStateOf(false) }
    var docBackSelected by remember { mutableStateOf(false) }
    var showDocFrontPicker by remember { mutableStateOf(false) }
    var showDocBackPicker by remember { mutableStateOf(false) }

    // Page 2
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var birthCountry by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }
    var docExpiry by remember { mutableStateOf("") }

    // Page 3
    var residence by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+60") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Page 4
    var faceScanComplete by remember { mutableStateOf(false) }
    var faceScanning by remember { mutableStateOf(false) }

    LaunchedEffect(faceScanning) {
        if (faceScanning) {
            delay(2500)
            faceScanning = false
            faceScanComplete = true
        }
    }

    // Page 5
    var consentChecked by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }

    val isPassport = docTypeIndex == 0

    fun canProceed(step: Int): Boolean = when (step) {
        1 -> docType.isNotBlank() && docFrontSelected && docBackSelected
        2 -> fullName.isNotBlank() && gender.isNotBlank() && birthDate.isNotBlank() &&
             birthCountry.isNotBlank() && nationality.isNotBlank() && docNumber.isNotBlank() &&
             (if (isPassport) docExpiry.isNotBlank() else true)
        3 -> residence.isNotBlank() && phone.isNotBlank() && email.isNotBlank()
        4 -> faceScanComplete
        else -> true
    }

    Column(modifier = Modifier.fillMaxSize().background(DriverColors.Background)) {
        // 顶栏
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable { if (currentStep == 1) onBack() else currentStep-- },
                contentAlignment = Alignment.Center
            ) { Text("←", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(8.dp))
            Text("KYC 实名认证", color = DriverColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        StepIndicator(currentStep)

        Column(
            modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (currentStep) {
                1 -> Page1DocUpload(
                    docType = docType,
                    onDocTypeSelect = { idx, label -> docTypeIndex = idx; docType = label; docFrontSelected = false; docBackSelected = false },
                    frontSelected = docFrontSelected, backSelected = docBackSelected,
                    onFrontClick = { showDocFrontPicker = true },
                    onBackClick = { showDocBackPicker = true },
                    isPassport = isPassport
                )
                2 -> Page2PersonalInfo(
                    fullName, { fullName = it }, isPassport,
                    gender, { gender = it },
                    birthDate, { birthDate = it },
                    birthCountry, { birthCountry = it },
                    nationality, { nationality = it },
                    docNumber, { docNumber = it },
                    docExpiry, { docExpiry = it }
                )
                3 -> Page3Contact(
                    residence, { residence = it },
                    countryCode, { countryCode = it },
                    phone, { phone = it },
                    email, { email = it }
                )
                4 -> Page4FaceRecognition(faceScanning, faceScanComplete,
                    onStartScan = { faceScanning = true }
                )
                5 -> Page5Confirm(
                    fullName, gender, birthDate, birthCountry, nationality, docNumber, docExpiry,
                    residence, countryCode, phone, email,
                    consentChecked, { consentChecked = it },
                    isPassport, docType
                )
            }
        }

        // 底部按钮
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                Box(
                    modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(14.dp))
                        .background(DriverColors.Surface).border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp))
                        .clickable { currentStep-- },
                    contentAlignment = Alignment.Center
                ) { Text("上一步", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium) }
            }
            val enabled = when (currentStep) {
                5 -> consentChecked
                else -> canProceed(currentStep)
            }
            Box(
                modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (enabled) DriverColors.Primary else DriverColors.SurfaceVariant)
                    .clickable(enabled = enabled) {
                        if (currentStep < 5) currentStep++
                        else if (currentStep == 5 && consentChecked) showSubmitDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (currentStep < 5) "下一步" else "完成验证",
                    color = if (enabled) Color.Black else DriverColors.TextDisabled,
                    fontSize = 16.sp, fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // 证件正面上传
    if (showDocFrontPicker) {
        PhotoPickerDialog(if (isPassport) "护照正面（人头像页）" else "证件正面") { showDocFrontPicker = false; docFrontSelected = true }
    }
    // 证件背面上传
    if (showDocBackPicker) {
        PhotoPickerDialog(if (isPassport) "护照外面（护照国家页面）" else "证件背面") { showDocBackPicker = false; docBackSelected = true }
    }

    // 提交成功弹窗
    if (showSubmitDialog) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).clip(RoundedCornerShape(20.dp))
                    .background(DriverColors.Card).padding(24.dp)
            ) {
                Text("✅", fontSize = 40.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(12.dp))
                Text("提交成功", color = DriverColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(8.dp))
                Text("您的资料已交给工作人员审核，1个工作日里完成，稍后会通知您。", color = DriverColors.TextSecondary, fontSize = 14.sp, lineHeight = 22.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(24.dp)).background(DriverColors.Primary)
                        .clickable { showSubmitDialog = false; onBack() },
                    contentAlignment = Alignment.Center
                ) { Text("知道了", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

/* ========== STEP INDICATOR ========== */

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            if (i > 1) {
                Box(
                    modifier = Modifier.weight(0.3f).height(2.dp)
                        .background(if (i <= currentStep) DriverColors.Primary else DriverColors.Border)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                        .background(if (i <= currentStep) DriverColors.Primary else DriverColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (i < currentStep) Text("✓", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    else Text("$i", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (i <= currentStep) Color.Black else DriverColors.TextMuted)
                }
                Spacer(Modifier.height(2.dp))
                Text(kycStepLabel(i), fontSize = 10.sp, color = if (i <= currentStep) DriverColors.Primary else DriverColors.TextMuted)
            }
        }
    }
}

/* ========== PAGE 1: 上传证件 ========== */

@Composable
private fun Page1DocUpload(
    docType: String,
    onDocTypeSelect: (Int, String) -> Unit,
    frontSelected: Boolean, backSelected: Boolean,
    onFrontClick: () -> Unit, onBackClick: () -> Unit,
    isPassport: Boolean
) {
    Text("选择证件类型", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    DriverDropdown(label = "证件类型", value = docType, options = docTypes) { label ->
        docTypes.indexOf(label).let { onDocTypeSelect(it, label) }
    }

    if (docType.isNotBlank()) {
        Spacer(Modifier.height(8.dp))
        Text("上传证件照片", color = DriverColors.TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        UploadBox(if (isPassport) "护照正面（人头像页）" else "证件正面", frontSelected, onFrontClick)
        UploadBox(if (isPassport) "护照外面（护照国家页面）" else "证件背面", backSelected, onBackClick)
    }
}

@Composable
private fun UploadBox(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(14.dp))
            .background(if (selected) DriverColors.PrimaryBg else DriverColors.Surface)
            .border(1.dp, if (selected) DriverColors.Primary.copy(alpha = 0.4f) else DriverColors.Border, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (selected) {
                Text("✅", fontSize = 30.sp)
                Spacer(Modifier.height(6.dp))
                Text("已上传", color = DriverColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            } else {
                Text("📷", fontSize = 28.sp)
                Spacer(Modifier.height(6.dp))
                Text(label, color = DriverColors.TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun PhotoPickerDialog(title: String, onDone: () -> Unit) {
    var processing by remember { mutableStateOf(false) }
    LaunchedEffect(processing) {
        if (processing) {
            delay(1200)
            onDone()
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).clip(RoundedCornerShape(20.dp))
                .background(DriverColors.Card).padding(24.dp)
        ) {
            Text(title, color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            if (processing) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏳", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text("正在处理...", color = DriverColors.TextSecondary, fontSize = 14.sp)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(DriverColors.Surface)
                        .clickable { processing = true }.padding(vertical = 14.dp), contentAlignment = Alignment.Center
                    ) { Text("📷 拍照", color = DriverColors.TextPrimary, fontSize = 14.sp) }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(DriverColors.Surface)
                        .clickable { processing = true }.padding(vertical = 14.dp), contentAlignment = Alignment.Center
                    ) { Text("🖼️ 相册", color = DriverColors.TextPrimary, fontSize = 14.sp) }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (!processing) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DriverColors.SurfaceVariant)
                    .clickable { onDone() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center
                ) { Text("取消", color = DriverColors.TextMuted, fontSize = 14.sp) }
            }
        }
    }
}

/* ========== PAGE 2: 个人资料 ========== */

@Composable
private fun Page2PersonalInfo(
    fullName: String, onFullName: (String) -> Unit, isPassport: Boolean,
    gender: String, onGender: (String) -> Unit,
    birthDate: String, onBirthDate: (String) -> Unit,
    birthCountry: String, onBirthCountry: (String) -> Unit,
    nationality: String, onNationality: (String) -> Unit,
    docNumber: String, onDocNumber: (String) -> Unit,
    docExpiry: String, onDocExpiry: (String) -> Unit
) {
    val labelPrefix = if (isPassport) "护照" else "证件"
    Text("$labelPrefix 个人资料", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

    DriverField(fullName, { v -> if (v.matches(Regex("^[a-zA-Z ]*$"))) onFullName(v) }, "$labelPrefix 全名")
    DriverDropdown(label = "$labelPrefix 性别", value = gender, options = genderOptions, onSelect = onGender)
    DriverField(birthDate, { v -> if (v.all { it.isDigit() || it == '-' }) onBirthDate(v) }, "出生日期 (YYYY-MM-DD)")
    DriverDropdown(label = "出生国家/地", value = birthCountry, options = countryList, onSelect = onBirthCountry)
    DriverDropdown(label = "国籍/公民", value = nationality, options = countryList, onSelect = onNationality)
    DriverField(docNumber, { v -> if (v.matches(Regex("^[a-zA-Z0-9]*$"))) onDocNumber(v) }, "$labelPrefix 号码")
    if (isPassport) {
        DriverField(docExpiry, { v -> if (v.all { it.isDigit() || it == '-' }) onDocExpiry(v) }, "$labelPrefix 到期日期 (YYYY-MM-DD)")
    }
}

/* ========== PAGE 3: 联系资料 ========== */

@Composable
private fun Page3Contact(
    residence: String, onResidence: (String) -> Unit,
    countryCode: String, onCountryCode: (String) -> Unit,
    phone: String, onPhone: (String) -> Unit,
    email: String, onEmail: (String) -> Unit
) {
    Text("联系资料", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

    DriverField(residence, onResidence, "居住地")

    val countryCodes = listOf("+60", "+65", "+86", "+91", "+62", "+66", "+84", "+63", "+81", "+82", "+1", "+44", "+61", "+33", "+49", "+7", "+55", "+27", "+966", "+971")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.width(110.dp)) {
            DriverDropdown(label = "区号", value = countryCode, options = countryCodes, onSelect = onCountryCode)
        }
        Box(modifier = Modifier.weight(1f)) {
            DriverField(phone, { v -> if (v.all { it.isDigit() }) onPhone(v) }, "手机号码")
        }
    }

    DriverField(email, onEmail, "电子邮箱")
}

/* ========== PAGE 4: 人脸识别 ========== */

@Composable
private fun Page4FaceRecognition(scanning: Boolean, complete: Boolean, onStartScan: () -> Unit) {
    Text("人脸识别", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    Text("请将脸部对准取景框内，并跟随导航随动完成扫描", color = DriverColors.TextMuted, fontSize = 13.sp)
    Spacer(Modifier.height(20.dp))

    Box(
        modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A2E)).border(2.dp, DriverColors.Primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            complete -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(DriverColors.Primary), contentAlignment = Alignment.Center
                ) { Text("✓", color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(12.dp))
                Text("扫描完成", color = DriverColors.Primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            scanning -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔍", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text("扫描中...", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🧑", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text("点击下方按钮开始人脸扫描", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }
    }

    Spacer(Modifier.height(16.dp))
    if (!complete) {
        Box(
            modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
                .background(if (scanning) DriverColors.SurfaceVariant else DriverColors.Primary)
                .clickable(enabled = !scanning) { onStartScan() },
            contentAlignment = Alignment.Center
        ) { Text(if (scanning) "请稍候..." else "开始人脸扫描", color = if (scanning) DriverColors.TextDisabled else Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium) }
    } else {
        Text("人脸识别已完成 ✓", color = DriverColors.Primary, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

/* ========== PAGE 5: 确认资料 ========== */

@Composable
private fun Page5Confirm(
    fullName: String, gender: String, birthDate: String, birthCountry: String, nationality: String,
    docNumber: String, docExpiry: String,
    residence: String, countryCode: String, phone: String, email: String,
    consentChecked: Boolean, onConsent: (Boolean) -> Unit,
    isPassport: Boolean, docType: String
) {
    Text("确认资料", color = DriverColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    Text("请仔细核对以下信息，确认无误后提交", color = DriverColors.TextMuted, fontSize = 13.sp)

    // 证件信息
    ConfirmCard("$docType 详情") {
        ConfirmRow("$docType 全名", fullName)
        ConfirmRow("$docType 性别", gender)
        ConfirmRow("出生日期", birthDate)
        ConfirmRow("出生国家/地", birthCountry)
        ConfirmRow("国籍/公民", nationality)
        ConfirmRow("$docType 号码", docNumber)
        if (isPassport) ConfirmRow("$docType 到期日期", docExpiry)
    }

    // 联系资料
    ConfirmCard("联系资料") {
        ConfirmRow("居住地", residence)
        ConfirmRow("国家/地区代码", "$countryCode $phone")
        ConfirmRow("电子邮箱", email)
    }

    Spacer(Modifier.height(8.dp))

    // 授权同意
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape)
                .background(if (consentChecked) DriverColors.Primary else DriverColors.SurfaceVariant)
                .border(1.dp, if (consentChecked) DriverColors.Primary else DriverColors.Border, CircleShape)
                .clickable { onConsent(!consentChecked) },
            contentAlignment = Alignment.Center
        ) { if (consentChecked) Text("✓", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("我已阅读并明确同意以下身份核验授权（本授权独立于服务协议）", color = DriverColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("1. 为完成账号实名认证、身份核验、防范账号欺诈，我同意平台收集我的身份证件照片、人脸扫描生物信息（人脸特征）。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            Text("2. 我知悉：上述证件图片、人脸数据，会加密传输给到第三方身份核验服务商 ID‑Analyzer，用于执行：证件质量检测、证件类型校验、OCR信息提取、动作活体检测、人脸1:1比对身份核验，服务商仅为本核验目的处理我的数据。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            Text("3. 我理解：本授权为完成KYC实名认证的必要条件，若拒绝授权，将无法完成实名认证，部分平台功能不可使用。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            Text("4. 平台不会将我的证件原图、人脸生物特征用于其他无关商业用途。平台优先仅存储核验结构化结果（文本信息、核验分数）；如业务需要留存证件原图，图片将设置自动过期删除周期，不会永久保存。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            Text("5. 根据马来西亚《个人数据保护法 PDPA》，我拥有查阅、更正、申请删除本人个人与生物识别数据的权利，也可以随时撤回本授权；撤回授权后，实名认证状态失效，可以通过平台客服提交申请。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            Text("6. 服务商ID‑Analyzer具备ISO27001信息安全认证，将按照合规标准保护我的身份与生物识别数据。", color = DriverColors.TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun ConfirmCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, color = DriverColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        content()
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = DriverColors.TextMuted, fontSize = 13.sp, modifier = Modifier.width(110.dp))
        Text(value, color = DriverColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
    }
}

/* ========== 通用组件 ========== */

@Composable
private fun DriverField(value: String, onChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
            .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp)).padding(16.dp),
        textStyle = TextStyle(color = DriverColors.TextPrimary, fontSize = 15.sp),
        cursorBrush = SolidColor(DriverColors.Primary),
        singleLine = true,
        decorationBox = { inner -> Box { if (value.isEmpty()) Text(placeholder, color = DriverColors.TextDisabled, fontSize = 15.sp); inner() } }
    )
}

@Composable
private fun DriverDropdown(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.Surface)
                .border(1.dp, DriverColors.Border, RoundedCornerShape(14.dp))
                .clickable { expanded = !expanded }.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (value.isBlank()) label else value,
                    color = if (value.isBlank()) DriverColors.TextDisabled else DriverColors.TextPrimary,
                    fontSize = 15.sp, modifier = Modifier.weight(1f)
                )
                Text(if (expanded) "▲" else "▼", color = DriverColors.TextMuted, fontSize = 11.sp)
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DriverColors.SurfaceVariant).padding(vertical = 4.dp)
            ) {
                options.forEach { opt ->
                    Box(
                        modifier = Modifier.fillMaxWidth().clickable { onSelect(opt); expanded = false }.padding(14.dp)
                    ) { Text(opt, color = DriverColors.TextPrimary, fontSize = 14.sp) }
                }
            }
        }
    }
}
