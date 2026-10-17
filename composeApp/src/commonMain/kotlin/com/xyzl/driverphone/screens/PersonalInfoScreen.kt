package com.xyzl.driverphone.screens

import com.xyzl.driverphone.LocalStrings
import com.xyzl.driverphone.fill

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xyzl.driverphone.DialCodes
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.MyInfo
import com.xyzl.driverphone.ProfileEditRepository
import com.xyzl.driverphone.loadImageBitmap
import com.xyzl.driverphone.loadRemoteImageBitmap
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEmail
import com.xyzl.driverphone.sanitizeNickname
import com.xyzl.driverphone.sanitizeWechat
import kotlinx.coroutines.launch

/** 个人信息页：头像 + 昵称 + ID + 手机号（区号）+ 邮箱 + 微信号（se_users） */
@Composable
fun PersonalInfoScreen(
    userId: String? = null,
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
) {
    val repo = remember { ProfileEditRepository() }
    val scope = rememberCoroutineScope()

    var loaded by remember { mutableStateOf(false) }
    var initial by remember { mutableStateOf(MyInfo()) }

    var avatarUri by remember { mutableStateOf<String?>(null) }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var remoteAvatar by remember { mutableStateOf<ImageBitmap?>(null) }
    var localAvatar by remember { mutableStateOf<ImageBitmap?>(null) }

    var nickname by remember { mutableStateOf("") }
    var displayId by remember { mutableStateOf("") }
    var dialCode by remember { mutableStateOf(DialCodes.first()) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var wechat by remember { mutableStateOf("") }

    var showAvatarSheet by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val id = userId ?: return@LaunchedEffect
        val info = repo.myInfo(id) ?: return@LaunchedEffect
        initial = info
        nickname = info.fullName
        displayId = info.displayId.orEmpty()
        email = info.email.orEmpty()
        wechat = info.wechatId.orEmpty()
        avatarUrl = info.avatarUrl
        val digits = info.phone.orEmpty().filter { it.isDigit() }
        val code = DialCodes.firstOrNull { digits.startsWith(it.removePrefix("+")) }
        if (code != null) {
            dialCode = code
            phone = digits.removePrefix(code.removePrefix("+"))
        } else {
            phone = digits
        }
        loaded = true
    }

    LaunchedEffect(avatarUrl, avatarUri) {
        avatarUri?.let { localAvatar = loadImageBitmap(it); return@LaunchedEffect }
        val url = avatarUrl
        remoteAvatar = if (url.isNullOrBlank()) null else loadRemoteImageBitmap(url)
    }

    val shownAvatar = localAvatar ?: remoteAvatar
    val phoneFull = dialCode.removePrefix("+") + phone.filter { it.isDigit() }
    val changed = loaded && (
        nickname != initial.fullName ||
            displayId != initial.displayId.orEmpty() ||
            phoneFull != initial.phone.orEmpty().filter { it.isDigit() } ||
            email != initial.email.orEmpty() ||
            wechat != initial.wechatId.orEmpty() ||
            avatarUri != null
        )

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = LocalStrings.current.s0045, onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(FormChip)
                            .clickable { showAvatarSheet = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (shownAvatar != null) {
                            Image(
                                bitmap = shownAvatar,
                                contentDescription = LocalStrings.current.s0026,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = LocalStrings.current.s0026,
                                tint = FormMuted,
                                modifier = Modifier.size(46.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(LocalStrings.current.s0587, fontSize = 12.sp, color = FormMuted)
                }
            }

            Spacer(Modifier.height(20.dp))

            FormTextField(
                label = LocalStrings.current.s0066,
                value = nickname,
                placeholder = LocalStrings.current.s0098,
                onValueChange = { nickname = sanitizeNickname(it) },
            )

            FormTextField(
                label = "ID",
                value = displayId,
                placeholder = LocalStrings.current.s0640,
                onValueChange = { displayId = it.filter { c -> c.isLetterOrDigit() || c == '-' }.take(20) },
            )

            FormLabel("手机号")
            Row(
                Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FormSelectCompact(
                    value = dialCode,
                    options = DialCodes,
                    onSelect = { dialCode = it },
                    modifier = Modifier.width(110.dp),
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = sanitizeDigits(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(LocalStrings.current.s0016, color = FormMuted) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = formFieldColors(),
                )
            }

            FormTextField(
                label = LocalStrings.current.s0079,
                value = email,
                placeholder = LocalStrings.current.s0004,
                onValueChange = { email = sanitizeEmail(it) },
            )
            FormTextField(
                label = LocalStrings.current.s0133,
                value = wechat,
                placeholder = LocalStrings.current.s0188,
                onValueChange = { wechat = sanitizeWechat(it) },
            )

            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = if (saving) LocalStrings.current.s0107 else LocalStrings.current.s0110,
            enabled = changed && !saving,
            onClick = {
                val id = userId
                if (id == null) {
                    dialogTitle = "请先登录"
                    dialogBody = "登录后才能保存个人信息"
                    showDialog = true
                } else {
                    saving = true
                    scope.launch {
                        var url = avatarUrl
                        avatarUri?.let { local ->
                            url = repo.uploadAvatar(id, local) ?: avatarUrl
                        }
                        val err = repo.saveInfo(
                            userId = id,
                            fullName = nickname,
                            displayId = displayId.ifBlank { null },
                            phone = phoneFull.ifBlank { null },
                            email = email.ifBlank { null },
                            wechatId = wechat.ifBlank { null },
                            avatarUrl = url,
                        )
                        saving = false
                        if (err == null) {
                            avatarUrl = url
                            avatarUri = null
                            initial = initial.copy(
                                fullName = nickname,
                                displayId = displayId.ifBlank { null },
                                phone = phoneFull.ifBlank { null },
                                email = email.ifBlank { null },
                                wechatId = wechat.ifBlank { null },
                                avatarUrl = url,
                            )
                            dialogTitle = "保存成功"
                            dialogBody = "个人信息已更新"
                        } else {
                            dialogTitle = "保存失败"
                            dialogBody = err
                        }
                        showDialog = true
                    }
                }
            },
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showAvatarSheet) {
        Dialog(onDismissRequest = { showAvatarSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FormCard,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(LocalStrings.current.s0657, color = FormInk, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            showAvatarSheet = false
                            onPickImage(ImageSource.Camera) { avatarUri = it }
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.PhotoCamera, null, tint = FormGreen)
                        Spacer(Modifier.width(12.dp))
                        Text(LocalStrings.current.s0011, color = FormInk, fontSize = 15.sp)
                    }
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            showAvatarSheet = false
                            onPickImage(ImageSource.Gallery) { avatarUri = it }
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, null, tint = FormGreen)
                        Spacer(Modifier.width(12.dp))
                        Text(LocalStrings.current.s0018, color = FormInk, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showAvatarSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = FormMuted)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = FormCard,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text(dialogBody, fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (dialogTitle == "保存成功") onBack()
                }) {
                    Text(LocalStrings.current.confirm, color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
