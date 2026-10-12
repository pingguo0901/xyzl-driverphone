package com.xyzl.driverphone.screens

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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

/** 车辆信息页：车辆资料 + 三份证明上传 + 车主确认 */
@Composable
fun VehicleInfoScreen(
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: ((String) -> Unit) -> Unit = {},
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
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
        FormPageHeader(title = "车辆信息", onBack = onBack)
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
