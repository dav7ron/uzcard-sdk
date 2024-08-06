package uz

object Constants {

    const val DISPLAY_WIDTH: String = "display_width"

    const val requestCode: Int = 123
    const val googlePlay: String =
        "https://play.google.com/store/apps/details?id=uz.uzcardpay.android"
    const val URI_PHONENUMBER: String = "content://uz.uzcardpay.android.provider/phoneNumber"
    const val URI_CARDSNUMBER: String = "content://uz.uzcardpay.android.provider/cardNumbers"

    const val URI_PERMISSION: String = "uz.uzcardpay.android.PERMISSION_READ_DATA"

    const val BASE_URL: String = "https://185.178.51.26:2021/api/"
    const val PACKAGE_NAME: String = "uz.uzcardpay.android"

    object Headers {
        const val DEVICE_ID = "device_id"
        const val VERSION = "version"
    }

    object Prefs {
        const val PREFS_FILE_NAME = "app.settings"
        const val APP_LANGUAGE = "appLanguage"
        const val PUBLIC_PHONE_NUMBER = "publicPhoneNumber"
        const val PUBLIC_PREFS_NAME: String = "AppPrefsPublic"
    }

}