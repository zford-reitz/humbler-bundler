package me.zeb.common.model.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignedDownload {
    /**
     *
     * @return
     * The signedUrl
     */
    /**
     *
     * @param signedUrl
     * The signed_url
     */
    @SerializedName("signed_url")
    @Expose
    var signedUrl: String? = null
    var id: Long = 0
}