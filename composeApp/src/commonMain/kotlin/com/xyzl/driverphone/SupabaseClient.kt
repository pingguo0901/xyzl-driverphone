package com.xyzl.driverphone

import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Supabase 单例（懒加载，全 App 共用一份）。
 *
 * 集成模块：
 * - Auth      ：邮箱 / 密码 登录注册
 * - Postgrest ：RPC（register_user）/ 表读写
 * - Realtime  ：实时订阅
 * - Storage   ：文件上传（KYC 证件照等）
 *
 * 会话持久化：multiplatform-settings
 *   Android = SharedPreferences，iOS = NSUserDefaults，重启 App 不掉登录。
 */
object Supabase {

    /** 集团枢纽（SEH）项目地址 */
    const val URL: String = "https://yldweqbpvlngmkhvjwrn.supabase.co"

    /** anon key（客户端公开密钥，可随包发布；不要换成 service_role） */
    const val ANON_KEY: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlsZHdlcWJwdmxuZ21raHZqd3JuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA3MDA1NzksImV4cCI6MjEwNjI3NjU3OX0.PSfYg0P64dJYdoJKd3c703oe64Ed0sdN1R7bAJ9oLxQ"

    /** 懒加载：第一次访问时才建客户端 */
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = URL,
            supabaseKey = ANON_KEY,
        ) {
            install(Auth) {
                // 会话落盘（Settings() 来自 multiplatform-settings-no-arg）
                sessionManager = SettingsSessionManager(settings = Settings())
            }
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }
}
