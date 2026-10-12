package com.xyzl.driverphone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 原生权限申请：电话 / 附近的设备 / 麦克风 / 通话记录 / 通讯录 / 通知 /
        // 相机 / 音乐和音频 / 照片和视频 / 位置信息 / 短信
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { /* 结果交由系统设置页管理，这里不阻塞进入应用 */ }

        enableEdgeToEdge()
        UpdateManager.setCurrentVersion(packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt())
        initNavigationContext(this)
        initImagePicker(this)
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
        requestAppPermissions()
    }

    /** 一次性把未授权的权限逐项弹窗申请 */
    private fun requestAppPermissions() {
        val missing = AppPermissions.missing(this)
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }
}
