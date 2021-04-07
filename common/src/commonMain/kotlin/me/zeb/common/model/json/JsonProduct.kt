package me.zeb.common.model.json

import com.google.gson.annotations.SerializedName

class JsonProduct(
    @SerializedName("category") val category: String,
    @SerializedName("human_name") val humanName: String,
    @SerializedName("machine_name") val machineName: String,
    @SerializedName("supports_canonical") val supportsCanonical: Boolean
)