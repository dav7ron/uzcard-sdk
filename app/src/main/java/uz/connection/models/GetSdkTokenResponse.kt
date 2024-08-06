package uz.connection.models

import com.google.gson.annotations.SerializedName

data class GetSdkTokenRequest(
    val userName: String? = null,
    val phoneNumber: String? = null,
//    val password: String? = null,
    val token: String? = null
)

data class GetSdkTokenResponse(
    val idForSDKToken: String? = null
)




