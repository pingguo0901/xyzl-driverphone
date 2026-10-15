package com.xyzl.driverphone.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.LocalAppColors
import com.xyzl.driverphone.ProfileEditRepository
import com.xyzl.driverphone.ScannedUser
import com.xyzl.driverphone.WeChatLogo
import com.xyzl.driverphone.WhatsAppLogo
import com.xyzl.driverphone.decodeQrFromImage
import com.xyzl.driverphone.loadRemoteImageBitmap
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.pickImage
import com.xyzl.driverphone.qrMatrixFor
import com.xyzl.driverphone.setTorchEnabled
import com.xyzl.driverphone.shareText
import kotlinx.coroutines.launch

/** 二维码页：分享个人二维码（se_user_qr_codes）+ 扫描二维码（加好友） */
@Composable
fun QrCodeScreen(userId: String? = null, onBack: () -> Unit = {}) {
    var tab by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "二维码", onBack = onBack)
        Spacer(Modifier.height(14.dp))
        SegmentedTabs(
            options = listOf("分享个人二维码", "扫描二维码"),
            selectedIndex = tab,
            onSelect = { tab = it },
        )
        Spacer(Modifier.height(20.dp))
        if (tab == 0) ShareQrTab(userId, Modifier.weight(1f)) else ScanQrTab(userId, Modifier.weight(1f))
    }
}

