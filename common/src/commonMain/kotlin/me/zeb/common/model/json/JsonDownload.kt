package me.zeb.common.model.json

import com.google.gson.annotations.SerializedName

data class JsonDownload(
    @SerializedName("machine_name") val machineName: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("download_struct") val downloadStruct: List<JsonDownloadStruct>,
//    @SerializedName("options_dict") val optionsDict: OptionsDict,
    @SerializedName("download_identifier") val downloadIdentifier: String?,
    @SerializedName("download_version_number")val downloadVersionNumber: Int?
)