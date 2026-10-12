package com.xyzl.driverphone.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xyzl.driverphone.DialCodes
import com.xyzl.driverphone.ImageSource
import com.xyzl.driverphone.loadImageBitmap
import com.xyzl.driverphone.sanitizeDigits
import com.xyzl.driverphone.sanitizeEmail
import com.xyzl.driverphone.sanitizeNickname
import com.xyzl.driverphone.sanitizeWechat

/** 个人信息页：头像 + 昵称 + 手机号（区号）+ 邮箱 + 微信号 */
@Composable
fun PersonalInfoScreen(
    onBack: () -> Unit = {},
    onPickImage: (ImageSource, (String) -> Unit) -> Unit = { _, _ -> },
) {
    var avatarUri by remember { mutableStateOf<String?>(null) }
    var nickname by remember { mutableStateOf("") }
    var dialCode by remember { mutableStateOf(DialCodes.first()) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var wechat by remember { mutableStateOf("") }
    var showAvatarSheet by remember { mutableStateOf(false) }
    var showSaved by remember { mutableStateOf(false) }

    val avatarBitmap = remember(avatarUri) { avatarUri?.let { loadImageBitmap(it) } }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = "个人信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))

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
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap,
                            contentDescription = "头像",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "头像",
                            tint = FormMuted,
                            modifier = Modifier.size(46.dp),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("点击设置头像", fontSize = 12.sp, color = FormMuted)
            }
        }

        Spacer(Modifier.height(20.dp))

        FormTextField(
            label = "昵称",
            value = nickname,
            placeholder = "中文 / 英文 / 数字 / 标点",
            onValueChange = { nickname = sanitizeNickname(it) },
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
                placeholder = { Text("仅数字", color = FormMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = formFieldColors(),
            )
        }

        FormTextField(
            label = "邮箱地址",
            value = email,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { email = sanitizeEmail(it) },
        )
        FormTextField(
            label = "微信号",
            value = wechat,
            placeholder = "选填 · 英文 / 数字 / 标点",
            onValueChange = { wechat = sanitizeWechat(it) },
        )

        Spacer(Modifier.height(6.dp))
        FormPrimaryButton(text = "保存更改", onClick = { showSaved = true })
        Spacer(Modifier.height(110.dp))
    }

    if (showAvatarSheet) {
        Dialog(onDismissRequest = { showAvatarSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FormCard,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("设置头像", color = FormInk, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
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
                        Text("拍照", color = FormInk, fontSize = 15.sp)
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
                        Text("从相册选择", color = FormInk, fontSize = 15.sp)
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

    if (showSaved) {
        AlertDialog(
            onDismissRequest = { showSaved = false },
            containerColor = FormCard,
            title = { Text("保存成功", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FormInk) },
            text = { Text("个人信息已更新", fontSize = 14.sp, color = FormMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showSaved = false
                    onBack()
                }) {
                    Text("确定", color = FormGreen, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
