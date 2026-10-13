package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/** 设置字体大小页：虚拟聊天预览 + 7 档滑块 */
@Composable
fun FontSizeScreen(onBack: () -> Unit = {}) {
    var level by remember { mutableStateOf(DisplayPrefs.fontSizeLevel) }
    val previewSize: TextUnit = (12 + level * 2).sp

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderCancelDone(
            title = "字体大小",
            onCancel = onBack,
            onDone = {
                DisplayPrefs.fontSizeLevel = level
                onBack()
            },
        )
        Spacer(Modifier.height(16.dp))

        FontPreview(fontSize = previewSize, modifier = Modifier.weight(1f))

        Spacer(Modifier.height(14.dp))
        FormNote("拖动下面的滑块，可设置字体大小。设置后，会改变聊天和应用全部的字体大小。如果在使用过程中存在问题或意见，可反馈给官方团队。")

        // 字体大小档位：固定在最底部
        Spacer(Modifier.height(14.dp))
        Slider(
            value = level.toFloat(),
            onValueChange = { level = it.roundToInt().coerceIn(1, 7) },
            valueRange = 1f..7f,
            steps = 5,
            colors = SliderDefaults.colors(
                thumbColor = FormGreen,
                activeTrackColor = FormGreen,
                inactiveTrackColor = FormChip,
            ),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("A", fontSize = 12.sp, color = FormMuted)
            Text(level.toString() + " / 7 档", fontSize = 12.sp, color = FormMuted, fontWeight = FontWeight.SemiBold)
            Text("A", fontSize = 20.sp, color = FormInk)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FontPreview(fontSize: TextUnit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FormCard)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 我方
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                Modifier
                    .widthIn(max = 230.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FormGreen)
                    .padding(10.dp),
            ) {
                Text("预览字体大小", color = Color.White, fontSize = fontSize, lineHeight = fontSize * 1.4f)
            }
            Spacer(Modifier.width(8.dp))
            Avatar("我", FormGreen)
        }
        // 对方
        Row(Modifier.fillMaxWidth()) {
            Avatar("对", FormChip, FormInk)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .widthIn(max = 230.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FormChip)
                    .padding(10.dp),
            ) {
                Text("拖动下面的滑块，可设置字体大小", color = FormInk, fontSize = fontSize, lineHeight = fontSize * 1.4f)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Avatar("对", FormChip, FormInk)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .widthIn(max = 230.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FormChip)
                    .padding(10.dp),
            ) {
                Text(
                    "设置后，会改变聊天和应用全部的字体大小。如果在使用过程中存在问题或意见，可反馈给官方团队",
                    color = FormInk,
                    fontSize = fontSize,
                    lineHeight = fontSize * 1.4f,
                )
            }
        }
    }
}

@Composable
private fun Avatar(text: String, color: Color, contentColor: Color = Color.White) {
    Box(
        Modifier.size(32.dp).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
