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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.model.Currencies
import com.xyzl.driverphone.model.DutyVehicles
import com.xyzl.driverphone.model.ExpenseTypes
import com.xyzl.driverphone.model.currencyForType

private val OnlineGreen = Color(0xFF206A4E)
private val InkDark = Color(0xFF111113)
private val MutedGray = Color(0xFF8E8E93)
private val FieldGray = Color(0xFFF2F2F4)
private val DividerGray = Color(0xFFE8E8EC)

/** 提交报销页 */
@Composable
fun ExpenseSubmitScreen(
    onBack: () -> Unit = {},
    onPickImage: (source: ImageSource, onPicked: (String) -> Unit) -> Unit = { _, _ -> },
    onPickDocument: (onPicked: (String) -> Unit) -> Unit = {},
    onSubmit: () -> Unit = {},
) {
    var vehicle by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("MYR") }
    var amount by remember { mutableStateOf("") }
    var receiptUri by remember { mutableStateOf<String?>(null) }

    val canSubmit = vehicle.isNotBlank() && type.isNotBlank() && amount.isNotBlank() && receiptUri != null

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 顶部：返回 + 居中标题
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FieldGray)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = InkDark,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "提交报销",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkDark,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(20.dp))

        // 归属车辆
        FieldLabel("归属车辆")
        DropdownField(
            text = vehicle.ifBlank { "请选择车辆" },
            placeholder = vehicle.isBlank(),
            options = DutyVehicles,
            onSelect = { vehicle = it },
        )

        Spacer(Modifier.height(18.dp))

        // 类型
        FieldLabel("类型")
        DropdownField(
            text = type.ifBlank { "请选择类型" },
            placeholder = type.isBlank(),
            options = ExpenseTypes,
            onSelect = {
                type = it
                currency = currencyForType(it)
            },
        )

        Spacer(Modifier.height(18.dp))

        // 金额：货币 + 输入框
        FieldLabel("金额")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DropdownField(
                text = currency,
                placeholder = false,
                options = Currencies,
                onSelect = { currency = it },
                modifier = Modifier.width(110.dp),
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    amount = input.filter { it.isDigit() || it == "." }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("0.00", color = MutedGray) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OnlineGreen,
                    unfocusedBorderColor = DividerGray,
                    focusedTextColor = InkDark,
                    unfocusedTextColor = InkDark,
                    cursorColor = OnlineGreen,
                ),
            )
        }

        Spacer(Modifier.height(18.dp))

        // 收据证明
        FieldLabel("收据证明")
        ReceiptUploadBox(
            uri = receiptUri,
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
            onPicked = { receiptUri = it },
        )

        Spacer(Modifier.height(28.dp))

        // 确认提交
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (canSubmit) OnlineGreen else Color(0xFFD9D9DE))
                .clickable(enabled = canSubmit) { onSubmit() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "确认提交",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (canSubmit) Color.White else Color(0xFF9A9AA0),
                letterSpacing = 2.sp,
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MutedGray,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

/** 弹出式选项：点击展开 Popup 列表 */
@Composable
private fun DropdownField(
    text: String,
    placeholder: Boolean,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FieldGray)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text,
                fontSize = 15.sp,
                color = if (placeholder) MutedGray else InkDark,
            )
        }

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.width(240.dp).padding(vertical = 6.dp)) {
                        options.forEach { option ->
                            Text(
                                option,
                                fontSize = 15.sp,
                                color = InkDark,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(option)
                                        expanded = false
                                    }
                                    .padding(horizontal = 18.dp, vertical = 12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 收据上传框：拍照 / 相册 / 文档 */
@Composable
private fun ReceiptUploadBox(
    uri: String?,
    onPickImage: (source: ImageSource, onPicked: (String) -> Unit) -> Unit,
    onPickDocument: (onPicked: (String) -> Unit) -> Unit,
    onPicked: (String) -> Unit,
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FieldGray)
            .clickable { showSheet = true },
        contentAlignment = Alignment.Center,
    ) {
        if (uri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.UploadFile, null, tint = OnlineGreen, modifier = Modifier.size(30.dp))
                Spacer(Modifier.height(8.dp))
                Text("拍照 / 相册 / 文档", color = MutedGray, fontSize = 13.sp)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, null, tint = OnlineGreen)
                Spacer(Modifier.width(8.dp))
                Text("已上传收据", color = InkDark, fontSize = 14.sp)
            }
        }
    }

    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("上传收据证明", color = InkDark, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    SheetRow(Icons.Filled.PhotoCamera, "拍照") {
                        showSheet = false
                        onPickImage(ImageSource.Camera, onPicked)
                    }
                    SheetRow(Icons.Filled.PhotoLibrary, "从相册选择") {
                        showSheet = false
                        onPickImage(ImageSource.Gallery, onPicked)
                    }
                    SheetRow(Icons.Filled.Description, "选择文档") {
                        showSheet = false
                        onPickDocument(onPicked)
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = MutedGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = OnlineGreen)
        Spacer(Modifier.width(12.dp))
        Text(label, color = InkDark, fontSize = 15.sp)
    }
}
