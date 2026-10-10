package com.xyzl.driverphone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** 注册表单字段（跨平台共享状态） */
data class RegisterFormState(
    val accountType: String = "个人",
    val nickname: String = "",
    val fullName: String = "",
    val gender: String = "",
    val birthday: String = "",
    val street: String = "",
    val garden: String = "",
    val postcode: String = "",
    val city: String = "",
    val state: String = "",
    val dialCode: String = "+60",
    val phone: String = "",
    val email: String = "",
    val wechat: String = "",
    val icUri: String? = null,
    val licenseUri: String? = null,
    val passportUri: String? = null,
    val agreedPolicy: Boolean = false,
)

/** 马来西亚区号选项 */
val DialCodes = listOf("+60", "+65", "+62", "+66", "+86", "+852", "+853", "+886")

/** 性别选项 */
val GenderOptions = listOf("男", "女")

/** 账号类型选项 */
val AccountTypes = listOf("个人", "公司")

/** 昵称：中文 / 英文 / 数字 / 常见标点 */
fun sanitizeNickname(input: String): String =
    input.filter { it.isLetterOrDigit() || it in " _-.·,，。！？!?@#&()（）【】" }

/** 姓名：仅英文和空格 */
fun sanitizeEnglishName(input: String): String =
    input.filter { it.isLetter() && it.code < 128 || it == ' ' }

/** 英文数字标点：门牌/花园 */
fun sanitizeEnglishPunct(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in " ,.-/#()'" }

/** 纯数字 */
fun sanitizeDigits(input: String): String = input.filter { it.isDigit() }

/** 纯英文 */
fun sanitizeEnglish(input: String): String =
    input.filter { it.isLetter() && it.code < 128 || it == ' ' }

/** 邮箱：英文 + 数字 + 邮箱标点 */
fun sanitizeEmail(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in "._%+-@" }

/** 微信号：英文 + 数字 + 标点 */
fun sanitizeWechat(input: String): String =
    input.filter { (it.isLetterOrDigit() && it.code < 128) || it in "._-@" }

/** 生日显示格式：yyyy-MM-dd */
fun formatBirthday(year: Int, month: Int, day: Int): String =
    year.toString().padStart(4, '0') + "-" +
        month.toString().padStart(2, '0') + "-" +
        day.toString().padStart(2, '0')

/** 注册表单是否可提交 */
fun RegisterFormState.canSubmit(): Boolean =
    nickname.isNotBlank() &&
        fullName.isNotBlank() &&
        gender.isNotBlank() &&
        birthday.isNotBlank() &&
        street.isNotBlank() &&
        garden.isNotBlank() &&
        postcode.isNotBlank() &&
        city.isNotBlank() &&
        state.isNotBlank() &&
        phone.isNotBlank() &&
        email.isNotBlank() &&
        icUri != null &&
        licenseUri != null &&
        passportUri != null &&
        agreedPolicy

/** 记住表单状态 */
@Composable
fun rememberRegisterForm(): RegisterFormState {
    var state by remember { mutableStateOf(RegisterFormState()) }
    return state
}
