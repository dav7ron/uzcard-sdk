package uz.connection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

fun runAsync(
    action: suspend () -> Unit
): Job {
    return GlobalScope.launch(Dispatchers.Main) {
        try {
            action.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
    try {
        return Resource.Success(apiCall.invoke()).data
    } catch (e: HttpException) {
        throw convertToProperException(e)
    } catch (e: IOException) {
        throw NetworkException(e)
    } catch (e: Exception) {
        throw e
    }
}

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()

    data class Failure(
        val isNetworkError: Boolean?, val errorCode: Int?, val errorBody: ResponseBody?
    ) : Resource<Nothing>()

}


private fun convertToProperException(httpException: HttpException): Exception {
    val responseCode = httpException.code()
    val errorMessage = httpException.response()?.errorBody()?.string()

    return when (responseCode) {
        400 -> BadRequestException(errorMessage)
        401 -> UnauthorisedException(errorMessage)
        402 -> PaymentRequiredException(errorMessage)
        403 -> ForbiddenException(errorMessage)
        404 -> NotFoundException(errorMessage)
        405 -> MethodNotAllowedException(errorMessage)

        500 -> InternalServerErrorException(errorMessage)
        501 -> NotImplementedException(errorMessage)
        502 -> BadGatewayException(errorMessage)
        503 -> ServiceUnavailableException(errorMessage)
        else -> ApiException(errorMessage)
    }
}

class NetworkException(cause: Exception) : RuntimeException(cause)

open class ApiException(errorMessage: String?) : RuntimeException(errorMessage)

class BadRequestException(errorMessage: String?) : ApiException(errorMessage)

class UnauthorisedException(errorMessage: String?) : ApiException(errorMessage)

class PaymentRequiredException(errorMessage: String?) : ApiException(errorMessage)

class ForbiddenException(errorMessage: String?) : ApiException(errorMessage)

class NotFoundException(errorMessage: String?) : ApiException(errorMessage)

class MethodNotAllowedException(errorMessage: String?) : ApiException(errorMessage)

class InternalServerErrorException(errorMessage: String?) : ApiException(errorMessage)

class NotImplementedException(errorMessage: String?) : ApiException(errorMessage)

class BadGatewayException(errorMessage: String?) : ApiException(errorMessage)

class ServiceUnavailableException(errorMessage: String?) : ApiException(errorMessage)
