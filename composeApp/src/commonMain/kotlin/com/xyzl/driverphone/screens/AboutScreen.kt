package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.appIconPainter

/** 关于平台页：APP 图标 + 名称 + 版本号 + 版本更新入口 + EULA 按钮 + 版权 */
@Composable
fun AboutScreen(
    versionName: String,
    onOpenVersionUpdate: () -> Unit = {},
    onOpenEula: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val icon = remember { appIconPainter() }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FormPageHeader(title = LocalStrings.current.s0047, onBack = onBack)
        // 上半内容可滚动，撑满剩余空间
        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Spacer(Modifier.height(36.dp))

        Box(
            Modifier.size(92.dp).clip(RoundedCornerShape(24.dp)).background(FormChip),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Image(painter = icon, contentDescription = LocalStrings.current.s0493, modifier = Modifier.fillMaxSize())
            } else {
                Text(LocalStrings.current.s0492, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = FormInk)
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(LocalStrings.current.s0496, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = FormInk)
        Spacer(Modifier.height(6.dp))
        Text("Version $versionName", fontSize = 13.sp, color = FormMuted)

        Spacer(Modifier.height(30.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            FormValueRow(
                label = LocalStrings.current.s0163,
                value = "",
                onClick = onOpenVersionUpdate,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

            Spacer(Modifier.height(24.dp))
        }

        // 底部固定：《终端用户软件许可协议》+ 版权
        TextButton(onClick = onOpenEula) {
            Text(LocalStrings.current.s0225, fontSize = 14.sp, color = FormGreen)
        }
        Spacer(Modifier.height(8.dp))
        Text(LocalStrings.current.s0501, fontSize = 12.sp, color = FormMuted)
        Spacer(Modifier.height(16.dp))
    }
}
