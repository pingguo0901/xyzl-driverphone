package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.SecurityRepository

private data class OauthItem(val provider: String, val label: String)

private val OauthItems = listOf(
    OauthItem("apple", "通过 Apple 登录"),
    OauthItem("facebook", "通过 Facebook 登录"),
    OauthItem("wechat", "通过 WeChat 登录"),
)

/** 更多安全设置页：se_user_oauth_bindings 的绑定状态；未接入 SDK 前点击弹「即将开放」 */
@Composable
fun SecuritySettingsScreen(userId: String? = null, onBack: () -> Unit = {}) {
    val repo = remember { SecurityRepository() }
    var bound by remember { mutableStateOf<Set<String>>(emptySet()) }
    var loaded by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val id = userId
        bound = if (id == null) emptySet() else repo.boundProviders(id)
        loaded = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = LocalStrings.current.s0067, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            OauthItems.forEachIndexed { index, item ->
                val isBound = bound.contains(item.provider)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { hint = LocalStrings.current.s0621 }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.label, fontSize = 15.sp, color = FormInk, modifier = Modifier.weight(1f))
                    Text(
                        text = if (isBound) LocalStrings.current.s0383 else LocalStrings.current.s0536,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isBound) FormGreen else FormMuted,
                    )
                }
                if (index != OauthItems.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        FormNote(
            if (!loaded) LocalStrings.current.s0555
            else LocalStrings.current.s0627,
        )
        Spacer(Modifier.height(110.dp))
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = FormCard,
            title = { Text(LocalStrings.current.s0002, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(message, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = { hint = null }) {
                    Text(LocalStrings.current.confirm, color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
