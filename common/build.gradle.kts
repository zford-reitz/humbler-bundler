import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "0.2.0-build132"
    id("com.android.library")
    id("kotlin-android-extensions")
    id("com.squareup.sqldelight")
}

group = "me.zeb"
version = "1.0"

repositories {
    google()
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("com.google.code.gson:gson:2.8.6")
                implementation("com.squareup.okhttp3:okhttp:4.9.1")
                implementation("com.squareup.sqldelight:runtime:1.4.3")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.4.3")
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.2.0")
                api("androidx.core:core-ktx:1.3.1")
                implementation("com.squareup.sqldelight:android-driver:1.4.3")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("org.xerial:sqlite-jdbc:3.30.1")
                implementation("com.squareup.sqldelight:sqlite-driver:1.4.3")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
}

sqldelight {
    database("HumblerBundlerDatabase") {
        packageName = "me.zeb.common.db"
        sourceFolders = listOf("sqldelight")
    }
}