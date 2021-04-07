package me.zeb.common.model.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class JsonSubproduct (
    @SerializedName("machine_name") val machineName: String,
    @SerializedName("url") val url: String,
    @SerializedName("downloads") val downloads: List<JsonDownload>,
    @SerializedName("payee") val payee: Payee,
    @SerializedName("human_name") val humanName: String,
    @SerializedName("custom_download_page_box_html") val customDownloadPageBoxHtml: Any?,
    @SerializedName("icon") val icon: String
)