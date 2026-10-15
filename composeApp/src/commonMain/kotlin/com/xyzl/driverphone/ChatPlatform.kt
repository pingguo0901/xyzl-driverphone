package com.xyzl.driverphone

/** 相册多选（照片/视频），最多 max 个（上限 10），回调本地 uri 列表 */
expect fun pickMediaMultiple(max: Int, onPicked: (List<String>) -> Unit)

/** 读取当前位置，回调 "纬度,经度"；失败或没权限回调 null */
expect fun currentLocation(onResult: (String?) -> Unit)

/** 语音录制：开始（返回是否成功）/ 停止（回调录音文件路径） */
expect fun startVoiceRecord(): Boolean
expect fun stopVoiceRecord(onResult: (String?) -> Unit)

/** 播放 / 停止播放语音消息（path 为录音文件路径） */
/** 读取本地文件字节（聊天附件上传 Storage 用） */
expect fun readFileBytes(uri: String): ByteArray?

expect fun playVoice(path: String)
expect fun stopVoicePlay()
