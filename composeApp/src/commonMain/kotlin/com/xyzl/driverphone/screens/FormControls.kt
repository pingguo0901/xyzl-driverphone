package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.xyzl.driverphone.ImageSource

internal val FormCard = Color(0xFF1C1C1E)
internal val FormChip = Color(0xFF27272A)
internal val FormInk = Color(0xFFFFFFFF)
internal val FormMuted = Color(0xFFA1A1AA)
internal val FormGreen = Color(0xFF3B82F6)
internal val FormDivide = Color(0xFF27272A)
internal val FormRed = Color(0xFFF43F5E)

/** 协议链接文字（蓝色可点，未登录也能打开） */
@Composable
internal fun PolicyLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = FormGreen,
        fontSize = 12.sp,
        modifier = Modifier.clickable { onClick() },
    )
}

@Composable
internal fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FormGreen,
    unfocusedBorderColor = FormDivide,
    focusedTextColor = FormInk,
    unfocusedTextColor = FormInk,
    cursorColor = FormGreen,
    focusedContainerColor = FormCard,
    unfocusedContainerColor = FormCard,
    focusedPlaceholderColor = Color(0xFF52525B),
    unfocusedPlaceholderColor = Color(0xFF52525B),
)

/** 二级页标题栏：左上角返回 + 居中标题 */
@Composable
fun FormPageHeader(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(44.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FormChip)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = FormInk,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = FormInk,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun FormLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = FormMuted,
        modifier = modifier.padding(start = 2.dp, bottom = 8.dp),
    )
}

@Composable
fun FormSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = FormInk,
        modifier = modifier.padding(start = 2.dp, top = 4.dp, bottom = 10.dp),
    )
}

/** 普通输入框 */
@Composable
fun FormTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardNumber: Boolean = false,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = FormMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (keyboardNumber) KeyboardType.Number else KeyboardType.Text,
            ),
            colors = formFieldColors(),
        )
    }
}

/** 弹出式选项（下拉） */
@Composable
fun FormSelect(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FormCard)
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value.ifBlank { placeholder },
                    color = if (value.isBlank()) FormMuted else FormInk,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = FormMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (expanded) {
                Popup(
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FormCard,
                        shadowElevation = 8.dp,
                    ) {
                        Column(Modifier.width(220.dp).padding(vertical = 6.dp)) {
                            options.forEach { option ->
                                Text(
                                    option,
                                    fontSize = 15.sp,
                                    color = FormInk,
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
}

/** 紧凑弹出式选项（用于区号那种窄栏） */
@Composable
fun FormSelectCompact(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FormCard)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(value, color = FormInk, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = FormMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FormCard,
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.width(140.dp).padding(vertical = 6.dp)) {
                        options.forEach { option ->
                            Text(
                                option,
                                fontSize = 15.sp,
                                color = FormInk,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(option)
                                        expanded = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 点击式选择栏（生日等） */
@Composable
fun FormPickerField(
    label: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FormCard)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = text.ifBlank { "选择日期" },
                color = if (text.isBlank()) FormMuted else FormInk,
                fontSize = 15.sp,
            )
        }
    }
}

/** 选择胶囊（男/女、已过期/有效 这类小选项） */
@Composable
fun FormChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selected) FormGreen else FormChip)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) Color.White else FormMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

