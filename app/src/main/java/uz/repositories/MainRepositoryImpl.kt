package uz.repositories

import uz.connection.ApiManager
import uz.connection.models.GetSdkTokenRequest
import uz.connection.models.GetSdkTokenResponse
import uz.connection.models.Response
import uz.connection.models.SDKRegisterCardResponse

class MainRepositoryImpl(private val apiManager: ApiManager) : MainRepository {

    override suspend fun getSdkToken(request: GetSdkTokenRequest): Response<GetSdkTokenResponse> {
        return apiManager.getSdkToken(request)
    }

    override suspend fun sdkRegisterCard(idForSDKToken: Int): Response<SDKRegisterCardResponse> {
        return apiManager.sdkRegisterCard(idForSDKToken)
    }

}