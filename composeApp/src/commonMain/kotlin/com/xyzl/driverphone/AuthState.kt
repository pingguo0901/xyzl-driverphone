package com.xyzl.driverphone

/** 全局登录态 */
sealed class AuthState {

    /** 启动中 / 正在恢复本地会话 */
    data object Loading : AuthState()

    /** 已登录 */
    data class Authenticated(
        val userId: String,
        val email: String?,
        /** se_users.primary_role（决定进哪个首页）；查不到为 null */
        val role: String? = null,
    ) : AuthState()

    /** 未登录（含已退出） */
    data object Unauthenticated : AuthState()

    /** 出错（网络 / 会话刷新失败等） */
    data class Error(val message: String) : AuthState()
}
