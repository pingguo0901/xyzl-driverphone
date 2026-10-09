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
        setContent {
            App(
                onCheckUpdate = { UpdateManager.checkForUpdate() },
                onApplyUpdate = { info, _ ->
                    UpdateManager.downloadAndInstall(this, info.apkUrl)
                    null
                },
            )
        }
    }
}
