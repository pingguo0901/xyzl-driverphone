package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ChatListItem
import com.xyzl.driverphone.ChatRepository
import com.xyzl.driverphone.NotificationBadgeStore
import com.xyzl.driverphone.NotificationItem
import com.xyzl.driverphone.NotificationRepository
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.formatClock
import com.xyzl.driverphone.formatDate
import com.xyzl.driverphone.loadImageBitmap
import kotlinx.coroutines.launch

private val ChatCard: Color @Composable get() = LocalAppColors.current.card
private val ChatAvatar: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val ChatGreen = Color(0xFF3B82F6)
private val ChatInk: Color @Composable get() = LocalAppColors.current.ink
private val ChatMuted: Color @Composable get() = LocalAppColors.current.muted

/**
 * 聊天 Tab（《聊天 Tab（私聊列表）接线表》）
 * 聊天：se_chat_threads（我在参与的 business/support/direct）· 官方通知：thread_type = 'official'
 * 名字/头像：se_users（对方）；官方用 se_accounts.display_name
 * 最后一条/时间：se_chat_threads.last_message / last_message_at
 * 未读：该会话消息表里别人发的 is_read = false 条数
 * 排序：is_pinned desc, last_message_at desc
 */
@Composable
fun ChatScreen(
    userId: String? = null,
    onOpenNotifications: () -> Unit = {},
    onOpenChat: (String, String) -> Unit = { _, _ -> },
    onOpenOfficial: (String, String) -> Unit = { _, _ -> },
) {
    var tab by remember { mutableStateOf(0) }
    val repo = remember { ChatRepository() }
    val notifRepo = remember { NotificationRepository() }
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<ChatListItem>>(emptyList()) }
    var notices by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    LaunchedEffect(userId) {
        items = if (userId == null) emptyList() else repo.list(userId)
        // 官方通知：se_notifications where type='official' order by created_at desc
        notices = if (userId == null) emptyList() else notifRepo.official(userId)
    }

    fun openNotice(item: NotificationItem) {
        if (!item.unread) return
        notices = notices.map { if (it.id == item.id) it.copy(isRead = true) else it }
        scope.launch {
            notifRepo.markRead(item.id)
            NotificationBadgeStore.refresh()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        ScreenHeader(title = LocalStrings.current.tabChat, onOpenNotifications = onOpenNotifications)
        Spacer(Modifier.height(12.dp))
        SegmentedTabs(
            options = listOf("聊天", "官方通知"),
            selectedIndex = tab,
            onSelect = { tab = it },
        )
        Spacer(Modifier.height(14.dp))

        if (tab == 0) {
            val list = items.filter { !it.official }
            if (list.isEmpty()) {
                EmptyHint("还没有聊天会话")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(list, key = { it.threadId }) { item ->
                        ChatRow(item, onClick = { onOpenChat(item.threadId, item.name) })
                    }
                }
            }
        } else {
            if (notices.isEmpty()) {
                EmptyHint("还没有官方通知")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(notices, key = { it.id }) { item ->
                        NoticeRow(item, onClick = { openNotice(item) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRow(item: ChatListItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ChatCard)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val bitmap = remember(item.avatarUrl) { item.avatarUrl?.let { loadImageBitmap(it) } }
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(ChatAvatar),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(46.dp).clip(CircleShape),
                )
            } else {
                Text(
                    item.name.take(1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChatInk,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChatInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (item.isPinned) {
                    Spacer(Modifier.width(6.dp))
                    Text(LocalStrings.current.s0633, fontSize = 10.sp, color = ChatGreen)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.lastMessage?.takeIf { it.isNotBlank() } ?: LocalStrings.current.s0510,
                fontSize = 13.sp,
                color = ChatMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            val clock = formatClock(item.lastMessageAt)
            Text(
                if (clock.isNotBlank()) clock else formatDate(item.lastMessageAt),
                fontSize = 12.sp,
                color = ChatMuted,
            )
            if (item.unread > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .widthIn(min = 28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ChatGreen)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (item.unread > 99) "99+" else item.unread.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
        Text(text, color = ChatMuted, fontSize = 13.sp)
    }
}

/** 官方通知一行：头像（logo_url / 首字）+ 名字 + 子标签 + 标题内容 + 时间 + 未读 */
@Composable
private fun NoticeRow(item: NotificationItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ChatCard)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val bitmap = remember(item.avatarUrl) { item.avatarUrl?.let { loadImageBitmap(it) } }
        val name = item.senderName ?: "官方通知"
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(ChatAvatar),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(46.dp).clip(CircleShape),
                )
            } else {
                Text(name.take(1), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ChatInk)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChatInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                val category = item.noticeCategory
                if (!category.isNullOrBlank()) {
                    Spacer(Modifier.width(6.dp))
                    Text("【" + category + "】", fontSize = 11.sp, color = ChatGreen)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.title, fontSize = 14.sp, color = ChatInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (item.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(item.body, fontSize = 13.sp, color = ChatMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            val clock = formatClock(item.createdAt)
            Text(if (clock.isNotBlank()) clock else formatDate(item.createdAt), fontSize = 12.sp, color = ChatMuted)
            if (item.unread) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .widthIn(min = 18.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ChatGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("1", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}
