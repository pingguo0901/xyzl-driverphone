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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.DeviceRow
import com.xyzl.driverphone.SecurityRepository
import com.xyzl.driverphone.formatLocalDateTime
import kotlinx.coroutines.launch

/** 时间显示：统一走 formatLocalDateTime（UTC → 马来西亚 UTC+8） */
private fun prettyLogin(raw: String?): String = formatLocalDateTime(raw)

private fun deviceTypeLine(device: DeviceRow): String {
    val os = device.osName?.takeIf { it.isNotBlank() }
    val type = device.deviceType?.takeIf { it.isNotBlank() }
    return listOfNotNull(os, type).joinToString(" · ").ifBlank { "-" }
}

/** 登录设备管理页：已登录（is_active=true）/ 已退出（is_active=false） */
@Composable
fun DeviceManageScreen(userId: String? = null, onBack: () -> Unit = {}) {
    val repo = remember { SecurityRepository() }
    val scope = rememberCoroutineScope()

    var active by remember { mutableStateOf<List<DeviceRow>>(emptyList()) }
    var inactive by remember { mutableStateOf<List<DeviceRow>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf(false) }
    var revealed by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<DeviceRow?>(null) }
    var pending by remember { mutableStateOf<DeviceRow?>(null) }
    var hint by remember { mutableStateOf<String?>(null) }

    suspend fun reload() {
        val id = userId ?: return
        val all = repo.devices(id)
        active = all.filter { it.isActive }
        inactive = all.filter { !it.isActive }
        loaded = true
    }

    LaunchedEffect(userId) { reload() }

    val current = detail
    if (current != null) {
        DeviceDetailScreen(
            device = current,
            onBack = { detail = null },
            onLogout = { pending = current },
        )
        return
    }

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
        when {
            !loaded -> Text("加载中…", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            active.isEmpty() -> Text("暂无已登录设备", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            else -> active.forEach { device ->
                DeviceRowView(
                    device = device,
                    editing = editing,
                    revealed = revealed == device.id,
                    actionLabel = "退出",
                    actionColor = FormRed,
                    onClick = { if (!editing) detail = device },
                    onMinus = { revealed = if (revealed == device.id) null else device.id },
                    onAction = { pending = device },
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        FormSectionTitle("已退出的设备")
        when {
            !loaded -> Text("加载中…", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            inactive.isEmpty() -> Text("暂无已退出设备", fontSize = 13.sp, color = FormMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
            else -> inactive.forEach { device ->
                DeviceRowView(
                    device = device,
                    editing = editing,
                    revealed = revealed == device.id,
                    actionLabel = "删除",
                    actionColor = FormRed,
                    onClick = { if (!editing) detail = device },
                    onMinus = { revealed = if (revealed == device.id) null else device.id },
                    onAction = { pending = device },
                )
            }
        }

        Spacer(Modifier.height(110.dp))
    }

    val target = pending
    if (target != null) {
        val isActive = target.isActive
        AlertDialog(
            onDismissRequest = { pending = null },
            containerColor = FormCard,
            title = {
                Text(
                    if (isActive) "退出该设备" else "删除设备记录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FormInk,
                )
            },
            text = {
                Text(
                    if (isActive) "退出后，下次在该设备登录时需要进行安全验证。" else "删除后这条记录不再显示。",
                    fontSize = 14.sp,
                    color = FormMuted,
                    lineHeight = 20.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val id = target.id
                    pending = null
                    revealed = null
                    detail = null
                    scope.launch {
                        val err = if (isActive) repo.logoutDevice(id) else repo.removeDevice(id)
                        if (err == null) reload() else hint = err
                    }
                }) {
                    Text(if (isActive) "退出" else "删除", color = FormRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pending = null }) {
                    Text("取消", color = FormMuted)
                }
            },
        )
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = FormCard,
            title = { Text("提示", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(message, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = { hint = null }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

/** 设备行：编辑态左侧 - 按钮，点 - 后卡片左滑露出右侧操作按钮 */
@Composable
private fun DeviceRowView(
    device: DeviceRow,
    editing: Boolean,
    revealed: Boolean,
    actionLabel: String,
    actionColor: Color,
    onClick: () -> Unit,
    onMinus: () -> Unit,
    onAction: () -> Unit,
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
            .background(actionColor),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(88.dp)
                .fillMaxHeight()
                .clickable { onAction() },
            contentAlignment = Alignment.Center,
        ) {
            Text(actionLabel, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
                Text(deviceTypeLine(device), fontSize = 12.sp, color = FormMuted)
            }
            Text(prettyLogin(device.lastLoginAt), fontSize = 12.sp, color = FormMuted)
        }
    }
}

/** 设备详情页 */
@Composable
private fun DeviceDetailScreen(
    device: DeviceRow,
    onBack: () -> Unit,
    onLogout: () -> Unit,
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
                DetailRow("系统版本", device.osName ?: "-")
                HorizontalDivider(color = FormDivide, thickness = 1.dp)
                DetailRow("设备类型", device.deviceType ?: "-")
                HorizontalDivider(color = FormDivide, thickness = 1.dp)
                DetailRow("最后登录时间", prettyLogin(device.lastLoginAt))
                HorizontalDivider(color = FormDivide, thickness = 1.dp)
                DetailRow("状态", if (device.isActive) "已登录" else "已退出")
            }
        }

        Spacer(Modifier.height(24.dp))
        if (device.isActive) {
            FormPrimaryButton(text = "退出该设备", onClick = onLogout, danger = true)
        }
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
