package uz.repositories

import uz.connection.models.GetSdkTokenRequest
import uz.connection.models.GetSdkTokenResponse
import uz.connection.models.Response
import uz.connection.models.SDKRegisterCardResponse

interface MainRepository {

    suspend fun getSdkToken(request: GetSdkTokenRequest): Response<GetSdkTokenResponse>

    suspend fun sdkRegisterCard(idForSDKToken: Int): Response<SDKRegisterCardResponse>

}