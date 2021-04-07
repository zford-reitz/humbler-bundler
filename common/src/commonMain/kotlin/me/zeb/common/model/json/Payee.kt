package me.zeb.common.model.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Payee {
    /**
     *
     * @return
     * The humanName
     */
    /**
     *
     * @param humanName
     * The human_name
     */
    @SerializedName("human_name")
    @Expose
    var humanName: String? = null
    /**
     *
     * @return
     * The machineName
     */
    /**
     *
     * @param machineName
     * The machine_name
     */
    @SerializedName("machine_name")
    @Expose
    var machineName: String? = null
    var id: Long = 0
}