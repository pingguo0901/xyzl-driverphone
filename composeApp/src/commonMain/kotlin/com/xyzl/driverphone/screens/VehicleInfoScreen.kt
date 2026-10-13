package com.xyzl.driverphone.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ImageSource

private val VehicleBrands = listOf(
    "Perodua", "Proton", "Toyota", "Honda", "Nissan", "Mazda",
    "Hyundai", "Kia", "Mitsubishi", "BMW", "Mercedes-Benz", "其他",
)

private val VehicleModels = listOf(
    "Myvi", "Axia", "Bezza", "Alza", "Ativa", "Aruz",
    "Saga", "Persona", "Iriz", "X50", "X70", "X90",
    "Vios", "City", "Hilux", "Alphard", "其他",
)

private val SeatOptions = listOf("5座", "7座", "8座", "10座")

private fun sanitizePlate(input: String): String =
    input.uppercase().filter { (it.isLetterOrDigit() && it.code < 128) || it == ' ' }

/** 一条车辆记录 */
private data class VehicleRecord(
    val brand: String,
    val model: String,
    val trim: String,
    val plate: String,
    val verifyTime: String = "",
    val passTime: String = "",
    val status: String = "",
)

/** 已授权车辆（公司司机，示例数据，待后端下发；为空则提示向公司索取） */
private val AuthorizedVehicles = listOf(
    VehicleRecord("Toyota", "Alphard", "8座", "JXY 8888"),
)

/** 我的车辆（已验证，示例数据） */
private val MyVerifiedVehicles = listOf(
    VehicleRecord("Toyota", "Alphard", "8座", "JXY 8888"),
)

/** 待通过验证车辆（示例数据） */
private val PendingVehicles = listOf(
    VehicleRecord("Honda", "CR-V", "7座", "WMB 2233", verifyTime = "12/10/2026 14:20", passTime = "-", status = "审核中"),
)

/** 车辆信息页：公司司机=已授权车辆 + 申请授权；个人=我的车辆 + 待通过验证 + 增加车辆 */
@Composable
fun VehicleInfoScreen(
    onBack: () -> Unit = {},
    isCompanyDriver: Boolean = false,
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    var showAdd by remember { mutableStateOf(false) }

    when {
        isCompanyDriver -> CompanyVehicleView(onBack = onBack)
        showAdd -> AddVehicleScreen(
            onBack = { showAdd = false },
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
        )
        else -> MyVehiclesView(onBack = onBack, onAdd = { showAdd = true })
    }
}

