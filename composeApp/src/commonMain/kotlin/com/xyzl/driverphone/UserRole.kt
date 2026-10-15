package com.xyzl.driverphone

/**
 * 注册角色。
 * code 与 Supabase 的 primary_role_enum / register_user(p_role) 一一对应。
 */
enum class UserRole(val code: String) {
    PERSONAL("personal"),
    DRIVER("driver"),
    ENTERPRISE("enterprise"),
    SDN_BHD("sdn_bhd"),
    COMPANY_ADMIN("company_admin"),
    CUSTOMER("customer");

    companion object {
        /** 由注册接口返回的字符串还原枚举；未知返回 null */
        fun fromCode(code: String?): UserRole? =
            entries.firstOrNull { it.code == code }
    }
}
