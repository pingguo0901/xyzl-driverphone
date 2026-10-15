package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xyzl.driverphone.ChatLine
import com.xyzl.driverphone.ChatRepository
import com.xyzl.driverphone.ChatThreadInfo
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.PermissionScene
import com.xyzl.driverphone.currentLocation
import com.xyzl.driverphone.formatClock
import com.xyzl.driverphone.formatDate
import com.xyzl.driverphone.loadImageBitmap
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.pickDocument
import com.xyzl.driverphone.pickImage
import com.xyzl.driverphone.pickMediaMultiple
import com.xyzl.driverphone.playVoice
import com.xyzl.driverphone.requestScenePermission
import com.xyzl.driverphone.startVoiceRecord
import com.xyzl.driverphone.stopVoiceRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BubbleMine = Color(0xFF3B82F6)
private val BubbleOther: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val TickGreen = Color(0xFF22C55E)
private val TickBlue = Color(0xFF3B82F6)

/** 聊天窗：返回 / 名称 / 三个点；底部 + 输入框 发送·语音；+ 面板；语音条；消息气泡与勾选状态 */
@Composable
fun ChatConversationScreen(
    peerName: String,
    readOnly: Boolean = false,
    onBack: () -> Unit = {},
    onOpenChatDetail: () -> Unit = {},
    onOpenPerson: (String) -> Unit = {},
    onOpenCall: (Boolean) -> Unit = {},
    userId: String? = null,
) {
    var input by remember { mutableStateOf("") }
    var panelOpen by remember { mutableStateOf(false) }
    var voiceMode by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }
    var cardSheet by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val myName = "我"
    val repo = remember { ChatRepository() }
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var thread by remember { mutableStateOf<ChatThreadInfo?>(null) }
    var seq by remember { mutableStateOf(0L) }

    fun add(
        mine: Boolean,
        senderName: String,
        body: ChatBody,
        tick: ChatTick = if (mine) ChatTick.Delivered else ChatTick.Read,
    ) {
        val parts = currentTimeParts()
        seq += 1
        messages.add(ChatMessage(seq, mine, senderName, body, parts.second, parts.first, tick))
    }

    // 进聊天窗：按对方名字找会话 → 拉真实消息 → 标记已读
    LaunchedEffect(userId, peerName) {
        if (userId == null) return@LaunchedEffect
        val opened = repo.open(userId, peerName)
        val info = opened?.first
        thread = info
        val loaded = opened?.second.orEmpty().mapIndexed { index, line ->
            line.toChatMessage(index.toLong() + 1L, peerName)
        }
        messages.clear()
        messages.addAll(loaded)
        seq = messages.size.toLong()
        if (info != null) repo.markThreadRead(info)
    }

    LaunchedEffect(recording) {
        if (recording) {
            seconds = 0
            while (true) {
                delay(1000)
                seconds += 1
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        // 顶部：返回 / 名称 / 三个点
        Row(
            Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = LocalAppColors.current.ink)
            }
            Text(
                peerName,
                modifier = Modifier.weight(1f),
                color = LocalAppColors.current.ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            if (!readOnly) {
                IconButton(onClick = onOpenChatDetail) {
                    Icon(Icons.Filled.MoreVert, "聊天详情", tint = LocalAppColors.current.ink)
                }
            }
        }

        // 消息列表
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageRow(msg) { onOpenPerson(if (msg.mine) myName else peerName) }
            }
        }

        // 底部：+ / 输入 / 发送·语音（只读模式整条隐藏）
        if (!readOnly) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                if (panelOpen || voiceMode) {
                    panelOpen = false
                    voiceMode = false
                } else {
                    panelOpen = true
                }
            }) {
                Icon(
                    if (panelOpen || voiceMode) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = if (panelOpen || voiceMode) "取消" else "更多",
                    tint = LocalAppColors.current.ink,
                )
            }

            if (voiceMode) {
                // 语音条：向左扩展
                Box(
                    Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (recording) Color(0xFF1E3A8A) else FormChip)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    requestScenePermission(PermissionScene.Voice)
                                    if (startVoiceRecord()) {
                                        recording = true
                                        tryAwaitRelease()
                                        recording = false
                                        stopVoiceRecord { path ->
                                            add(
                                                mine = true,
                                                senderName = myName,
                                                body = ChatBody.Voice(path, maxOf(1, seconds)),
                                                tick = if (path == null) ChatTick.Sending else ChatTick.Delivered,
                                            )
                                        }
                                    } else {
                                        recording = false
                                        hint = "没有麦克风权限，无法录音"
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (recording) "松开 发送 · " + seconds + "\"" else "按住 说话",
                        color = if (recording) Color.White else FormInk,
                        fontSize = 14.sp,
                    )
                }
            } else {
                Box(
                    Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(FormChip)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (input.isEmpty()) {
                        Text("发消息…", color = FormMuted, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = FormInk, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // 发送 / 语音
            if (input.isNotBlank() && !voiceMode) {
                IconButton(onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        add(true, myName, ChatBody.Text(text), ChatTick.Sent)
                        input = ""
                        val info = thread
                        if (info != null && userId != null) {
                            scope.launch { repo.sendText(info, userId, text) }
                        }
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, "发送", tint = BubbleMine)
                }
            } else {
                IconButton(onClick = {
                    voiceMode = !voiceMode
                    panelOpen = false
                }) {
                    Icon(Icons.Filled.Mic, "语音", tint = if (voiceMode) BubbleMine else FormInk)
                }
            }
        }

        // + 面板：相册 / 拍照 / 语音通话 / 视频通话 / 位置 / 名片 / 文件
        if (panelOpen) {
            Column(Modifier.fillMaxWidth().background(LocalAppColors.current.card).padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PanelItem("相册", Icons.Filled.PhotoLibrary) {
                        requestScenePermission(PermissionScene.Photos)
                        pickMediaMultiple(10) { uris ->
                            if (uris.isNotEmpty()) {
                                add(true, myName, ChatBody.Album(uris.take(10)), ChatTick.Delivered)
                            }
                        }
                    }
                    PanelItem("拍照", Icons.Filled.PhotoCamera) {
                        requestScenePermission(PermissionScene.Camera)
                        pickImage(ImageSource.Camera) { uri ->
                            add(true, myName, ChatBody.Album(listOf(uri)), ChatTick.Delivered)
                        }
                    }
                    PanelItem("语音通话", Icons.Filled.Call) { onOpenCall(false) }
                    PanelItem("视频通话", Icons.Filled.Videocam) { onOpenCall(true) }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PanelItem("位置", Icons.Filled.LocationOn) {
                        requestScenePermission(PermissionScene.Location)
                        currentLocation { coords ->
                            val label = if (coords == null) "当前位置（定位不可用）" else "我的当前位置"
                            add(
                                true, myName,
                                ChatBody.Location(label, coords),
                                if (coords == null) ChatTick.Sent else ChatTick.Delivered,
                            )
                        }
                    }
                    PanelItem("名片", Icons.Filled.ContactPage) { cardSheet = true }
                    PanelItem("文件", Icons.Filled.Description) {
                        pickDocument { uri ->
                            val name = uri.substringAfterLast('/').ifBlank { "文件" }
                            add(true, myName, ChatBody.File(name), ChatTick.Delivered)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
        }
    }

    // 名片选择
    if (cardSheet) {
        Dialog(onDismissRequest = { cardSheet = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = FormCard) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("发送名片", color = FormInk, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    CardOption("星域臻旅 · 公司名片", "XY-0001 · 星域臻旅") {
                        add(true, myName, ChatBody.Card("星域臻旅 · 公司名片", "XY-0001 · 星域臻旅"), ChatTick.Delivered)
                        cardSheet = false
                    }
                    CardOption("调度 · 陈师傅", "XY-S-0231 · 星域臻旅 · 调度中心") {
                        add(true, myName, ChatBody.Card("调度 · 陈师傅", "XY-S-0231 · 星域臻旅 · 调度中心"), ChatTick.Delivered)
                        cardSheet = false
                    }
                    CardOption("客服小星", "XY-S-0102 · 星域臻旅 · 客服中心") {
                        add(true, myName, ChatBody.Card("客服小星", "XY-S-0102 · 星域臻旅 · 客服中心"), ChatTick.Delivered)
                        cardSheet = false
                    }
                }
            }
        }
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = FormCard,
            title = { Text("提示", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(message, fontSize = 14.sp, color = FormMuted) },
            confirmButton = { TextButton(onClick = { hint = null }) { Text("确定", color = FormGreen) } },
        )
    }
}

@Composable
private fun PanelItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(FormCard),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, label, tint = FormInk, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = FormMuted, fontSize = 11.sp)
    }
}

