package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ChatRepository
import com.xyzl.driverphone.pickBirthday

private val searchTypes = listOf("日期", "图片与视频", "文件", "名片", "位置", "链接")

/**
 * 查找聊天历史记录（聊天详情页 → 查找聊天内容）
 * 搜索：content 关键词；筛选：日期 / 图片与视频 / 文件 / 名片 / 位置 / 链接
 * 结果：发送人（sender_user_id → se_users.full_name）+ 内容 + 时间；点一条回聊天窗
 */
@Composable
fun ChatSearchScreen(
    peerName: String,
    onBack: () -> Unit = {},
    onOpenMessage: (Long) -> Unit = {},
    userId: String? = null,
) {
    val repo = remember { ChatRepository() }
    val all = remember { mutableStateListOf<ChatMessage>() }
    var keyword by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(-1) }
    var pickedDate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId, peerName) {
        if (userId == null) return@LaunchedEffect
        val info = repo.open(userId, peerName)?.first ?: return@LaunchedEffect
        val lines = repo.messages(info, userId)
        all.clear()
        all.addAll(lines.mapIndexed { index, line -> line.toChatMessage(index.toLong() + 1L, peerName) })
    }

    val results = all.filter { msg ->
        when {
            keyword.isNotBlank() -> bodyText(msg.body).contains(keyword, ignoreCase = true)
            type == 0 -> pickedDate != null && msg.date == pickedDate
            type == 1 -> msg.body is ChatBody.Album
            type == 2 -> msg.body is ChatBody.File
            type == 3 -> msg.body is ChatBody.Card
            type == 4 -> msg.body is ChatBody.Location
            type == 5 -> bodyText(msg.body).contains("http")
            else -> true
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) { Text("取消", color = FormMuted) }
            Box(
                Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(20.dp)).background(FormChip).padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    textStyle = androidx.compose.ui.text.TextStyle(color = FormInk, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (keyword.isEmpty()) Text("搜索", color = FormMuted, fontSize = 14.sp)
            }
            TextButton(onClick = { type = -1 }) { Text("搜索", color = FormGreen, fontWeight = FontWeight.SemiBold) }
        }

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            searchTypes.forEachIndexed { index, label ->
                val selected = type == index
                Box(
                    Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) FormGreen else FormChip)
                        .clickable {
                            if (index == 0) {
                                type = 0
                                pickBirthday { date -> pickedDate = date }
                            } else {
                                type = index
                                keyword = ""
                            }
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = if (selected) Color.White else FormInk, fontSize = 12.sp)
                }
            }
        }
        if (type == 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                if (pickedDate == null) "点击「日期」选择一天，或再点一次重新选" else "日期：" + pickedDate,
                color = FormMuted, fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        Spacer(Modifier.height(10.dp))
        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                Text("没有找到相关聊天记录", color = FormMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            ) {
                items(results, key = { it.id }) { msg ->
                    ChatRecordCard(msg, onClick = { onOpenMessage(msg.id) })
                }
            }
        }
    }
}

/** 聊天记录卡片：发送方头像 + 名称，名称下面是内容，最右边与名称同行显示日期与时间 */
@Composable
fun ChatRecordCard(msg: ChatMessage, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FormCard)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(FormChip), contentAlignment = Alignment.Center) {
            Text(msg.senderName.take(1), color = FormInk, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    msg.senderName,
                    modifier = Modifier.weight(1f),
                    color = FormInk, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                )
                Text(msg.date + " " + msg.time, color = FormMuted, fontSize = 11.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(bodyText(msg.body), color = FormMuted, fontSize = 13.sp, maxLines = 2, lineHeight = 18.sp)
        }
    }
}

/** 消息内容的可读文本（用于搜索与记录列表） */
fun bodyText(body: ChatBody): String = when (body) {
    is ChatBody.Text -> body.text
    is ChatBody.Album -> "[图片与视频] " + body.uris.size + " 个"
    is ChatBody.File -> "[文件] " + body.name
    is ChatBody.Location -> "[位置] " + body.name
    is ChatBody.Card -> "[名片] " + body.title
    is ChatBody.Voice -> "[语音] " + body.seconds + " 秒"
}
