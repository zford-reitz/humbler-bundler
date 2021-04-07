package me.zeb.common

import me.zeb.common.db.HumblerBundlerDatabase
import java.io.File

actual fun getPlatformName(): String {
    return "Android"
}

actual fun createDb() : HumblerBundlerDatabase? {
    return null
}

actual fun applicationDirectory(): File {
    return File("")
}