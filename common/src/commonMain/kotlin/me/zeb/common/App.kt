package me.zeb.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import me.zeb.common.db.HumbleOrder
import me.zeb.common.db.Subproduct
import me.zeb.common.model.Category
import me.zeb.common.model.json.Gamekey
import me.zeb.common.model.json.JsonHumbleOrder
import me.zeb.common.model.json.JsonSubproduct
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*

val db = createDb()
val client = OkHttpClient.Builder()
    .cookieJar(SessionPerisistingCookieJar(db!!.cookieQueries))
    .build()

// https://www.schiff.io/projects/humble-bundle-api
@Composable
fun App() {
    var username by remember { mutableStateOf("") }
    val password = remember { mutableStateOf(TextFieldValue()) }
    var guardCode by remember { mutableStateOf("") }
    var gamekey by remember { mutableStateOf("") }
    val gson = Gson()
    val bundleitems = db!!.subproductQueries.all().executeAsList()

    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
            TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            TextField(value = guardCode, onValueChange = { guardCode = it }, label = { Text("Guard code") })
            Button(onClick = {
                login(username = username, password = password.value.text, guardCode = guardCode)
            }) {
                Text("Login")
            }
            TextField(value = gamekey, onValueChange = { gamekey = it }, label = { Text("Bundle key") })
            Button(onClick = {
                fetchOrder(gamekey)
            }) {
                Text("Fetch a Bundle")
            }
            Button(onClick = {
                fetchAllOrders(gson)
            }) {
                Text("Fetch all Bundles")
            }
            Button(onClick = {
                writeBundlesToDatabase(gson)
            }) {
                Text("Write Bundles to database")
            }
            Button(onClick = {
                fetchIcons()
            }) {
                Text("Fetch icons")
            }
            LazyColumn(
                modifier = Modifier.fillMaxHeight()) {
                items(items = bundleitems, itemContent = { item ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = MaterialTheme.colors.surface,
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.padding(4.dp).fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                        ){
                            Text(text = item.human_name)
                            Text(text = " (${item.category.name})")
                        }
                    }
                })
            }
        }
    }
}

fun fetchIcons() {
    for (subproduct in db!!.subproductQueries.canCacheIcon().executeAsList()) {
        db.subproductQueries.cacheIcon(
            cached_icon = client.newCall(Request.Builder().url(subproduct.icon!!).get().build()).execute().body!!.bytes(),
            id = subproduct.id)
    }
}

fun fetchAllOrders(gson: Gson) {
    val request = Request.Builder()
        .url("https://www.humblebundle.com/api/v1/user/order")
        .addHeader("ajax", "true")
        .addHeader("Accept", "application/json")
        .addHeader("Accept-Charset", "utf-8")
        .addHeader("Keep-Alive", "true")
        .addHeader("X-Requested-By", "hb_android_app")
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Unexpected code $response")
        }

        val ordersJson = response.body!!.string()
        val gamekeys = gson.fromJson(ordersJson, Array<Gamekey>::class.java).map { it.gamekey }

        for (key in gamekeys) {
            if (!File(bundlesDirectory(), "${key}.json").exists()) {
                fetchOrder(key)
            }
        }
    }
}

fun fetchOrder(gamekey: String) {
    val request = Request.Builder()
        .url("https://www.humblebundle.com/api/v1/order/${gamekey}?all_tpkds=true")
        .addHeader("Accept", "application/json")
        .addHeader("Accept-Charset", "utf-8")
        .addHeader("Keep-Alive", "true")
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Unexpected code $response")
        }

        writeBundleToFile(gamekey, response.body!!.string())
    }
}

fun writeBundleToFile(gamekey: String, content: String) {
    bundlesDirectory()
        .also { it.mkdirs() }
        .let { File(it, "${gamekey}.json") }
        .writeText(content)
}

fun bundlesDirectory(): File {
    return File(applicationDirectory(), "bundles")
}

fun writeBundlesToDatabase(gson: Gson) {
    for (bundleFile in bundlesDirectory().listFiles()) {
        try {
            if (!bundlePresentInDatabase(bundleFile.name.substringBeforeLast(".json"))) {
                val jsonHumbleOrder = gson.fromJson(bundleFile.readText(), JsonHumbleOrder::class.java)
                db!!.humbleOrderQueries.insert(jsonHumbleOrder.toHumbleOrder())

                val gamekey = jsonHumbleOrder.gamekey
                for (jsonSubproduct in jsonHumbleOrder.subproducts) {
                    db.subproductQueries.insert(jsonSubproduct.toSubproduct(gamekey))
                }
            }
        } catch (e: Exception) {
            println(bundleFile)
            throw e
        }
    }
}

fun JsonSubproduct.toSubproduct(gamekey: String): Subproduct {
    return Subproduct(
        id = 0,
        order_gamekey = gamekey,
        machine_name = machineName,
        url = url,
        human_name = humanName,
        custom_download_page_box_html = customDownloadPageBoxHtml?.toString(),
        icon = icon,
        category = category(),
        cached_icon = null
    )
}

fun JsonSubproduct.category(): Category {
    val hasComicDownloads = downloads
        .flatMap { it.downloadStruct }
        .map { it.name?.toLowerCase() }
        .any { it == "cbz" || it == "cbr" }

    if (hasComicDownloads) return Category.COMIC

    if (downloads.any { it.platform.toLowerCase() == "ebook" }) return Category.EBOOK

    return Category.OTHER
}

fun JsonHumbleOrder.toHumbleOrder(): HumbleOrder {
    return HumbleOrder(
        gamekey = gamekey,
        created = created,
        category = product.category,
        human_name = product.humanName,
        machine_name = product.machineName)
}

fun bundlePresentInDatabase(bundleKey: String): Boolean {
    return db!!.humbleOrderQueries.countForGamekey(bundleKey).executeAsOne() > 0
}

private fun login(username: String, password: String, guardCode: String?): LoginResult {
    val loginUrl = "https://www.humblebundle.com/processlogin"

    val formBuilder = FormBody.Builder()
        .add("username", username)
        .add("password", password)
    if (guardCode != null && guardCode.isNotBlank()) formBuilder.add("guard", guardCode)

    val formBody = formBuilder.build()

    val request = Request.Builder()
        .url(loginUrl)
        .addHeader("ajax", "true")
        .addHeader("Accept", "application/json")
        .addHeader("Accept-Charset", "utf-8")
        .addHeader("Keep-Alive", "true")
        .addHeader("X-Requested-By", "hb_android_app")
        .post(formBody)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            if (response.body!!.string().contains("humble_guard_required")) {
                return LoginResult.GUARD_REQUIRED
            } else {
                throw IOException("Unexpected code $response")
            }
        }

        return LoginResult.SUCCESS
    }
}