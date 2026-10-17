package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.WalletQrInfo
import com.xyzl.driverphone.WalletRepository
import com.xyzl.driverphone.decodeQrFromImage
import com.xyzl.driverphone.openUrl
import com.xyzl.driverphone.pickImage
import com.xyzl.driverphone.qrMatrixFor
import com.xyzl.driverphone.saveQrToGallery
import com.xyzl.driverphone.setTorchEnabled
import kotlinx.coroutines.delay

private val ScanCard: Color @Composable get() = LocalAppColors.current.card
private val ScanChip: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val ScanInk: Color @Composable get() = LocalAppColors.current.ink
private val ScanMuted: Color @Composable get() = LocalAppColors.current.muted
private val ScanGreen = Color(0xFF3B82F6)

/** 25x25 二维码模块矩阵（与二维码页一致的示例矩阵） */
private val WalletQrMatrix = listOf(
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

/** 钱包扫码页：扫码 / 收款 左右选项 */
@Composable
fun WalletScanTab(userId: String? = null) {
    var mode by remember { mutableStateOf(0) } // 0 扫码, 1 收款
    var qr by remember { mutableStateOf<WalletQrInfo?>(null) }
    LaunchedEffect(userId) {
        if (userId != null) qr = WalletRepository().qrInfo(userId)
    }
    var torchOn by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf<String?>(null) }
    var notFound by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { setTorchEnabled(false) }
    }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            if (mode == 0) {
                ScanFrameView()
            } else {
                ReceiveQrView(qr)
            }
        }

        // 相册 / 闪光灯（胶囊上方）
        Row(
            Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ScanRoundAction(
                label = LocalStrings.current.s0070,
                icon = Icons.Filled.PhotoLibrary,
                active = false,
            ) {
                pickImage(ImageSource.Gallery) { uri ->
                    val content = decodeQrFromImage(uri)
                    if (content != null) scanned = content else notFound = true
                }
            }
            ScanRoundAction(
                label = if (torchOn) LocalStrings.current.s0114 else LocalStrings.current.s0194,
                icon = if (torchOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                active = torchOn,
            ) {
                torchOn = !torchOn
                setTorchEnabled(torchOn)
            }
        }

        Spacer(Modifier.height(14.dp))
        SegmentedTabs(
            options = listOf("扫码", "收款"),
            selectedIndex = mode,
            onSelect = { mode = it },
        )
        Spacer(Modifier.height(6.dp))
    }

    val result = scanned
    if (result != null) {
        AlertDialog(
            onDismissRequest = { scanned = null },
            containerColor = ScanCard,
            title = { Text(LocalStrings.current.s0136, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ScanInk) },
            text = { Text(result, fontSize = 14.sp, color = ScanInk) },
            confirmButton = {
                TextButton(onClick = {
                    val url = result
                    scanned = null
                    if (url.startsWith("http")) openUrl(url)
                }) {
                    Text(LocalStrings.current.s0446, color = ScanGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { scanned = null }) { Text(LocalStrings.current.s0304, color = ScanMuted) }
            },
        )
    }

    if (notFound) {
        AlertDialog(
            onDismissRequest = { notFound = false },
            containerColor = ScanCard,
            title = { Text(LocalStrings.current.s0538, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ScanInk) },
            text = { Text(LocalStrings.current.s0720, fontSize = 14.sp, color = ScanMuted) },
            confirmButton = {
                TextButton(onClick = { notFound = false }) { Text(LocalStrings.current.confirm, color = ScanGreen) }
            },
        )
    }
}

@Composable
private fun ScanFrameView() {
    Box(
        Modifier
            .fillMaxSize()
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
            LocalStrings.current.s0126,
            fontSize = 12.sp,
            color = ScanMuted,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
        )
    }
}

