package uz.connection

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.RawValue

data class ApiResponse<T>(
    var result: @RawValue T?, var error: ApiError?
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        parcel.readValue(ApiResponse<T>::result::class.java.classLoader) as T,
        parcel.readParcelable(ApiError::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(result)
        parcel.writeParcelable(error, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ApiResponse<*>> {
        override fun createFromParcel(parcel: Parcel): ApiResponse<*> {
            return ApiResponse<Any>(parcel)
        }

        override fun newArray(size: Int): Array<ApiResponse<*>?> {
            return arrayOfNulls(size)
        }
    }
}