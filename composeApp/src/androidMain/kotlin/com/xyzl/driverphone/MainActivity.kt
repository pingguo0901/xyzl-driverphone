package com.xyzl.driverphone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
}