@Composable
private fun ShareQrTab(userId: String?, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    val repo = remember { ProfileEditRepository() }

    var name by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var avatar by remember { mutableStateOf<ImageBitmap?>(null) }
    var content by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val id = userId ?: return@LaunchedEffect
        val qr = repo.myQr(id)
        content = qr.content
        val info = repo.myInfo(id)
        name = info?.fullName.orEmpty()
        avatarUrl = info?.avatarUrl
    }

    LaunchedEffect(avatarUrl) {
        val url = avatarUrl ?: return@LaunchedEffect
        avatar = loadRemoteImageBitmap(url)
    }

    if (content.isBlank()) content = ""

    Column(
        modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = FormCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    Modifier.fillMaxSize().padding(top = 46.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    QrCodeView(
                        text = content,
                        modifier = Modifier
                            .fillMaxWidth(0.66f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
            AvatarCircle(avatar, name, size = 68.dp, fontSize = 24.sp, borderWidth = 3.dp)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            if (name.isBlank()) "司机" else name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = FormInk,
        )
        if (content.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(content, fontSize = 10.sp, color = FormMuted, maxLines = 1)
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = buildAnnotatedString {
                append("分享你的二维码后，他人可以将你添加为联系人。分享你的链接后，他人可以开始与你聊天。")
                withStyle(SpanStyle(color = FormGreen)) { append("了解更多") }
            },
            fontSize = 12.sp,
            color = FormMuted,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(22.dp))
        Box(Modifier.fillMaxWidth()) { FormSectionTitle("链接分享方式") }
        Spacer(Modifier.height(10.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShareActionItem(label = "分享", icon = Icons.Filled.Share) {
                    if (content.isNotBlank()) shareText(content) else hint = "二维码还没准备好"
                }
                ShareActionItem(label = "复制", icon = Icons.Filled.ContentCopy) {
                    if (content.isNotBlank()) {
                        clipboard.setText(AnnotatedString(content))
                        hint = "链接已复制"
                    } else {
                        hint = "二维码还没准备好"
                    }
                }
                ShareBrandItem(label = "Instagram") {
                    InstagramButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            hint = "链接已复制，正在打开 Instagram"
                            openUrl("https://www.instagram.com/")
                        },
                        size = 44.dp,
                        logoSize = 24.dp,
                    )
                }
                ShareBrandItem(label = "Facebook") {
                    FacebookButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            openUrl("https://www.facebook.com/sharer/sharer.php?u=" + content)
                        },
                        size = 44.dp,
                    )
                }
                ShareBrandItem(label = "WhatsApp") {
                    WhatsAppButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            openUrl("https://wa.me/?text=" + content)
                        },
                        size = 44.dp,
                        logoSize = 24.dp,
                    )
                }
                ShareBrandItem(label = "微信") {
                    WeChatButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            hint = "链接已复制，正在打开微信"
                            openUrl("weixin://")
                        },
                        size = 44.dp,
                        logoSize = 24.dp,
                    )
                }
                ShareBrandItem(label = "邮件") {
                    EmailButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            openUrl("mailto:?subject=" + "星域臻旅 我的二维码" + "&body=" + content)
                        },
                        size = 44.dp,
                        logoSize = 24.dp,
                    )
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = FormCard,
            title = { Text("提示", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(message, fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { hint = null }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

/** 头像：有图显示图，没图显示名字首字 */
@Composable
private fun AvatarCircle(
    avatar: ImageBitmap?,
    name: String,
    size: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    borderWidth: androidx.compose.ui.unit.Dp,
) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(FormGreen)
            .border(borderWidth, FormCard, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (avatar != null) {
            Image(
                bitmap = avatar,
                contentDescription = "头像",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                name.trim().take(1).ifBlank { "我" },
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** 二维码：优先用真实内容生成，生成不了退回示例矩阵 */
@Composable
private fun QrCodeView(text: String, modifier: Modifier = Modifier) {
    val matrix = remember(text) { if (text.isBlank()) null else qrMatrixFor(text) }
    QrCanvas(matrix ?: SampleQrMatrix, modifier)
}

/** 扫描二维码 Tab：相册识别 + 加好友 */
@Composable
private fun ScanQrTab(userId: String?, modifier: Modifier = Modifier) {
    val repo = remember { ProfileEditRepository() }
    val scope = rememberCoroutineScope()

    var torchOn by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf<ScannedUser?>(null) }
    var hint by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { setTorchEnabled(false) }
    }

    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(LocalAppColors.current.background),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.fillMaxWidth(0.72f).aspectRatio(1f)) {
                Canvas(Modifier.fillMaxSize()) {
                    val len = size.minDimension * 0.18f
                    val stroke = 4.dp.toPx()
                    val c = Color(0xFF10B981)
                    drawLine(c, Offset(0f, 0f), Offset(len, 0f), stroke)
                    drawLine(c, Offset(0f, 0f), Offset(0f, len), stroke)
                    drawLine(c, Offset(size.width, 0f), Offset(size.width - len, 0f), stroke)
                    drawLine(c, Offset(size.width, 0f), Offset(size.width, len), stroke)
                    drawLine(c, Offset(0f, size.height), Offset(len, size.height), stroke)
                    drawLine(c, Offset(0f, size.height), Offset(0f, size.height - len), stroke)
                    drawLine(c, Offset(size.width, size.height), Offset(size.width - len, size.height), stroke)
                    drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - len), stroke)
                }
            }
            Text(
                if (loading) "正在识别…" else "将二维码放入框内",
                fontSize = 12.sp,
                color = LocalAppColors.current.muted,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScanActionItem(
                label = if (torchOn) "关闭闪光灯" else "闪光灯",
                icon = if (torchOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                active = torchOn,
            ) {
                torchOn = !torchOn
                setTorchEnabled(torchOn)
            }
            ScanActionItem(label = "相册", icon = Icons.Filled.PhotoLibrary, active = false) {
                pickImage(ImageSource.Gallery) { uri ->
                    val raw = decodeQrFromImage(uri)
                    if (raw == null) {
                        hint = "没有识别到二维码，换一张试试"
                    } else {
                        loading = true
                        scope.launch {
                            val hit = repo.resolveScan(raw)
                            loading = false
                            if (hit == null) hint = "这不是星域臻旅的二维码" else scanned = hit
                        }
                    }
                }
            }
        }
    }

    val hit = scanned
    if (hit != null) {
        AlertDialog(
            onDismissRequest = { scanned = null },
            containerColor = FormCard,
            title = { Text("扫描结果", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScannedAvatar(hit.avatarUrl, hit.name)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(hit.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                        Text("扫码添加为好友", fontSize = 12.sp, color = FormMuted)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val me = userId
                    if (me == null) {
                        scanned = null
                        hint = "请先登录"
                    } else {
                        scope.launch {
                            val accountId = repo.accountId(me)
                            val err = repo.addFriend(me, accountId, hit.userId)
                            scanned = null
                            hint = err ?: "好友申请已发送"
                        }
                    }
                }) {
                    Text("加好友", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { scanned = null }) {
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
            text = { Text(message, fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { hint = null }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

@Composable
private fun ScannedAvatar(url: String?, name: String) {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        bitmap = url?.let { loadRemoteImageBitmap(it) }
    }
    Box(
        Modifier.size(48.dp).clip(CircleShape).background(FormChip),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(bitmap!!, contentDescription = "头像", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(name.trim().take(1).ifBlank { "用" }, color = FormInk, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 二维码绘制 */
@Composable
private fun QrCanvas(matrix: List<String>, modifier: Modifier = Modifier) {
    val modules = matrix.size
    Canvas(modifier) {
        if (modules == 0) return@Canvas
        val cell = this.size.minDimension / modules
        val originX = (this.size.width - cell * modules) / 2f
        val originY = (this.size.height - cell * modules) / 2f
        drawRect(Color.White, topLeft = Offset.Zero, size = this.size)
        for (y in 0 until modules) {
            val row = matrix[y]
            for (x in row.indices) {
                if (row[x] == '1') {
                    drawRect(
                        Color.Black,
                        topLeft = Offset(originX + x * cell, originY + y * cell),
                        size = Size(cell + 0.6f, cell + 0.6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareBrandItem(label: String, button: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        button()
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 9.sp, color = FormMuted, maxLines = 1)
    }
}

@Composable
private fun ShareActionItem(
    label: String,
    icon: ImageVector? = null,
    glyph: String? = null,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() },
    ) {
        Box(
            Modifier.size(38.dp).clip(CircleShape).background(FormChip),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = label, tint = FormInk, modifier = Modifier.size(19.dp))
            } else {
                Text(
                    glyph ?: label.take(1),
                    color = FormInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 9.sp, color = FormMuted, maxLines = 1)
    }
}

@Composable
private fun ScanActionItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            Modifier.size(52.dp).clip(CircleShape).background(if (active) FormGreen else FormChip),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = if (active) Color.White else FormInk, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = FormMuted)
    }
}

/** 兜底示例矩阵（真实二维码生成失败时显示） */
private val SampleQrMatrix = listOf(
    "1111111001111000001111111",
    "1000001000100001001000001",
    "1011101011101001101011101",
    "1011101010111011001011101",
    "1011101011110000101011101",
    "1000001011011111101000001",
    "1111111010101010101111111",
    "0000000010110110100000000",
    "1011111001001000001111100",
    "1101110100111110010100010",
    "1011111100101011100001011",
    "1100010000111101011010001",
    "1001111011101000111110111",
    "1101000101100110110101010",
    "1001101011111101100111011",
    "1000110010010111100110001",
    "1011111101111010111110100",
    "0000000010101001100011000",
    "1111111001101010101010111",
    "1000001011001110100011011",
    "1011101011001011111110101",
    "1011101011101001111011111",
    "1011101011010000100001101",
    "1000001000000001110111001",
    "1111111010011101001111111",
)
