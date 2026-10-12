package com.xyzl.driverphone.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.xyzl.driverphone.WeChatLogo
import com.xyzl.driverphone.WhatsAppLogo
import com.xyzl.driverphone.decodeQrFromImage
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.pickImage
import com.xyzl.driverphone.setTorchEnabled
import com.xyzl.driverphone.shareText

/** 司机的邀请链接（等后端下发真实链接后替换） */
private const val InviteLink = "https://xyzl.my/d/DRV8821"
private const val DriverName = "陈师傅"

/** 25x25 二维码模块矩阵（纠错等级 M，已实测可扫出上面的链接） */
private val QrMatrix = listOf(
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

/** 二维码页：分享个人二维码 / 扫描二维码 */
@Composable
fun QrCodeScreen(onBack: () -> Unit = {}) {
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
        if (tab == 0) ShareQrTab() else ScanQrTab()
    }
}

@Composable
private fun ShareQrTab() {
    val clipboard = LocalClipboardManager.current
    var hint by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
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
                    Text(DriverName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
                    Spacer(Modifier.height(12.dp))
                    QrCanvas(
                        Modifier
                            .fillMaxWidth(0.66f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
            Box(
                Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(FormGreen)
                    .border(3.dp, FormCard, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("陈", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
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
                    shareText(InviteLink)
                }
                ShareActionItem(label = "复制", icon = Icons.Filled.ContentCopy) {
                    clipboard.setText(AnnotatedString(InviteLink))
                    hint = "链接已复制"
                }
                ShareActionItem(label = "Instagram", glyph = "IG") {
                    clipboard.setText(AnnotatedString(InviteLink))
                    hint = "链接已复制，正在打开 Instagram"
                    openUrl("https://www.instagram.com/")
                }
                ShareActionItem(label = "Facebook", glyph = "f") {
                    clipboard.setText(AnnotatedString(InviteLink))
                    openUrl("https://www.facebook.com/sharer/sharer.php?u=" + InviteLink)
                }
                ShareActionItem(label = "WhatsApp", icon = WhatsAppLogo) {
                    clipboard.setText(AnnotatedString(InviteLink))
                    openUrl("https://wa.me/?text=" + InviteLink)
                }
                ShareActionItem(label = "微信", icon = WeChatLogo) {
                    clipboard.setText(AnnotatedString(InviteLink))
                    hint = "链接已复制，正在打开微信"
                    openUrl("weixin://")
                }
                ShareActionItem(label = "邮件", icon = Icons.Filled.Mail) {
                    clipboard.setText(AnnotatedString(InviteLink))
                    openUrl("mailto:?subject=" + "星域臻旅 司机邀请" + "&body=" + InviteLink)
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

@Composable
private fun QrCanvas(modifier: Modifier = Modifier) {
    val modules = QrMatrix.size
    Canvas(modifier) {
        val cell = this.size.minDimension / modules
        val originX = (this.size.width - cell * modules) / 2f
        val originY = (this.size.height - cell * modules) / 2f
        drawRect(Color.White, topLeft = Offset.Zero, size = this.size)
        for (y in 0 until modules) {
            for (x in 0 until modules) {
                if (QrMatrix[y][x] == '1') {
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
private fun ScanQrTab() {
    var torchOn by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf<String?>(null) }
    var notFound by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { setTorchEnabled(false) }
    }

    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF101713)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.fillMaxWidth(0.72f).aspectRatio(1f)) {
                Canvas(Modifier.fillMaxSize()) {
                    val len = size.minDimension * 0.18f
                    val stroke = 4.dp.toPx()
                    val c = Color(0xFF7FE3B8)
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
                "将二维码放入框内",
                fontSize = 12.sp,
                color = Color(0xFF9DB0A8),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
            )
        }

        Spacer(Modifier.height(40.dp))
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
                    val content = decodeQrFromImage(uri)
                    if (content != null) scanned = content else notFound = true
                }
            }
        }
    }

    val result = scanned
    if (result != null) {
        AlertDialog(
            onDismissRequest = { scanned = null },
            containerColor = FormCard,
            title = { Text("扫描结果", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(result, fontSize = 14.sp, color = FormInk, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = {
                    val url = result
                    scanned = null
                    if (url.startsWith("http")) openUrl(url)
                }) {
                    Text("打开", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { scanned = null }) { Text("关闭", color = FormMuted) }
            },
        )
    }

    if (notFound) {
        AlertDialog(
            onDismissRequest = { notFound = false },
            containerColor = FormCard,
            title = { Text("未识别到二维码", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("这张图片里没有找到二维码，换一张再试试。", fontSize = 14.sp, color = FormMuted, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { notFound = false }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
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
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (active) FormGreen else FormChip),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = FormInk, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, color = FormMuted)
    }
}
