package me.zeb.common.model.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderRef {
    /**
     *
     * @return
     * The gamekey
     */
    /**
     *
     * @param gamekey
     * The gamekey
     */
    @SerializedName("gamekey")
    @Expose
    var gamekey: String? = null
    var id: Long = 0
}