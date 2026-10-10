package com.xyzl.driverphone.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.entryBackgroundPainter

/**
 * 登录/注册入口页：整屏背景图 + 底部「登录」「注册」两个入口按钮。
 * 注意：这不是登录页/注册页本身，只是进入它们的入口。
 */
@Composable
fun EntryScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF081226))) {
        // 背景图（Android 用真实图片，iOS 回退纯色）
        entryBackgroundPainter()?.let { painter ->
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        // 底部按钮区
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 56.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 登录按钮：金色实心
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFD8B645), Color(0xFFE6C765))))
                    .clickable { onLoginClick() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "登 录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1206),
                    letterSpacing = 4.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            // 注册按钮：描边透明
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0x22FFFFFF))
                    .clickable { onRegisterClick() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "注 册",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE6C765),
                    letterSpacing = 4.sp,
                )
            }
        }
    }
}
