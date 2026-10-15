package com.xyzl.driverphone

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 重置密码「邮件链接」回跳标记。
 * Android 端在 MainActivity 收到 `xyzl-driverphone://auth/reset#...&type=recovery` 时置位，
 * App() 监听到就弹「重建密码」页。
 */
object AuthDeepLink {
    /** 必须和 Supabase 后台 Redirect URLs 里登记的一致 */
    const val REDIRECT_URL: String = "xyzl-driverphone://auth/reset"

    private val _recovery = MutableStateFlow(false)
    val recovery: StateFlow<Boolean> = _recovery

    fun markRecovery() { _recovery.value = true }
    fun clear() { _recovery.value = false }
}
