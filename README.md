# xyzl-driverphone

星域集团（Stellar Elite）司机端 App。技术栈：Kotlin Multiplatform (KMP) + Compose Multiplatform，后续叠加 PWA。

## 页面
- 首页：接单状态、今日订单/收入概览
- 行程：进行中/待接的订单
- 历史：已完成/已取消的行程记录
- 财务：账户余额、提现、周收入
- 我：司机个人信息与设置

## 技术栈
- Kotlin 2.1.0
- Compose Multiplatform 1.7.3（Android + iOS）
- Android Gradle Plugin 8.5.2
- Gradle 8.9

## 构建
```bash
# Android APK
./gradlew :composeApp:assembleRelease
# 输出：composeApp/build/outputs/apk/release/composeApp-release.apk

# iOS framework
./gradlew :composeApp:linkDebugFrameworkIosArm64
```

## Release
打 tag（`v*`）触发 GitHub Actions 自动构建 APK 并发布到 Release。
