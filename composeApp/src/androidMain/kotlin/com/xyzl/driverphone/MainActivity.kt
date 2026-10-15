package com.xyzl.driverphone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAuthDeepLink(intent)
        UpdateManager.setCurrentVersion(packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt())
        initNavigationContext(this)
        initPermissionPlatform(this)
        initImagePicker(this)
        initChatPlatform(this)
        initQrPlatform(this)
        setContent {
            App(
                appVersionName = packageManager.getPackageInfo(packageName, 0).versionName ?: "",
                onCheckUpdate = { UpdateManager.checkForUpdate() },
                onApplyUpdate = { info, _ ->
                    UpdateManager.downloadAndInstall(this, info.apkUrl)
                    null
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthDeepLink(intent)
    }

    /** 重置密码邮件点回来：import 会话 + 通知 App 弹「重建密码」页 */
    private fun handleAuthDeepLink(intent: Intent?) {
        val data = intent?.data?.toString() ?: return
        if (!data.startsWith(AuthDeepLink.REDIRECT_URL)) return
        if (data.contains("type=recovery")) AuthDeepLink.markRecovery()
        Supabase.client.handleDeeplinks(intent)
    }
}
