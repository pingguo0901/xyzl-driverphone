package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** 翻译目标语言（se_notification_preferences.translate_target_language → language_enum） */
enum class TranslateLanguage(val code: String, val label: String, val labelEn: String) {
    ZH("zh", "中文", "Chinese"),
    EN("en", "英文", "English"),
    MS("ms", "马来文", "Malay"),
    TA("ta", "泰米尔文", "Tamil");

    companion object {
        fun of(code: String?): TranslateLanguage =
            entries.firstOrNull { it.code == code } ?: ZH
    }
}

/** 通知 / 翻译 / 聊天设置（se_notification_preferences） */
data class UserPreferences(
    val systemMessage: Boolean = true,
    val voiceCall: Boolean = true,
    val inAppBanner: Boolean = true,
    val messageSound: Boolean = true,
    val vibrate: Boolean = true,
    val translateTarget: TranslateLanguage = TranslateLanguage.ZH,
    val autoTranslateChat: Boolean = false,
    val chatUseEarpiece: Boolean = true,
    val chatUseStandaloneSend: Boolean = false,
    val chatAutoDownloadViewed: Boolean = true,
    val chatKeepOriginalMedia: Boolean = false,
)

/**
 * 设置页统一读写 se_notification_preferences。
 * 一行一个 user_id（没有唯一约束，所以先查 id 再 update / insert）。
 */
class PreferencesRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    suspend fun load(userId: String): UserPreferences = try {
        val row = supabase.postgrest.from("se_notification_preferences")
            .select(
                Columns.list(
                    "id", "notify_system_message", "notify_voice_call", "notify_in_app_banner",
                    "notify_message_sound", "notify_vibrate", "translate_target_language",
                    "auto_translate_chat", "chat_use_earpiece", "chat_use_standalone_send",
                    "chat_auto_download_viewed", "chat_keep_original_media",
                ),
            ) {
                filter { eq("user_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
        row?.toPrefs() ?: UserPreferences()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        UserPreferences()
    }

    suspend fun save(userId: String, prefs: UserPreferences): Result<Unit> = try {
        val id = supabase.postgrest.from("se_notification_preferences")
            .select(Columns.list("id")) {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()?.s("id")

        val payload = buildJsonObject {
            put("user_id", userId)
            put("notify_system_message", prefs.systemMessage)
            put("notify_voice_call", prefs.voiceCall)
            put("notify_in_app_banner", prefs.inAppBanner)
            put("notify_message_sound", prefs.messageSound)
            put("notify_vibrate", prefs.vibrate)
            put("translate_target_language", prefs.translateTarget.code)
            put("auto_translate_chat", prefs.autoTranslateChat)
            put("chat_use_earpiece", prefs.chatUseEarpiece)
            put("chat_use_standalone_send", prefs.chatUseStandaloneSend)
            put("chat_auto_download_viewed", prefs.chatAutoDownloadViewed)
            put("chat_keep_original_media", prefs.chatKeepOriginalMedia)
        }
        if (id == null) {
            supabase.postgrest.from("se_notification_preferences").insert(payload)
        } else {
            supabase.postgrest.from("se_notification_preferences").update(payload) {
                filter { eq("id", id) }
            }
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun JsonObject.toPrefs(): UserPreferences = UserPreferences(
    systemMessage = b("notify_system_message") ?: true,
    voiceCall = b("notify_voice_call") ?: true,
    inAppBanner = b("notify_in_app_banner") ?: true,
    messageSound = b("notify_message_sound") ?: true,
    vibrate = b("notify_vibrate") ?: true,
    translateTarget = TranslateLanguage.of(s("translate_target_language")),
    autoTranslateChat = b("auto_translate_chat") ?: false,
    chatUseEarpiece = b("chat_use_earpiece") ?: true,
    chatUseStandaloneSend = b("chat_use_standalone_send") ?: false,
    chatAutoDownloadViewed = b("chat_auto_download_viewed") ?: true,
    chatKeepOriginalMedia = b("chat_keep_original_media") ?: false,
)

private fun JsonObject.s(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }

private fun JsonObject.b(key: String): Boolean? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull()
