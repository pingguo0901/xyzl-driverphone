package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.shareText

private val InviteCardColor = Color(0xFFF7F7F9)
private val InviteChipColor = Color(0xFFEFEFF3)
private val InviteInk = Color(0xFF111111)
private val InviteMuted = Color(0xFF6B7280)
private val InviteGreen = Color(0xFF3B82F6)

/** 我的邀请码 + 邀请下载链接（示例数据，待后端下发） */
private const val InviteCode = "XYZL-8821"
private const val InviteDownloadLink = "https://xyzl.my/app"

private fun inviteShareText(): String =
    "点击以下下载链接\"" + InviteDownloadLink + "\"下载星域臻旅客户端并使用我们的平台进行下单，邀请码：\"" + InviteCode + "\"。"

/** 邀请码页：我的邀请码（复制）+ 分享方式 */
@Composable
fun InviteScreen(onBack: () -> Unit = {}) {
    val clipboard = LocalClipboardManager.current
    var hint by remember { mutableStateOf<String?>(null) }
    val shareContent = inviteShareText()

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "邀请码", onBack = onBack)

        Spacer(Modifier.height(16.dp))

        // 我的邀请码 + 复制
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = InviteCardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("我的邀请码", fontSize = 13.sp, color = InviteMuted)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        InviteCode,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InviteInk,
                        modifier = Modifier.weight(1f),
                    )
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(InviteChipColor)
                            .clickable {
                                clipboard.setText(AnnotatedString(InviteCode))
                                hint = "邀请码已复制"
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "复制", tint = InviteGreen, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("复制", fontSize = 12.sp, color = InviteGreen, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        FormSectionTitle("分享方式")
        Spacer(Modifier.height(10.dp))

        // 分享方式卡片
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = InviteCardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InviteActionItem("分享", Icons.Filled.Share) { shareText(shareContent) }
                InviteActionItem("复制", Icons.Filled.ContentCopy) {
                    clipboard.setText(AnnotatedString(shareContent))
                    hint = "分享内容已复制"
                }
                InviteBrandItem("Instagram") {
                    InstagramButton(onClick = {
                        clipboard.setText(AnnotatedString(shareContent))
                        openUrl("https://www.instagram.com/")
                    }, size = 44.dp, logoSize = 24.dp)
                }
                InviteBrandItem("Facebook") {
                    FacebookButton(onClick = {
                        clipboard.setText(AnnotatedString(shareContent))
                        openUrl("https://www.facebook.com/sharer/sharer.php?u=" + InviteDownloadLink)
                    }, size = 44.dp)
                }
                InviteBrandItem("WhatsApp") {
                    WhatsAppButton(onClick = {
                        clipboard.setText(AnnotatedString(shareContent))
                        openUrl("https://wa.me/?text=" + shareContent)
                    }, size = 44.dp, logoSize = 24.dp)
                }
                InviteBrandItem("微信") {
                    WeChatButton(onClick = {
                        clipboard.setText(AnnotatedString(shareContent))
                        hint = "分享内容已复制，正在打开微信"
                        openUrl("weixin://")
                    }, size = 44.dp, logoSize = 24.dp)
                }
                InviteBrandItem("邮件") {
                    EmailButton(onClick = {
                        clipboard.setText(AnnotatedString(shareContent))
                        openUrl("mailto:?subject=" + "星域臻旅 邀请码" + "&body=" + shareContent)
                    }, size = 44.dp, logoSize = 24.dp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(shareContent, fontSize = 12.sp, color = InviteMuted, lineHeight = 18.sp)

        Spacer(Modifier.height(24.dp))
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = InviteCardColor,
            title = { Text("提示", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = InviteInk) },
            text = { Text(message, fontSize = 14.sp, color = InviteMuted) },
            confirmButton = { TextButton(onClick = { hint = null }) { Text("确定", color = InviteGreen) } },
        )
    }
}

@Composable
private fun InviteActionItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(50)).background(InviteChipColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = InviteInk, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = InviteMuted, maxLines = 1)
    }
}

@Composable
private fun InviteBrandItem(label: String, button: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        button()
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = InviteMuted, maxLines = 1)
    }
}
