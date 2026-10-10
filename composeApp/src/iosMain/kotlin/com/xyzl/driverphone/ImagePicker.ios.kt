package com.xyzl.driverphone

actual fun pickBirthday(onPicked: (String) -> Unit) {
    // iOS 端暂未接入原生日历，留空
}

actual fun pickImage(source: ImageSource, onPicked: (String) -> Unit) {
    // iOS 端暂未接入相机/相册，留空
}
