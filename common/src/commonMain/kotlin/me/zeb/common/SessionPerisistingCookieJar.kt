package me.zeb.common

import me.zeb.common.db.CookieQueries
import me.zeb.common.db.HumblerBundlerDatabase
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionPerisistingCookieJar(val cookieQueries: CookieQueries) : CookieJar {

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieQueries.all().executeAsList()
            .map(me.zeb.common.db.Cookie::toCookie)
            .filter { it.expiresAt > System.currentTimeMillis() }
            .filter { url.topPrivateDomain().equals(it.domain) }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.filter { it.name == "_simpleauth_sess" }
            .map(Cookie::toPersistentCookie)
            .forEach { cookieQueries.insert(it) }
    }

}

fun Cookie.toPersistentCookie() = me.zeb.common.db.Cookie(
    name = name,
    value = value,
    expiresAt = expiresAt,
    domain = domain,
    path = path,
    secure = secure,
    httpOnly = httpOnly,
    persistent = persistent,
    hostOnly = hostOnly
)

fun me.zeb.common.db.Cookie.toCookie(): Cookie {
    val cookieBuilder = Cookie.Builder()
        .name(name)
        .value(value)
        .expiresAt(expiresAt)
        .domain(domain)
        .path(path)

    if (secure) cookieBuilder.secure()

    if (httpOnly) cookieBuilder.httpOnly()

    return cookieBuilder.build()
}
