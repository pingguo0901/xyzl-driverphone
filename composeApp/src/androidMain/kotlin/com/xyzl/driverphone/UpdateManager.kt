package com.xyzl.driverphone

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object UpdateManager {
    /** 版本清单：主地址走 GitHub raw，国内网络常被墙 → 备用走 Supabase 镜像 */
    private val VERSION_URLS = listOf(
        "https://raw.githubusercontent.com/pingguo0901/xyzl-driverphone/main/version.json",
        // 加时间戳参数避开 CDN 缓存，保证拿到最新版本号
        "https://yldweqbpvlngmkhvjwrn.supabase.co/storage/v1/object/public/releases/version.json?t=" +
            (System.currentTimeMillis() / 60000),
    )
    private const val APK_FILENAME = "xyzl-driverphone.apk"
    private var currentVersionCode = 0
    private var apkDownloadId = 0L

    fun setCurrentVersion(versionCode: Int) {
        currentVersionCode = versionCode
    }

    suspend fun checkForUpdate(): VersionInfo? = withContext(Dispatchers.IO) {
        for (versionUrl in VERSION_URLS) {
            val info = fetchVersion(versionUrl)
            if (info != null) return@withContext info
        }
        null
    }

    /** 拉一个版本清单地址；失败（网络/被墙/解析不了）返回 null，交给下一个地址 */
    private fun fetchVersion(versionUrl: String): VersionInfo? = try {
        val conn = (URL(versionUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 6000
            readTimeout = 6000
            requestMethod = "GET"
        }
        if (conn.responseCode == 200) {
            val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
            val remoteCode = json.optInt("versionCode", 0)
            if (remoteCode > currentVersionCode) {
                VersionInfo(
                    versionCode = remoteCode,
                    versionName = json.optString("versionName", ""),
                    apkUrl = json.optString("apkUrl", ""),
                    changelog = json.optString("changelog", "")
                )
            } else null
        } else null
    } catch (e: Exception) {
        null
    }

    fun downloadAndInstall(context: Context, apkUrl: String) {
        // 主地址（GitHub）+ 备用镜像（Supabase），第一个失败自动换第二个
        val urls = listOf(apkUrl, mirrorUrl(apkUrl)).filter { it.isNotBlank() }.distinct()
        enqueue(context, urls, 0)
    }

    /** 由 GitHub tag 推导备用镜像地址（同一个包，换条线路） */
    private fun mirrorUrl(apkUrl: String): String {
        val m = Regex("download/(v[0-9.]+)/").find(apkUrl) ?: return ""
        return "https://yldweqbpvlngmkhvjwrn.supabase.co/storage/v1/object/public/releases/composeApp-release-" +
            m.groupValues[1] + ".apk"
    }

    private fun enqueue(context: Context, urls: List<String>, index: Int) {
        if (index >= urls.size) return
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val destFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_FILENAME)
        if (destFile.exists()) destFile.delete()

        val request = DownloadManager.Request(Uri.parse(urls[index])).apply {
            setTitle("星域司机更新下载中...")
            setDescription("正在下载最新版本")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // Android 10+ 只允许写进应用自己的外部文件目录（cache 目录会直接下载失败）
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_FILENAME)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        val id = downloadManager.enqueue(request)
        apkDownloadId = id

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val got = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (got != id) return
                ctx.unregisterReceiver(this)
                if (downloadSucceeded(downloadManager, id)) {
                    installApk(ctx, destFile)
                } else {
                    enqueue(ctx, urls, index + 1)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun downloadSucceeded(dm: DownloadManager, id: Long): Boolean {
        val cursor = dm.query(DownloadManager.Query().setFilterById(id)) ?: return false
        cursor.use { cur ->
            if (!cur.moveToFirst()) return false
            val status = cur.getInt(cur.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
    }

    private fun installApk(context: Context, file: File) {
        if (!file.exists()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val apkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.grantUriPermission(
                "com.android.packageinstaller",
                apkUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val chooser = Intent.createChooser(intent, "安装星域司机更新")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
        }
    }
}
