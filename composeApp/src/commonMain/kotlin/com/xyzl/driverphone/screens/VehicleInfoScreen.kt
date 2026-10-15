package com.xyzl.driverphone.screens

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.AuthRepository
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.NewVehicleInput
import com.xyzl.driverphone.PickerOption
import com.xyzl.driverphone.VehicleCard
import com.xyzl.driverphone.VehicleRepository
import kotlinx.coroutines.launch

private val OwnerOptions = listOf("是", "否")

private fun sanitizePlate(input: String): String =
    input.uppercase().filter { (it.isLetterOrDigit() && it.code < 128) || it == ' ' }

/** 时间戳显示：2026-10-12T14:20:31+08:00 → 2026-10-12 14:20 */
private fun prettyTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    val t = raw.replace("T", " ")
    return if (t.length >= 16) t.substring(0, 16) else t
}

private fun statusLabel(status: String?): String = when (status) {
    "approved" -> "已通过"
    "pending" -> "审核中"
    "rejected" -> "已拒绝"
    else -> status ?: "-"
}

/** 车辆信息页：我的车辆（已通过）+ 待通过验证；personal 才有右上角「增加车辆」 */
@Composable
fun VehicleInfoScreen(
    userId: String? = null,
    onBack: () -> Unit = {},
    isCompanyDriver: Boolean = false,
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    var showAdd by remember { mutableStateOf(false) }

    if (isCompanyDriver) {
        CompanyVehicleView(onBack = onBack)
        return
    }

    if (showAdd) {
        AddVehicleScreen(
            userId = userId,
            onBack = { showAdd = false },
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
        )
        return
    }

    MyVehiclesView(
        userId = userId,
        onBack = onBack,
        onAdd = { showAdd = true },
    )
}

/** 公司司机：已授权车辆（公司分配，不在 se_vehicle_profiles 的 owner_id 维度里） */
@Composable
private fun CompanyVehicleView(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "车辆信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("已授权车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                Text("请向所属公司索取授权车辆", fontSize = 14.sp, color = FormMuted)
            }
        }
        Spacer(Modifier.height(110.dp))
    }
}

