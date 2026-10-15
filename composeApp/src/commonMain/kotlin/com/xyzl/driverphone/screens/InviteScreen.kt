package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.InviteInfo
import com.xyzl.driverphone.ProfileEditRepository
import com.xyzl.driverphone.SecurityRepository
import com.xyzl.driverphone.shareText

/** 邀请码类型 → 页面标题 */
private fun inviteLabel(inviteType: String): String = when (inviteType) {
    "universal" -> "我的邀请码"
    "personal_to_customer", "company_to_customer", "platform_to_customer" -> "我的邀请码"
    "company_to_driver", "platform_to_driver", "driver_to_customer" -> "我的邀请码"
    else -> "我的邀请码"
}

/**
 * 邀请码页（se_invitation_codes）
 * driver → 所属公司的 company_to_customer（1 条）
 * personal → 自己账户的 personal_to_customer + company_to_driver（2 条）
 */
@Composable
fun InviteScreen(userId: String? = null, onBack: () -> Unit = {}) {
    val clipboard = LocalClipboardManager.current
    val repo = remember { SecurityRepository() }
    val profileRepo = remember { ProfileEditRepository() }

    var invites by remember { mutableStateOf<List<InviteInfo>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val id = userId
        if (id != null) {
            val role = profileRepo.userRole(id)
            invites = repo.myInvites(id, role)
        }
        loaded = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "邀请码", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        when {
            !loaded -> Text("加载中…", fontSize = 14.sp, color = FormMuted)
            invites.isEmpty() -> Text("暂无邀请码", fontSize = 14.sp, color = FormMuted)
            else -> invites.forEachIndexed { index, invite ->
                val link = invite.shareLink.orEmpty().ifBlank { defaultShareLink(invite.code) }
                Card(
                    Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FormCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Text(inviteLabel(invite.inviteType), fontSize = 13.sp, color = FormMuted)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                invite.code,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FormInk,
                                modifier = Modifier.weight(1f),
                            )
                            PillAction(label = "复制", icon = true, enabled = invite.code.isNotBlank()) {
                                clipboard.setText(AnnotatedString(invite.code))
                                hint = "邀请码已复制"
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(link, fontSize = 12.sp, color = FormMuted)
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PillAction(label = "复制链接", icon = true, enabled = link.isNotBlank()) {
                                clipboard.setText(AnnotatedString(link))
                                hint = "链接已复制"
                            }
                            PillAction(label = "分享", icon = false, enabled = link.isNotBlank()) {
                                shareText(shareBody(link, invite.code))
                            }
                        }
                    }
                }
                if (index == invites.lastIndex) {
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        FormNote("邀请码只有一种：归属某个账号。个人/独资/企业显示自己的；司机/客服/客户显示归属公司的。客户分享给好友，好友填同一码即归入同一公司。")
        Spacer(Modifier.height(110.dp))
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

@Composable
private fun PillAction(
    label: String,
    icon: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(FormChip)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (icon) Icons.Filled.ContentCopy else Icons.Filled.Share,
            contentDescription = label,
            tint = if (enabled) FormGreen else FormMuted,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 12.sp, color = if (enabled) FormGreen else FormMuted, fontWeight = FontWeight.Medium)
    }
}

private fun defaultShareLink(code: String): String = "https://xyzl.my/app?invite=" + code

private fun shareBody(link: String, code: String): String =
    "点击以下下载链接\"" + link + "\"下载星域臻旅客户端并使用我们的平台进行下单" +
        (if (code.isBlank()) "。" else "，邀请码：\"" + code + "\"。")
