package com.xyzl.driverphone.screens

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xyzl.driverphone.openUrl

private data class SocialAccount(
    val id: String,
    val label: String,
    val bound: Boolean,
    val officialUrl: String,
)

private val initialAccounts = listOf(
    SocialAccount("apple", "通过 Apple 登录", false, "https://appleid.apple.com/sign-in"),
    SocialAccount("facebook", "通过 Facebook 登录", true, "https://www.facebook.com/login"),
    SocialAccount("wechat", "通过 WeChat 登录", false, "https://weixin.qq.com/"),
)

/** 更多安全设置页：三方账号绑定状态，未绑定点击跳官方页面 */
@Composable
fun SecuritySettingsScreen(onBack: () -> Unit = {}) {
    val accounts = remember { initialAccounts }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "更多安全设置", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            accounts.forEachIndexed { index, account ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!account.bound) openUrl(account.officialUrl)
                        }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        account.label,
                        fontSize = 15.sp,
                        color = FormInk,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (account.bound) "已绑定" else "未绑定",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (account.bound) FormGreen else FormMuted,
                    )
                }
                if (index != accounts.lastIndex) {
                    HorizontalDivider(color = FormDivide, thickness = 1.dp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        FormNote("未绑定的账号点击后会跳转到对应官方页面完成授权绑定，绑定结果以官方返回为准。")
        Spacer(Modifier.height(110.dp))
    }
}
