package com.xyzl.driverphone

/** 需要按场景申请的运行时权限 */
enum class PermissionScene { Camera, Photos, Location, Notification, Voice }

/** 进入对应场景时申请该场景所需权限（平台各自实现；已授权则什么也不做） */
expect fun requestScenePermission(scene: PermissionScene)