/** 收款：展示二维码 + 下载二维码 / 输入特定金额（金额二维码 60 秒有效） */
@Composable
private fun ReceiveQrView(qr: WalletQrInfo?) {
    val WalletDriverName = qr?.name ?: "我"
    val PayQrBase = qr?.token ?: "https://xyzl.my/pay"
    var amount by remember { mutableStateOf<String?>(null) }
    var currency by remember { mutableStateOf("MYR") }
    var showAmountDialog by remember { mutableStateOf(false) }
    var dialogCurrency by remember { mutableStateOf("MYR") }
    var dialogAmount by remember { mutableStateOf("") }
    var remaining by remember { mutableStateOf(0) }
    var hint by remember { mutableStateOf<String?>(null) }

    val payload = if (amount != null) "$PayQrBase?amount=$amount&cur=$currency" else PayQrBase
    val matrix = remember(payload) { qrMatrixFor(payload) ?: WalletQrMatrix }

    // 金额二维码有效时间 60 秒，过时自动返回上一页（退出金额模式）
    LaunchedEffect(amount) {
        if (amount != null) {
            remaining = 60
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
            }
            amount = null
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ScanCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(WalletDriverName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ScanInk)
                    Spacer(Modifier.height(14.dp))
                    Box(Modifier.size(200.dp).clip(RoundedCornerShape(12.dp))) {
                        WalletQrCanvas(matrix, Modifier.fillMaxSize())
                    }
                }
            }

            if (amount != null) {
                Spacer(Modifier.height(14.dp))
                Text("$currency $amount", fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = ScanGreen)
                Spacer(Modifier.height(6.dp))
                Text(LocalStrings.current.s0661.fill(remaining), fontSize = 12.sp, color = ScanMuted)
            }

            Spacer(Modifier.height(18.dp))
            // 二维码下方胶囊：下载二维码 / 输入特定金额
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ScanChip),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.weight(1f).fillMaxSize().clickable {
                        val ok = saveQrToGallery(payload)
                        hint = if (ok) LocalStrings.current.s0251 else LocalStrings.current.s0290
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(LocalStrings.current.s0236, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ScanGreen)
                }
                Box(Modifier.size(1.dp, 22.dp).background(ScanMuted.copy(alpha = 0.3f)))
                Box(
                    Modifier.weight(1f).fillMaxSize().clickable { showAmountDialog = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(LocalStrings.current.s0185, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ScanGreen)
                }
            }
        }
    }

    if (showAmountDialog) {
        AlertDialog(
            onDismissRequest = { showAmountDialog = false },
            containerColor = ScanCard,
            title = { Text(LocalStrings.current.s0185, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ScanInk) },
            text = {
                Column {
                    Text(LocalStrings.current.s0076, fontSize = 13.sp, color = ScanMuted)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("SGD", "MYR").forEach { cur ->
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (dialogCurrency == cur) ScanGreen else ScanChip)
                                    .clickable { dialogCurrency = cur }
                                    .padding(horizontal = 20.dp, vertical = 9.dp),
                            ) {
                                Text(cur, fontSize = 13.sp, color = if (dialogCurrency == cur) Color.White else ScanMuted)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    FormTextField(
                        label = LocalStrings.current.s0042,
                        value = dialogAmount,
                        placeholder = LocalStrings.current.s0179,
                        onValueChange = { dialogAmount = it },
                        keyboardNumber = true,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (dialogAmount.isNotBlank()) {
                            currency = dialogCurrency
                            amount = dialogAmount
                        }
                        showAmountDialog = false
                        dialogAmount = ""
                    },
                ) {
                    Text(LocalStrings.current.confirm, color = ScanGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAmountDialog = false }) { Text("取消", color = ScanMuted) }
            },
        )
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = ScanCard,
            title = { Text(LocalStrings.current.s0002, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ScanInk) },
            text = { Text(message, fontSize = 14.sp, color = ScanMuted) },
            confirmButton = { TextButton(onClick = { hint = null }) { Text(LocalStrings.current.confirm, color = ScanGreen) } },
        )
    }
}

@Composable
private fun WalletQrCanvas(matrix: List<String>, modifier: Modifier = Modifier) {
    val modules = matrix.size
    Canvas(modifier) {
        val cell = size.minDimension / modules
        val originX = (size.width - cell * modules) / 2f
        val originY = (size.height - cell * modules) / 2f
        drawRect(Color.White, topLeft = Offset.Zero, size = size)
        for (y in 0 until modules) {
            for (x in 0 until modules) {
                if (matrix[y][x] == '1') {
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
private fun ScanRoundAction(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (active) ScanGreen else ScanChip),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = ScanInk, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = ScanMuted)
    }
}
