package uz.connection.models

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("data")
    val data: T? = null
)
