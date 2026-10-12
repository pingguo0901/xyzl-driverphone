package com.xyzl.driverphone.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class DeviceInfo(
    val id: String,
    val name: String,
    val type: String,
    val lastActive: String,
    val signedIn: Boolean,
)

private val initialDevices = listOf(
    DeviceInfo("d1", "iPhone 15 Pro", "iOS 18 · 手机", "刚刚", true),
    DeviceInfo("d2", "Xiaomi 14", "Android 15 · 手机", "今天 14:20", true),
    DeviceInfo("d3", "iPad Air", "iPadOS 18 · 平板", "昨天 21:05", true),
    DeviceInfo("d4", "Huawei Mate 40", "Android 12 · 手机", "10-08 09:12", false),
    DeviceInfo("d5", "Samsung Galaxy S21", "Android 13 · 手机", "09-30 18:40", false),
)

/** 登录设备管理页：已登录/已退出设备 + 编辑删除 + 设备详情 */
@Composable
fun DeviceManageScreen(onBack: () -> Unit = {}) {
    var devices by remember { mutableStateOf(initialDevices) }
    var editing by remember { mutableStateOf(false) }
    var revealed by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<DeviceInfo?>(null) }
    var pendingDelete by remember { mutableStateOf<DeviceInfo?>(null) }

    val current = detail
    if (current != null) {
        DeviceDetailScreen(
            device = current,
            onBack = { detail = null },
            onDelete = { pendingDelete = current },
        )
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            FormPageHeaderAction(
                title = "登录设备管理",
                actionLabel = if (editing) "完成" else "编辑",
                onBack = onBack,
                onAction = {
                    editing = !editing
                    if (!editing) revealed = null
                },
            )
            Spacer(Modifier.height(16.dp))

            FormSectionTitle("已登录的设备")
            val signedIn = devices.filter { it.signedIn }
            if (signedIn.isEmpty()) {
                Text("暂无已登录设备", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            } else {
                signedIn.forEach { device ->
                    DeviceRow(
                        device = device,
                        editing = editing,
                        revealed = revealed == device.id,
                        onClick = { if (!editing) detail = device },
                        onMinus = { revealed = if (revealed == device.id) null else device.id },
                        onDelete = { pendingDelete = device },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            FormSectionTitle("已退出的设备")
            val signedOut = devices.filter { !it.signedIn }
            if (signedOut.isEmpty()) {
                Text("暂无已退出设备", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            } else {
                signedOut.forEach { device ->
                    DeviceRow(
                        device = device,
                        editing = editing,
                        revealed = revealed == device.id,
                        onClick = { if (!editing) detail = device },
                        onMinus = { revealed = if (revealed == device.id) null else device.id },
                        onDelete = { pendingDelete = device },
                    )
                }
            }

            Spacer(Modifier.height(110.dp))
        }
    }

    val target = pendingDelete
    if (target != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = FormCard,
            title = {
                Text("删除设备", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
            },
            text = {
                Text(
                    "删除后，下次在该设备登录时需要进行安全验证。",
                    fontSize = 14.sp,
                    color = FormMuted,
                    lineHeight = 20.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    devices = devices.filter { it.id != target.id }
                    revealed = null
                    detail = null
                    pendingDelete = null
                }) {
                    Text("删除", color = FormRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("取消", color = FormMuted)
                }
            },
        )
    }
}

/** 设备行：编辑态左侧 - 按钮，点 - 后卡片左滑露出右侧删除按钮 */
@Composable
private fun DeviceRow(
    device: DeviceInfo,
    editing: Boolean,
    revealed: Boolean,
    onClick: () -> Unit,
    onMinus: () -> Unit,
    onDelete: () -> Unit,
) {
    val offset by animateDpAsState(
        targetValue = if (revealed) (-88).dp else 0.dp,
        label = "deviceOffset",
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FormRed),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(88.dp)
                .fillMaxHeight()
                .clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Text("删除", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(x = offset)
                .clip(RoundedCornerShape(16.dp))
                .background(FormCard)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (editing) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(FormRed)
                        .clickable { onMinus() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("−", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(device.name, fontSize = 15.sp, color = FormInk, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(device.type, fontSize = 12.sp, color = FormMuted)
            }
            Text(device.lastActive, fontSize = 12.sp, color = FormMuted)
        }
    }
}

/** 设备详情页 */
@Composable
private fun DeviceDetailScreen(
    device: DeviceInfo,
    onBack: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "设备详情", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                DetailRow("设备名称", device.name)
                HorizontalDivider(color = FormDivide, thickness = 1.dp)
                DetailRow("设备类型", device.type)
                HorizontalDivider(color = FormDivide, thickness = 1.dp)
                DetailRow("最近活跃时间", device.lastActive)
            }
        }

        Spacer(Modifier.height(24.dp))
        FormPrimaryButton(text = "删除该设备", onClick = onDelete, danger = true)
        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 14.sp, color = FormMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 15.sp, color = FormInk, fontWeight = FontWeight.Medium)
    }
}
