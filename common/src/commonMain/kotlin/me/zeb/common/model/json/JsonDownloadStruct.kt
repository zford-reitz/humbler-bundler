package me.zeb.common.model.json

import com.google.gson.annotations.SerializedName

data class JsonDownloadStruct(
    @SerializedName("sha1") val sha1: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("url") val url: Url?,
    @SerializedName("human_size") val humanSize: String,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("small") val small: Int?,
    @SerializedName("md5") val md5: String
)