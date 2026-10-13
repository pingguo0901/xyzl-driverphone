package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** 通话页：语音 / 视频（视频页我方画面在右上角可拖拽，点击可互换） */
@Composable
fun CallScreen(
    peerName: String,
    video: Boolean,
    onEnd: () -> Unit = {},
) {
    var seconds by remember { mutableStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(video) }
    var swapped by remember { mutableStateOf(false) }
    var selfX by remember { mutableStateOf(0f) }
    var selfY by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds += 1
        }
    }

    val timeText = (seconds / 60).toString().padStart(2, '0') + ":" + (seconds % 60).toString().padStart(2, '0')

    Box(Modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        if (video) {
            // 整个页面是对方视频（点击我方小窗可互换）
            Box(Modifier.fillMaxSize().background(Color(0xFF111827)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (swapped) "我" else peerName,
                        color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("视频画面", color = LocalAppColors.current.textMuted, fontSize = 13.sp)
                }
            }

            // 我方小窗
            Box(
                Modifier
                    .offset { IntOffset(selfX.roundToInt(), selfY.roundToInt()) }
                    .padding(top = 60.dp, end = 16.dp)
                    .size(width = 96.dp, height = 140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LocalAppColors.current.surfaceVariant)
                    .pointerInput(Unit) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            selfX += drag.x
                            selfY += drag.y
                        }
                    }
                    .clickable { swapped = !swapped },
                contentAlignment = Alignment.Center,
            ) {
                Text(if (swapped) peerName else "我", color = Color.White, fontSize = 13.sp)
            }

            Column(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 12.dp)) {
                Text(peerName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(timeText, color = LocalAppColors.current.textMuted, fontSize = 13.sp)
            }
        } else {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(top = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.size(120.dp).clip(CircleShape).background(FormChip), contentAlignment = Alignment.Center) {
                    Text(peerName.take(1), color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(18.dp))
                Text(peerName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(timeText, color = LocalAppColors.current.textMuted, fontSize = 14.sp)
            }
        }

        // 底部：静音 / 挂断 / 扬声器
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 42.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CallButton(
                icon = if (muted) Icons.Filled.MicOff else Icons.Filled.Mic,
                label = if (muted) "已静音" else "静音",
                tint = if (muted) Color.White else Color.White,
                bg = if (muted) Color(0xFFD1D5DB) else LocalAppColors.current.surfaceVariant,
            ) { muted = !muted }

            CallButton(
                icon = Icons.Filled.CallEnd,
                label = "挂断",
                tint = Color.White,
                bg = Color(0xFFF43F5E),
                size = 64.dp,
            ) { onEnd() }

            CallButton(
                icon = if (speaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                label = if (speaker) "扬声器" else "听筒",
                tint = Color.White,
                bg = if (speaker) Color(0xFF1D4ED8) else LocalAppColors.current.surfaceVariant,
            ) { speaker = !speaker }
        }
    }
}

@Composable
private fun CallButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    bg: Color,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(size).clip(CircleShape).background(bg).clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, label, tint = tint, modifier = Modifier.size(if (size > 60.dp) 28.dp else 24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = LocalAppColors.current.textMuted, fontSize = 11.sp)
    }
}
