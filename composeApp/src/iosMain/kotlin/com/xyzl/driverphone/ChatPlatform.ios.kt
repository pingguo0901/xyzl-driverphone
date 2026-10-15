package com.xyzl.driverphone

actual fun pickMediaMultiple(max: Int, onPicked: (List<String>) -> Unit) {
    onPicked(emptyList())
}

actual fun currentLocation(onResult: (String?) -> Unit) {
    onResult(null)
}

actual fun startVoiceRecord(): Boolean = false

actual fun stopVoiceRecord(onResult: (String?) -> Unit) {
    onResult(null)
}

actual fun readFileBytes(uri: String): ByteArray? = null

actual fun playVoice(path: String) {
}

actual fun stopVoicePlay() {
}
