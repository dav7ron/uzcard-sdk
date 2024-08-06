package uz.connection

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiError(
    var code: Int? = 0, var message: String? = null
) : Parcelable {

    companion object {
        fun parse(error: Throwable): ApiError {
            return try {
                Gson().fromJson(error.message, ApiError::class.java)
            } catch (e: Exception) {
                ApiError(message = error.message)
            }
        }
    }
}