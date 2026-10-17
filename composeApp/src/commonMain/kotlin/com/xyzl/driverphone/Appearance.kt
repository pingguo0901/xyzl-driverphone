package com.xyzl.driverphone

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.russhwolf.settings.Settings

/**
 * 全局字体大小：7 档（1-7，默认 4），存 multiplatform-settings。
 * 1.0 = 系统默认；App 根部通过 LocalDensity.fontScale 覆盖。
 */
object AppFontStore {
    private val settings: Settings by lazy { Settings() }

    var level by mutableStateOf(settings.getInt("font_scale", 4).coerceIn(1, 7))
        private set

    val scale: Float
        get() = when (level) {
            1 -> 0.80f
            2 -> 0.90f
            3 -> 0.95f
            4 -> 1.00f
            5 -> 1.10f
            6 -> 1.25f
            else -> 1.40f
        }

    fun set(value: Int) {
        level = value.coerceIn(1, 7)
        settings.putInt("font_scale", level)
    }
}

/** App 语言（纯本地；zh / en） */
enum class AppLanguage(val code: String, val label: String, val labelEn: String) {
    ZH("zh", "中文", "Chinese"),
    EN("en", "英文", "English");

    companion object {
        fun of(code: String?): AppLanguage = if (code == "en") EN else ZH
    }
}

object AppLanguageStore {
    private val settings: Settings by lazy { Settings() }

    var language by mutableStateOf(AppLanguage.of(settings.getString("app_language", "zh")))
        private set

    fun set(lang: AppLanguage) {
        language = lang
        settings.putString("app_language", lang.code)
    }
}

/**
 * 轻量本地化：不做资源库，所有读取 LocalStrings.current 的地方随语言切换自动重组。
 * 新增文案时同时补 zh / en 两份即可。
 */
data class AppStrings(
    val tabHome: String,
    val tabTrips: String,
    val tabChat: String,
    val tabFinance: String,
    val tabProfile: String,
    val notifications: String,
    val display: String,
    val fontSize: String,
    val language: String,
    val translation: String,
    val chat: String,
    val chatRecords: String,
    val done: String,
    val cancel: String,
    val back: String,
    val exportChats: String,
    val clearChats: String,
    val exportedTitle: String,
    val exportedText: String,
    val clearedTitle: String,
    val clearedText: String,
    val confirm: String,
    val clear: String,
    val failed: String,
)

val ZhStrings = AppStrings(
    tabHome = "首页",
    tabTrips = "行程",
    tabChat = "聊天",
    tabFinance = "财务",
    tabProfile = "我",
    notifications = "通知",
    display = "界面与显示",
    fontSize = "字体大小",
    language = "多语言",
    translation = "翻译",
    chat = "聊天",
    chatRecords = "聊天记录管理",
    done = "完成",
    cancel = "取消",
    back = "返回",
    exportChats = "导出全部聊天记录",
    clearChats = "清空全部聊天记录",
    exportedTitle = "导出完成",
    exportedText = "聊天记录已保存到手机下载目录。",
    clearedTitle = "已清空",
    clearedText = "聊天记录已清空。",
    confirm = "确定",
    clear = "清空",
    failed = "失败",
)

val EnStrings = AppStrings(
    tabHome = "Home",
    tabTrips = "Trips",
    tabChat = "Chat",
    tabFinance = "Finance",
    tabProfile = "Me",
    notifications = "Notifications",
    display = "Display",
    fontSize = "Font Size",
    language = "Language",
    translation = "Translation",
    chat = "Chat",
    chatRecords = "Chat History",
    done = "Done",
    cancel = "Cancel",
    back = "Back",
    exportChats = "Export all chats",
    clearChats = "Clear all chats",
    exportedTitle = "Export complete",
    exportedText = "Chat history saved to Downloads.",
    clearedTitle = "Cleared",
    clearedText = "Chat history cleared.",
    confirm = "OK",
    clear = "Clear",
    failed = "Failed",
)

val LocalStrings = staticCompositionLocalOf { ZhStrings }
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ZH }

fun appStrings(lang: AppLanguage): AppStrings = if (lang == AppLanguage.EN) EnStrings else ZhStrings
