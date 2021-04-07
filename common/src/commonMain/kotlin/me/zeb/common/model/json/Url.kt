package me.zeb.common.model.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Url {
    /**
     *
     * @return
     * The web
     */
    /**
     *
     * @param web
     * The web
     */
    @SerializedName("web")
    @Expose
    var web: String? = null
    /**
     *
     * @return
     * The bittorrent
     */
    /**
     *
     * @param bittorrent
     * The bittorrent
     */
    @SerializedName("bittorrent")
    @Expose
    var bittorrent: String? = null
    var id: Long = 0
}