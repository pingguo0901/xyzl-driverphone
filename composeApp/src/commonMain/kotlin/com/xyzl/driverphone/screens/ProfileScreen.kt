package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text("我的", fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111113))
        Spacer(Modifier.height(14.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).background(Color(0xFFF2F2F4), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color(0xFF8E8E93))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("陈师傅", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111113))
                    Spacer(Modifier.height(4.dp))
                    Text("车牌：JXY 8888", fontSize = 13.sp, color = Color(0xFF8E8E93))
                    Text("手机：+60 12-345 6789", fontSize = 13.sp, color = Color(0xFF8E8E93))
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(vertical = 6.dp)) {
                ProfileItem("个人信息")
                HorizontalDivider(color = Color(0xFFF0F0F2))
                ProfileItem("车辆信息")
                HorizontalDivider(color = Color(0xFFF0F0F2))
                ProfileItem("设置")
                HorizontalDivider(color = Color(0xFFF0F0F2))
                ProfileItem("关于")
            }
        }
    }
}

@Composable
private fun ProfileItem(title: String) {
    Text(
        title,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        fontSize = 16.sp,
        color = Color(0xFF111113),
    )
}
