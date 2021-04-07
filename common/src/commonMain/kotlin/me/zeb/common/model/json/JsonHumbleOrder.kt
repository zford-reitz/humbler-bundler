package me.zeb.common.model.json

import com.google.gson.annotations.SerializedName
import java.util.*

data class JsonHumbleOrder(
    @SerializedName("subproducts") val subproducts: List<JsonSubproduct>,
    @SerializedName("product") val product: JsonProduct,
    @SerializedName("gamekey") val gamekey: String,
    @SerializedName("created") val created: String
)