package com.xyzl.driverphone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

/** 全局字体大小：7 档（1-7，默认 4），存 multiplatform-settings。 */
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