@Composable
private fun CardOption(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(38.dp).clip(CircleShape).background(FormChip),
            contentAlignment = Alignment.Center,
        ) { Text(title.take(1), color = FormInk, fontSize = 15.sp) }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = FormInk, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = FormMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun MessageRow(msg: ChatMessage, onAvatarClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.mine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!msg.mine) {
            Avatar(msg.senderName.take(1), onClick = onAvatarClick)
            Spacer(Modifier.width(8.dp))
        } else {
            Spacer(Modifier.width(8.dp))
            TickMark(msg.tick)
            Spacer(Modifier.width(6.dp))
        }

        Box(
            Modifier
                .widthIn(max = 250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (msg.mine) BubbleMine else BubbleOther)
                .padding(11.dp),
        ) {
            MessageBody(msg)
        }

        if (msg.mine) {
            Spacer(Modifier.width(6.dp))
            Avatar("我", onClick = onAvatarClick)
        } else {
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun MessageBody(msg: ChatMessage) {
    val body = msg.body
    val fg = if (msg.mine) Color.White else FormInk
    val fgDim = if (msg.mine) Color(0xFFDBEAFE) else FormMuted
    val innerChip = if (msg.mine) Color(0x33000000) else FormChip
    when (body) {
        is ChatBody.Text -> Text(body.text, color = fg, fontSize = 14.sp, lineHeight = 20.sp)
        is ChatBody.Voice -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PlayArrow, "播放", tint = fg, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text(body.seconds.toString() + "\"", color = fg, fontSize = 14.sp)
        }
        is ChatBody.File -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Description, "文件", tint = fg, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text(body.name, color = fg, fontSize = 13.sp, maxLines = 2)
        }
        is ChatBody.Location -> Column(Modifier.clickable {
            body.coords?.let { openUrl("https://www.google.com/maps/search/?api=1&query=" + it) }
        }) {
            Box(
                Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.LocationOn, "位置", tint = TickGreen, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(body.name, color = fg, fontSize = 13.sp)
            Text(
                if (body.coords == null) "点击查看（未开启定位）" else body.coords,
                color = fgDim, fontSize = 11.sp,
            )
        }
        is ChatBody.Card -> Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(innerChip),
                contentAlignment = Alignment.Center,
            ) { Text(body.title.take(1), color = fg, fontSize = 15.sp) }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(body.title, color = fg, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(body.subtitle, color = fgDim, fontSize = 11.sp)
            }
        }
        is ChatBody.Album -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            body.uris.take(3).forEach { uri ->
                val bmp = remember(uri) { loadImageBitmap(uri) }
                if (bmp != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bmp,
                        contentDescription = "图片",
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)),
                    )
                } else {
                    Box(
                        Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(innerChip),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Filled.PhotoLibrary, "图片", tint = fg, modifier = Modifier.size(20.dp)) }
                }
            }
            if (body.uris.size > 3) {
                Text("+" + (body.uris.size - 3), color = fg, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Avatar(text: String, onClick: () -> Unit) {
    Box(
        Modifier.size(36.dp).clip(CircleShape).background(FormChip).clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) { Text(text, color = FormInk, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun TickMark(tick: ChatTick) {
    val (icon, color) = when (tick) {
        ChatTick.Sending -> Icons.Filled.Schedule to TickGreen
        ChatTick.Sent -> Icons.Filled.Done to TickGreen
        ChatTick.Delivered -> Icons.Filled.DoneAll to TickGreen
        ChatTick.Read -> Icons.Filled.DoneAll to TickBlue
    }
    Icon(icon, "状态", tint = color, modifier = Modifier.size(15.dp))
}

/** 数据库一行 → 界面气泡 */
private fun ChatLine.toChatMessage(id: Long, peerName: String): ChatMessage {
    val body = when (type) {
        "voice" -> ChatBody.Voice(voiceUrl, voiceSeconds ?: 0)
        "location" -> ChatBody.Location(
            content ?: "位置",
            if (lat != null && lng != null) lat.toString() + "," + lng.toString() else null,
        )
        "image" -> ChatBody.Album(listOfNotNull(attachmentUrl))
        "file" -> ChatBody.File(fileName ?: content ?: "文件")
        "contact_card" -> ChatBody.Card(content ?: "名片", "联系人名片")
        else -> ChatBody.Text(content.orEmpty())
    }
    val tick = when (deliveryStatus) {
        "read" -> ChatTick.Read
        "sent" -> ChatTick.Sent
        "delivered" -> ChatTick.Delivered
        else -> if (mine) ChatTick.Delivered else ChatTick.Read
    }
    return ChatMessage(
        id = id,
        mine = mine,
        senderName = if (mine) "我" else peerName,
        body = body,
        time = formatClock(createdAt),
        date = formatDate(createdAt),
        tick = tick,
    )
}
