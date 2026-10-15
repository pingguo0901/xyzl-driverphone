package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.NotificationBadgeStore
import com.xyzl.driverphone.NotificationItem
import com.xyzl.driverphone.NotificationRepository
import com.xyzl.driverphone.formatHomeTimestamp
import kotlinx.coroutines.launch

private val NotifCard: Color @Composable get() = LocalAppColors.current.card
private val NotifChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val NotifGreen = Color(0xFF3B82F6)
private val NotifDone = Color(0xFF22C55E)
private val NotifInk: Color @Composable get() = LocalAppColors.current.ink
private val NotifMuted: Color @Composable get() = LocalAppColors.current.muted
private val NotifDanger = Color(0xFFF43F5E)

/**
 * 通知页（《通知页面接线表》）
 * Tab：全部 / 未读 / 已读 / 官方通知　·　点开一条 = is_read=true + read_at
 * 右上角三点：全部标记已读 / 清空（软删 deleted_at）
 */
@Composable
fun NotificationsScreen(userId: String?, onBack: () -> Unit = {}) {
    val repo = remember { NotificationRepository() }
    val scope = rememberCoroutineScope()

    var tab by remember { mutableStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var selecting by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    val selected = remember { mutableStateListOf<String>() }

    LaunchedEffect(userId) {
        if (userId != null) items = repo.list(userId)
    }

    val filtered = items.filter { n ->
        when (tab) {
            1 -> n.unread
            2 -> !n.unread
            3 -> n.official
            else -> true
        }
    }
    val allSelected = filtered.isNotEmpty() && filtered.all { selected.contains(it.id) }

    fun openItem(item: NotificationItem) {
        if (selecting) {
            if (selected.contains(item.id)) selected.remove(item.id) else selected.add(item.id)
            return
        }
        if (!item.unread) return
        items = items.map { if (it.id == item.id) it.copy(isRead = true) else it }
        scope.launch { repo.markRead(item.id); NotificationBadgeStore.refresh() }
    }

    fun markSelectedRead() {
        val keys = selected.toList()
        items = items.map { if (keys.contains(it.id)) it.copy(isRead = true) else it }
        selected.clear()
        selecting = false
        scope.launch { keys.forEach { repo.markRead(it) }; NotificationBadgeStore.refresh() }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            if (selecting) {
                CircleIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "取消",
                    background = NotifChip,
                    tint = NotifInk,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    selecting = false
                    selected.clear()
                }
                Text(
                    text = "已选择 " + selected.size + " 项",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotifInk,
                    modifier = Modifier.align(Alignment.Center),
                )
                CircleIconButton(
                    icon = Icons.Filled.Check,
                    contentDescription = "完成",
                    background = NotifDone,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    selecting = false
                    selected.clear()
                }
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NotifChip)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = NotifInk,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = "通知",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotifInk,
                    modifier = Modifier.align(Alignment.Center),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NotifChip)
                        .clickable { menuOpen = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.MoreVert, "更多", tint = NotifInk, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        SegmentedTabs(
            options = listOf("全部", "未读", "已读", "官方通知"),
            selectedIndex = tab,
            onSelect = { tab = it },
        )
        Spacer(Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            Text(
                "暂无通知",
                fontSize = 14.sp,
                color = NotifMuted,
                modifier = Modifier.padding(top = 32.dp),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = if (selecting) 120.dp else 96.dp),
            ) {
                items(filtered, key = { it.id }) { item ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (selecting) {
                            val checked = selected.contains(item.id)
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(item.id) else selected.add(item.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NotifGreen,
                                    uncheckedColor = NotifMuted,
                                    checkmarkColor = Color.White,
                                ),
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            NoticeRow(item = item, onClick = { openItem(item) })
                        }
                    }
                }
            }
        }
    }

    if (selecting) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(NotifCard),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            BottomAction(
                label = if (allSelected) "取消全选" else "全选",
                icon = Icons.Filled.Check,
            ) {
                if (allSelected) {
                    filtered.forEach { selected.remove(it.id) }
                } else {
                    filtered.forEach { if (!selected.contains(it.id)) selected.add(it.id) }
                }
            }
            BottomAction(label = "标记已读", icon = Icons.Filled.DoneAll) { markSelectedRead() }
            BottomAction(label = "删除", icon = Icons.Filled.Delete, tint = NotifDanger) {
                confirmDelete = true
            }
        }
    }

    if (menuOpen) {
        Dialog(onDismissRequest = { menuOpen = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = NotifCard) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("通知操作", color = NotifInk, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            menuOpen = false
                            if (userId != null) {
                                items = items.map { it.copy(isRead = true) }
                                scope.launch { repo.markAllRead(userId); NotificationBadgeStore.refresh() }
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.DoneAll, "全部标记已读", tint = NotifGreen, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("全部标记已读", color = NotifInk, fontSize = 15.sp)
                            Text("把所有通知标成已读", color = NotifMuted, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            menuOpen = false
                            confirmClear = true
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Delete, "清空", tint = NotifDanger, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("清空", color = NotifInk, fontSize = 15.sp)
                            Text("清掉全部通知（软删除，可恢复）", color = NotifMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = NotifCard,
            title = { Text("清空通知", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = NotifInk) },
            text = { Text("确定清空全部通知吗？", fontSize = 14.sp, color = NotifMuted) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    if (userId != null) {
                        items = emptyList()
                        selected.clear()
                        scope.launch { repo.clear(userId); NotificationBadgeStore.refresh() }
                    }
                }) { Text("确定", color = NotifDanger, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("取消", color = NotifMuted) }
            },
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = NotifCard,
            title = { Text("删除通知", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = NotifInk) },
            text = { Text("确定删除选中的 " + selected.size + " 条通知吗？", fontSize = 14.sp, color = NotifMuted) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    val keys = selected.toList()
                    items = items.filterNot { keys.contains(it.id) }
                    selected.clear()
                    selecting = false
                    scope.launch { repo.delete(keys); NotificationBadgeStore.refresh() }
                }) { Text("确定", color = NotifDanger, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("取消", color = NotifMuted) }
            },
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    background: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun BottomAction(
    label: String,
    icon: ImageVector,
    tint: Color = NotifInk,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, label, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = tint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NoticeRow(item: NotificationItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NotifCard)
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(if (item.unread) NotifGreen else Color.Transparent),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotifInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.official) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(NotifGreen.copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("官方", fontSize = 10.sp, color = NotifGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(item.body, fontSize = 13.sp, color = NotifMuted, lineHeight = 19.sp)
            Spacer(Modifier.height(6.dp))
            Text(formatHomeTimestamp(item.createdAt).orEmpty(), fontSize = 12.sp, color = NotifMuted)
        }
    }
}