/** 公司司机：已授权车辆 + 申请授权车辆 */
@Composable
private fun CompanyVehicleView(onBack: () -> Unit) {
    var vehicleId by remember { mutableStateOf("") }
    var requested by remember { mutableStateOf(false) }

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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("已授权车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                if (AuthorizedVehicles.isEmpty()) {
                    Text("请向所属公司索取授权车辆", fontSize = 14.sp, color = FormMuted)
                } else {
                    AuthorizedVehicles.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        if (index != AuthorizedVehicles.lastIndex) {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = FormChip, thickness = 1.dp)
        Spacer(Modifier.height(20.dp))

        FormSectionTitle("申请授权车辆")
        FormTextField(
            label = "车辆ID",
            value = vehicleId,
            placeholder = "请输入公司下发的车辆ID",
            onValueChange = { vehicleId = it },
        )
        Spacer(Modifier.height(8.dp))
        FormPrimaryButton(
            text = "确认请求授权",
            enabled = vehicleId.isNotBlank(),
            onClick = { requested = true },
        )
        Spacer(Modifier.height(110.dp))
    }

    if (requested) {
        AlertDialog(
            onDismissRequest = { requested = false },
            containerColor = FormCard,
            title = { Text("请求已提交", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("授权车辆请求已发送给所属公司，请等待公司授权。", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = { requested = false }) {
                    Text("知道了", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

/** 个人司机：我的车辆 + 待通过验证车辆；右上角「增加车辆」 */
@Composable
private fun MyVehiclesView(onBack: () -> Unit, onAdd: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderAction(title = "车辆信息", actionLabel = "增加车辆", onBack = onBack, onAction = onAdd)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("我的车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                if (MyVerifiedVehicles.isEmpty()) {
                    Text("暂无已验证车辆", fontSize = 14.sp, color = FormMuted)
                } else {
                    MyVerifiedVehicles.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        if (index != MyVerifiedVehicles.lastIndex) {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = FormChip, thickness = 1.dp)
        Spacer(Modifier.height(20.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("待通过验证车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                Spacer(Modifier.height(12.dp))
                if (PendingVehicles.isEmpty()) {
                    Text("暂无待验证车辆", fontSize = 14.sp, color = FormMuted)
                } else {
                    PendingVehicles.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        Spacer(Modifier.height(8.dp))
                        InfoLine("验证日期时间", vehicle.verifyTime)
                        InfoLine("通过日期时间", vehicle.passTime)
                        InfoLine("状态", vehicle.status)
                        if (index != PendingVehicles.lastIndex) {
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun VehicleInfoLines(vehicle: VehicleRecord) {
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

/** 增加车辆页：车辆资料 + 三份证明上传 + 车主确认 */
@Composable
fun AddVehicleScreen(
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var frontViewUri by remember { mutableStateOf<String?>(null) }
    var rearViewUri by remember { mutableStateOf<String?>(null) }
    var frontSeatUri by remember { mutableStateOf<String?>(null) }
    var rearSeatUri by remember { mutableStateOf<String?>(null) }
    var roadTaxUri by remember { mutableStateOf<String?>(null) }
    var carCardUri by remember { mutableStateOf<String?>(null) }
    var insuranceUri by remember { mutableStateOf<String?>(null) }
    var ownerAnswer by remember { mutableStateOf(0) }
    var ownerIcUri by remember { mutableStateOf<String?>(null) }
    var cannotGetOwnerIc by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "增加车辆", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormSelect(
            label = "品牌",
            value = brand,
            placeholder = "选择品牌",
            options = VehicleBrands,
            onSelect = { brand = it },
        )
        FormSelect(
            label = "型号",
            value = model,
            placeholder = "选择型号",
            options = VehicleModels,
            onSelect = { model = it },
        )
        FormSelect(
            label = "车款",
            value = seats,
            placeholder = "选择车款",
            options = SeatOptions,
            onSelect = { seats = it },
        )
        FormTextField(
            label = "车牌号",
            value = plate,
            placeholder = "例：JXY 8888",
            onValueChange = { plate = sanitizePlate(it) },
        )

        FormSectionTitle("车外观照片")
        Text(
            "上传车辆外观前后照片",
            fontSize = 13.sp,
            color = FormMuted,
            modifier = Modifier.padding(start = 2.dp, bottom = 10.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormUploadTile(
                label = "车前",
                hint = "车前",
                uri = frontViewUri,
                onPicked = { frontViewUri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
                modifier = Modifier.weight(1f),
            )
            FormUploadTile(
                label = "车后",
                hint = "车后",
                uri = rearViewUri,
                onPicked = { rearViewUri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
                modifier = Modifier.weight(1f),
            )
        }

        FormSectionTitle("车辆内饰照片")
        Text(
            "上传车辆内饰前后排照片",
            fontSize = 13.sp,
            color = FormMuted,
            modifier = Modifier.padding(start = 2.dp, bottom = 10.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormUploadTile(
                label = "车前排",
                hint = "车前排",
                uri = frontSeatUri,
                onPicked = { frontSeatUri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
                modifier = Modifier.weight(1f),
            )
            FormUploadTile(
                label = "车后排",
                hint = "车后排",
                uri = rearSeatUri,
                onPicked = { rearSeatUri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
                modifier = Modifier.weight(1f),
            )
        }

        FormSectionTitle("车辆证明")
        FormUpload(
            label = "路税证明",
            hint = "上传马来西亚交通局合规路税证明（拍照 / 相册 / 文档）",
            uri = roadTaxUri,
            onPicked = { roadTaxUri = it },
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
        )
        FormUpload(
            label = "车卡证明",
            hint = "上传马来西亚交通局合规车卡证明（拍照 / 相册 / 文档）",
            uri = carCardUri,
            onPicked = { carCardUri = it },
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
        )
        FormUpload(
            label = "保险证明",
            hint = "上传马来西亚第三方保险证明（拍照 / 相册 / 文档）",
            uri = insuranceUri,
            onPicked = { insuranceUri = it },
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
        )

        FormSectionTitle("您是否为该车辆车主")
        SegmentedTabs(
            options = listOf("是", "否"),
            selectedIndex = ownerAnswer,
            onSelect = { ownerAnswer = it },
        )
        Spacer(Modifier.height(16.dp))

        if (ownerAnswer == 1) {
            FormUpload(
                label = "车主马来西亚身份证",
                hint = "上传马来西亚身份证（仅支持拍照）",
                uri = ownerIcUri,
                onPicked = { ownerIcUri = it },
                onPickImage = onPickImage,
                onPickDocument = onPickDocument,
                allowGallery = false,
                allowDocument = false,
            )
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = cannotGetOwnerIc,
                    onCheckedChange = { cannotGetOwnerIc = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = FormGreen,
                        uncheckedColor = FormMuted,
                        checkmarkColor = Color.White,
                    ),
                )
                Text("我无法获取该车主身份证", color = FormInk, fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(6.dp))
        FormPrimaryButton(
            text = "完成提交",
            onClick = {
                val ownerIncomplete = ownerAnswer == 1 && (ownerIcUri == null || cannotGetOwnerIc)
                dialog = if (ownerIncomplete) 1 else 2
            },
        )
        Spacer(Modifier.height(110.dp))
    }

    if (dialog == 1) {
        AlertDialog(
            onDismissRequest = { dialog = 0 },
            containerColor = FormCard,
            title = { Text("信息不全", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("信息不全，请联系平台客服协助办理，谢谢", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = { dialog = 0 }) {
                    Text("知道了", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    if (dialog == 2) {
        AlertDialog(
            onDismissRequest = { dialog = 0 },
            containerColor = FormCard,
            title = { Text("提交成功", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("车辆资料已提交，等待平台审核", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    dialog = 0
                    onBack()
                }) {
                    Text("完成", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
