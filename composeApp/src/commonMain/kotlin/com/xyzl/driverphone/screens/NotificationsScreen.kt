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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NotifCard = Color(0xFF1C1C1E)
private val NotifChip = Color(0xFF27272A)
private val NotifGreen = Color(0xFF3B82F6)
private val NotifDone = Color(0xFF22C55E)
private val NotifInk = Color(0xFFFFFFFF)
private val NotifMuted = Color(0xFFA1A1AA)

private data class NoticeItem(
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean,
    val official: Boolean,
)

private val notices = listOf(
    NoticeItem("新订单已派单", "T001 吉隆坡国际机场 → 武吉免登，请在 15 分钟内确认", "16:20", true, true),
    NoticeItem("薪资已到账", "本周薪资 RM 486.00 已到账，可在财务页查看流水", "10:22", true, true),
    NoticeItem("实名认证通过", "你的 KYC 认证已通过，可以正常接单", "昨天", false, true),
    NoticeItem("报销已受理", "加油报销 RM 120.00 已受理，3 个工作日内完成审核", "昨天", true, false),
    NoticeItem("服务分更新", "当前服务分 4.9，继续保持", "10-10", false, false),
)

/** 通知页：返回 / 标题 / 右上角三个点；支持多选（全选·标记已读·删除） */
@Composable
fun NotificationsScreen(onBack: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var selecting by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val items = remember { mutableStateListOf<NoticeItem>().also { it.addAll(notices) } }
    val selected = remember { mutableStateListOf<String>() }

    fun keyOf(item: NoticeItem) = item.title + "|" + item.time

    val filtered = items.filter { n ->
        when (tab) {
            1 -> n.unread
            2 -> !n.unread
            3 -> n.official
            else -> true
        }
    }

    val allSelected = filtered.isNotEmpty() && filtered.all { selected.contains(keyOf(it)) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            if (selecting) {
                // 左上角：打叉取消
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
                // 右上角：绿色打勾完成
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
                // 右上角三个点
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
                items(filtered, key = { keyOf(it) }) { item ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // 选择模式：卡片右移，左边出现勾选框
                        if (selecting) {
                            val checked = selected.contains(keyOf(item))
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(keyOf(item)) else selected.add(keyOf(item))
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NotifGreen,
                                    uncheckedColor = NotifMuted,
                                    checkmarkColor = Color.White,
                                ),
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            NoticeRow(
                                item = item,
                                onClick = {
                                    if (selecting) {
                                        if (selected.contains(keyOf(item))) selected.remove(keyOf(item))
                                        else selected.add(keyOf(item))
                                    } else {
                                        val idx = items.indexOfFirst { keyOf(it) == keyOf(item) }
                                        if (idx >= 0) items[idx] = items[idx].copy(unread = false)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // 选中后底部：全选 / 标记已读 / 删除
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
                    filtered.forEach { selected.remove(keyOf(it)) }
                } else {
                    filtered.forEach { if (!selected.contains(keyOf(it))) selected.add(keyOf(it)) }
                }
            }
            BottomAction(label = "标记已读", icon = Icons.Filled.DoneAll) {
                for (i in items.indices) {
                    if (selected.contains(keyOf(items[i]))) items[i] = items[i].copy(unread = false)
                }
                selected.clear()
                selecting = false
            }
            BottomAction(label = "删除", icon = Icons.Filled.Delete, tint = Color(0xFFF43F5E)) {
                confirmDelete = true
            }
        }
    }

    // 三个点菜单
    if (menuOpen) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { menuOpen = false }) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(20.dp),
                color = NotifCard,
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("标记全部已读", color = NotifInk, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            menuOpen = false
                            for (i in items.indices) items[i] = items[i].copy(unread = false)
                            selecting = true
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.DoneAll, "标记全部已读", tint = NotifGreen, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("标记全部已读", color = NotifInk, fontSize = 15.sp)
                            Text("进入多选，可继续全选 / 标记已读 / 删除", color = NotifMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = NotifCard,
            title = { Text("删除通知", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = NotifInk) },
            text = { Text("确定删除选中的 " + selected.size + " 条通知吗？", fontSize = 14.sp, color = NotifMuted) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    confirmDelete = false
                    val keys = selected.toList()
                    items.removeAll { keys.contains(keyOf(it)) }
                    selected.clear()
                    selecting = false
                }) { Text("确定", color = Color(0xFFF43F5E), fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { confirmDelete = false }) {
                    Text("取消", color = NotifMuted)
                }
            },
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
private fun NoticeRow(item: NoticeItem, onClick: () -> Unit) {
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
            Text(item.time, fontSize = 12.sp, color = NotifMuted)
        }
    }
}
