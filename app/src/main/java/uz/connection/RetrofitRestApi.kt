package uz.connection

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import uz.connection.models.GetSdkTokenRequest
import uz.connection.models.GetSdkTokenResponse
import uz.connection.models.SDKRegisterCardResponse

interface RetrofitRestApi {

    @POST("SDKCards/GetIdForSDKToken")
    suspend fun getSdkToken(@Body request: GetSdkTokenRequest): uz.connection.models.Response<GetSdkTokenResponse>

    @POST("SDKCards/RegisterCard")
    suspend fun sdkRegisterCard(@Query("id_for_sdk_token") idForSDKToken: Int): uz.connection.models.Response<SDKRegisterCardResponse>

}