package com.stellarelite.driver

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stellarelite.driver.platform.AppContextHolder
import com.stellarelite.driver.platform.ReceiptPickerHolder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContextHolder.appContext = applicationContext
        ReceiptPickerHolder.activity = this
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ReceiptPickerHolder.handleResult(requestCode, resultCode, data)
    }
}
