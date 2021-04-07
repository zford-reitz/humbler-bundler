package me.zeb.common

import me.zeb.common.db.HumblerBundlerDatabase
import java.io.File

expect fun getPlatformName(): String

expect fun createDb() : HumblerBundlerDatabase?

expect fun applicationDirectory(): File