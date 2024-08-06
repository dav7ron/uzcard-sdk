package uz.connection

import uz.connection.models.GetSdkTokenRequest
import uz.connection.models.GetSdkTokenResponse
import uz.connection.models.SDKRegisterCardResponse

class ApiManager(private val retrofitRestApi: RetrofitRestApi) {

    suspend fun getSdkToken(
        request: GetSdkTokenRequest
    ): uz.connection.models.Response<GetSdkTokenResponse> {
        return safeApiCall {
            retrofitRestApi.getSdkToken(
                request
            )
        }
    }

    suspend fun sdkRegisterCard(idForSDKToken: Int): uz.connection.models.Response<SDKRegisterCardResponse> {
        return safeApiCall {
            retrofitRestApi.sdkRegisterCard(idForSDKToken)
        }
    }
}