/** 上传框：拍照 / 相册 / 文档（可分别开关），带上方标题 */
@Composable
fun FormUpload(
    label: String,
    hint: String,
    uri: String?,
    onPicked: (String) -> Unit,
    onPickImage: (ImageSource, (String) -> Unit) -> Unit,
    onPickDocument: ((String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    allowCamera: Boolean = true,
    allowGallery: Boolean = true,
    allowDocument: Boolean = true,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        FormUploadTile(
            label = label,
            hint = hint,
            uri = uri,
            onPicked = onPicked,
            onPickImage = onPickImage,
            onPickDocument = onPickDocument,
            modifier = Modifier.fillMaxWidth(),
            allowCamera = allowCamera,
            allowGallery = allowGallery,
            allowDocument = allowDocument,
        )
    }
}

/** 上传框（不带标题，可放一排两个框）：拍照 / 相册 / 文档 */
@Composable
fun FormUploadTile(
    label: String,
    hint: String,
    uri: String?,
    onPicked: (String) -> Unit,
    onPickImage: (ImageSource, (String) -> Unit) -> Unit,
    onPickDocument: ((String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    allowCamera: Boolean = true,
    allowGallery: Boolean = true,
    allowDocument: Boolean = true,
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(
        modifier
            .height(124.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FormCard)
            .clickable { showSheet = true },
        contentAlignment = Alignment.Center,
    ) {
        if (uri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(hint, color = FormMuted, fontSize = 13.sp)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = FormGreen)
                Spacer(Modifier.height(4.dp))
                Text("$hint 已上传", color = FormInk, fontSize = 12.sp)
            }
        }
    }

    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FormCard,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(label, color = FormInk, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    if (allowCamera) {
                        FormSheetRow(Icons.Filled.PhotoCamera, "拍照") {
                            showSheet = false
                            onPickImage(ImageSource.Camera, onPicked)
                        }
                    }
                    if (allowGallery) {
                        FormSheetRow(Icons.Filled.PhotoLibrary, "从相册选择") {
                            showSheet = false
                            onPickImage(ImageSource.Gallery, onPicked)
                        }
                    }
                    if (allowDocument) {
                        FormSheetRow(Icons.Filled.Description, "选择文档") {
                            showSheet = false
                            onPickDocument(onPicked)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = FormMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSheetRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = FormGreen)
        Spacer(Modifier.width(12.dp))
        Text(label, color = FormInk, fontSize = 15.sp)
    }
}

/** 密码输入框（默认隐藏明文） */
@Composable
fun FormPasswordField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = FormMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default,
            colors = formFieldColors(),
        )
    }
}

/** 带右侧文字按钮的二级页标题栏（如：返回 + 标题 + 编辑） */
@Composable
fun FormPageHeaderAction(
    title: String,
    actionLabel: String,
    onBack: () -> Unit,
    onAction: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(44.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FormChip)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = FormInk,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = FormInk,
            modifier = Modifier.align(Alignment.Center),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(FormChip)
                .clickable { onAction() }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(actionLabel, fontSize = 14.sp, color = FormGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 开关行（设置类页面） */
@Composable
fun FormSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = FormInk, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FormGreen,
                uncheckedThumbColor = FormMuted,
                uncheckedTrackColor = FormChip,
                uncheckedBorderColor = FormDivide,
            ),
        )
    }
}

/** 值行（可点，如「字体大小」「多语言」、当前模式） */
@Composable
fun FormValueRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = FormInk, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = FormMuted)
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = FormMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 语言/单选列表行：右侧绿色打勾 */
@Composable
fun FormCheckRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = FormInk, modifier = Modifier.weight(1f))
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "已选择",
                tint = FormGreen,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** 小字说明文字 */
@Composable
fun FormNote(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        fontSize = 12.sp,
        color = FormMuted,
        lineHeight = 18.sp,
        modifier = modifier.padding(start = 2.dp, bottom = 6.dp),
    )
}

/** 分组卡片容器 */
@Composable
fun FormGroup(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FormCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) { content() }
    }
}

/** 左上角取消 + 居中标题 + 右上角完成 */
@Composable
fun FormPageHeaderCancelDone(
    title: String,
    onCancel: () -> Unit,
    onDone: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(44.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(FormChip)
                .clickable { onCancel() }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("取消", fontSize = 14.sp, color = FormMuted, fontWeight = FontWeight.SemiBold)
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = FormInk,
            modifier = Modifier.align(Alignment.Center),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(FormChip)
                .clickable { onDone() }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("完成", fontSize = 14.sp, color = FormGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 主按钮（保存更改 / 完成提交） */
@Composable
fun FormPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    danger: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (danger) FormRed else FormGreen,
            disabledContainerColor = FormChip,
        ),
    ) {
        Text(
            text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) Color.White else FormMuted,
        )
    }
}