/** 个人：我的车辆 + 待通过验证车辆 */
@Composable
private fun MyVehiclesView(
    userId: String?,
    onBack: () -> Unit,
    onAdd: () -> Unit,
) {
    val repo = remember { VehicleRepository() }
    var isPersonal by remember { mutableStateOf(false) }
    var approved by remember { mutableStateOf<List<VehicleCard>>(emptyList()) }
    var pending by remember { mutableStateOf<List<VehicleCard>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val id = userId ?: return@LaunchedEffect
        isPersonal = AuthRepository().getUserRole(id) == "personal"
        val accountId = repo.accountId(id)
        if (accountId != null) {
            approved = repo.vehicles(accountId, "approved")
            pending = repo.vehicles(accountId, "pending")
        }
        loaded = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (isPersonal) {
            FormPageHeaderAction(title = "车辆信息", actionLabel = "增加车辆", onBack = onBack, onAction = onAdd)
        } else {
            FormPageHeader(title = "车辆信息", onBack = onBack)
        }
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("我的车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                when {
                    !loaded -> Text("加载中…", fontSize = 14.sp, color = FormMuted)
                    approved.isEmpty() -> Text("暂无已验证车辆", fontSize = 14.sp, color = FormMuted)
                    else -> approved.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        if (index != approved.lastIndex) Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = FormChip, thickness = 1.dp)
        Spacer(Modifier.height(20.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("待通过验证车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                when {
                    !loaded -> Text("加载中…", fontSize = 14.sp, color = FormMuted)
                    pending.isEmpty() -> Text("暂无待验证车辆", fontSize = 14.sp, color = FormMuted)
                    else -> pending.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        Spacer(Modifier.height(8.dp))
                        InfoLine("验证日期时间", prettyTime(vehicle.verifyAt))
                        InfoLine("通过日期时间", prettyTime(vehicle.verifiedAt))
                        InfoLine("状态", statusLabel(vehicle.status))
                        if (index != pending.lastIndex) Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun VehicleInfoLines(vehicle: VehicleCard) {
    InfoLine("品牌", vehicle.brand)
    InfoLine("型号", vehicle.model)
    InfoLine("车款", vehicle.trim)
    InfoLine("车牌", vehicle.plate)
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = FormMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FormInk)
    }
}

/** 增加车辆页：品牌/型号/车款下拉 + 车牌 + 4 张照片 + 3 份证明 + 车主身份 */
@Composable
fun AddVehicleScreen(
    userId: String? = null,
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    val repo = remember { VehicleRepository() }
    val scope = rememberCoroutineScope()

    var brands by remember { mutableStateOf<List<PickerOption>>(emptyList()) }
    var models by remember { mutableStateOf<List<PickerOption>>(emptyList()) }
    var trims by remember { mutableStateOf<List<PickerOption>>(emptyList()) }

    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var trim by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    var frontPhoto by remember { mutableStateOf<String?>(null) }
    var backPhoto by remember { mutableStateOf<String?>(null) }
    var frontSeatPhoto by remember { mutableStateOf<String?>(null) }
    var backSeatPhoto by remember { mutableStateOf<String?>(null) }
    var roadTaxFile by remember { mutableStateOf<String?>(null) }
    var regCardFile by remember { mutableStateOf<String?>(null) }
    var insuranceFile by remember { mutableStateOf<String?>(null) }

    var isOwnerAnswer by remember { mutableStateOf(OwnerOptions.first()) }
    var ownerIcFile by remember { mutableStateOf<String?>(null) }
    var ownerIcUnavailable by remember { mutableStateOf(false) }

    var saving by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { brands = repo.brands() }

    val isOwner = isOwnerAnswer == "是"
    val brandId = brands.firstOrNull { it.label == brand }?.id
    val modelId = models.firstOrNull { it.label == model }?.id
    val trimId = trims.firstOrNull { it.label == trim }?.id

    val ready = brandId != null && modelId != null && plate.isNotBlank() &&
        frontPhoto != null && backPhoto != null && frontSeatPhoto != null && backSeatPhoto != null &&
        roadTaxFile != null && regCardFile != null && insuranceFile != null &&
        (isOwner || ownerIcFile != null || ownerIcUnavailable)

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "增加车辆", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {

            if (brands.isEmpty()) {
                FormNote("品牌列表是空的（se_vehicle_brands 没有 is_active = true 的数据），先让管理端加品牌。")
                Spacer(Modifier.height(10.dp))
            }

            FormSelect(
                label = "品牌",
                value = brand,
                placeholder = "选择品牌",
                options = brands.map { it.label },
                onSelect = { picked ->
                    brand = picked
                    model = ""
                    trim = ""
                    val id = brands.firstOrNull { it.label == picked }?.id
                    scope.launch { models = if (id == null) emptyList() else repo.models(id) }
                },
            )
            FormSelect(
                label = "型号",
                value = model,
                placeholder = "选择型号",
                options = models.map { it.label },
                onSelect = { picked ->
                    model = picked
                    trim = ""
                    val id = models.firstOrNull { it.label == picked }?.id
                    scope.launch { trims = if (id == null) emptyList() else repo.trims(id) }
                },
            )
            FormSelect(
                label = "车款",
                value = trim,
                placeholder = "选择车款",
                options = trims.map { it.label },
                onSelect = { trim = it },
            )
            FormTextField(
                label = "车牌号",
                value = plate,
                placeholder = "例如 WXY 1234",
                onValueChange = { plate = sanitizePlate(it) },
            )

            FormSectionTitle("车辆照片")
            FormUpload(
                label = "车前照片",
                hint = "拍照或从相册选择",
                uri = frontPhoto,
                onPicked = { frontPhoto = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormUpload(
                label = "车后照片",
                hint = "拍照或从相册选择",
                uri = backPhoto,
                onPicked = { backPhoto = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormUpload(
                label = "车前排照片",
                hint = "拍照或从相册选择",
                uri = frontSeatPhoto,
                onPicked = { frontSeatPhoto = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormUpload(
                label = "车后排照片",
                hint = "拍照或从相册选择",
                uri = backSeatPhoto,
                onPicked = { backSeatPhoto = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )

            FormSectionTitle("车辆证明")
            FormUpload(
                label = "路税证明",
                hint = "拍照或上传文件",
                uri = roadTaxFile,
                onPicked = { roadTaxFile = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormUpload(
                label = "车卡证明",
                hint = "拍照或上传文件",
                uri = regCardFile,
                onPicked = { regCardFile = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )
            FormUpload(
                label = "保险证明",
                hint = "拍照或上传文件",
                uri = insuranceFile,
                onPicked = { insuranceFile = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
            )

            FormSectionTitle("是否车主")
            SegmentedTabs(
                options = OwnerOptions,
                selectedIndex = OwnerOptions.indexOf(isOwnerAnswer).coerceAtLeast(0),
                onSelect = { isOwnerAnswer = OwnerOptions[it] },
            )
            Spacer(Modifier.height(14.dp))

            if (!isOwner) {
                FormUpload(
                    label = "车主马来西亚身份证",
                    hint = "仅拍照上传",
                    uri = ownerIcFile,
                    onPicked = { ownerIcFile = it },
                    onPickImage = onPickImage,
                    onPickDocument = onPickDocument,
                    allowDocument = false,
                )
                FormCheckRow(
                    label = "我无法获取该车主身份证",
                    selected = ownerIcUnavailable,
                    onClick = { ownerIcUnavailable = !ownerIcUnavailable },
                )
            }

            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = if (saving) "提交中…" else "完成提交",
            enabled = ready && !saving,
            onClick = {
                val id = userId
                val bId = brandId
                val mId = modelId
                if (id == null) {
                    dialogTitle = "请先登录"
                    dialogBody = "登录后才能提交车辆"
                    showDialog = true
                } else if (bId == null || mId == null) {
                    dialogTitle = "资料没填全"
                    dialogBody = "品牌和型号都要选"
                    showDialog = true
                } else {
                    saving = true
                    scope.launch {
                        val accountId = repo.accountId(id)
                        if (accountId == null) {
                            saving = false
                            dialogTitle = "提交失败"
                            dialogBody = "找不到你的账户（se_account_members 没有你的记录）"
                            showDialog = true
                            return@launch
                        }
                        val err = repo.submitVehicle(
                            userId = id,
                            accountId = accountId,
                            input = NewVehicleInput(
                                brandId = bId,
                                modelId = mId,
                                trimId = trimId,
                                plate = plate.trim(),
                                isOwner = isOwner,
                                frontPhoto = frontPhoto,
                                backPhoto = backPhoto,
                                frontSeatPhoto = frontSeatPhoto,
                                backSeatPhoto = backSeatPhoto,
                                roadTaxFile = roadTaxFile,
                                registrationCardFile = regCardFile,
                                insuranceFile = insuranceFile,
                                ownerIcFile = ownerIcFile,
                                ownerIcUnavailable = ownerIcUnavailable,
                            ),
                        )
                        saving = false
                        if (err == null) {
                            dialogTitle = "提交成功"
                            dialogBody = "车辆资料已提交，等待审核"
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
