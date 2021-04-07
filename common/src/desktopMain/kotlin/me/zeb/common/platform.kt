package me.zeb.common

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.zeb.common.db.HumblerBundlerDatabase
import com.squareup.sqldelight.db.SqlCursor
import me.zeb.common.db.Subproduct
import java.io.File


actual fun getPlatformName(): String {
    return "Desktop"
}

// parts of this shamelesssly stolen from https://github.com/cashapp/sqldelight/issues/1605
actual fun createDb() : HumblerBundlerDatabase? {
    val dbPath = File(applicationDirectory(), "humbler-bundler.db").absolutePath
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

    val currentVer: Int = getVersion(driver)
    if (currentVer == 0) {
        HumblerBundlerDatabase.Schema.create(driver)
        setVersion(driver, 1)
    } else {
        val schemaVer = HumblerBundlerDatabase.Schema.version
        if (schemaVer > currentVer) {
            HumblerBundlerDatabase.Schema.migrate(driver, currentVer, schemaVer)
            setVersion(driver, schemaVer)
        }
    }

    return HumblerBundlerDatabase(
        driver = driver,
        subproductAdapter = Subproduct.Adapter(
            categoryAdapter = EnumColumnAdapter()
        )
    )
}

actual fun applicationDirectory(): File {
    return File(System.getProperty("user.home"), ".humbler-bundler")
}

private fun getVersion(driver: SqlDriver): Int {
    val sqlCursor: SqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
    return sqlCursor.getLong(0)!!.toInt()
}

private fun setVersion(driver: SqlDriver, version: Int) {
    driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
}