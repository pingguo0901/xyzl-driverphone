package com.stellarelite.driver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stellarelite.driver.platform.AppContextHolder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContextHolder.appContext = applicationContext
        UpdateManager.setCurrentVersion(packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt())
        setContent {
            App(
                onCheckUpdate = { UpdateManager.checkForUpdate() },
                onRequestUpdate = { info ->
                    UpdateManager.downloadAndInstall(this, info.apkUrl)
                }
            )
        }
    }
}
