import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(libs.haze)
            implementation(libs.haze.materials)
        }
    }
}

android {
    namespace = "com.xyzl.driverphone"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // 只需改 appVersionName，versionCode 会自动按 major*10000 + minor*100 + patch 计算
    val appVersionName = "1.0.39"
    val appVersionParts = appVersionName.split(".").map { it.toIntOrNull() ?: 0 }
    val appVersionCode = (appVersionParts.getOrNull(0) ?: 0) * 10000 +
        (appVersionParts.getOrNull(1) ?: 0) * 100 +
        (appVersionParts.getOrNull(2) ?: 0)

    defaultConfig {
        applicationId = "com.xyzl.driverphone"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "xyzl2026"
            keyAlias = "xyzl-driver"
            keyPassword = "xyzl2026"
            storeType = "PKCS12"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
