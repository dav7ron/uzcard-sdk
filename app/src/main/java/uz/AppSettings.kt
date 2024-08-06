package uz

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.NameNotFoundException
import android.provider.Settings
import android.util.Log
import uz.eopc.testsdkapp.BuildConfig

class AppSettings(private val context: Context) {

    private val prefs =
        context.getSharedPreferences(Constants.Prefs.PREFS_FILE_NAME, Context.MODE_PRIVATE)
    private val publicPrefs = getPublicSharedPref()

//    val appVersion: String get() = BuildConfig.VERSION_NAME

    val deviceId: String
        @SuppressLint("HardwareIds") get() = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ANDROID_ID
        )

    var language: String
        get() = prefs.getString(Constants.Prefs.APP_LANGUAGE, "ru") ?: "ru"
        set(value) = setField(Constants.Prefs.APP_LANGUAGE, value)

    var registeredPhoneNumber: String
        get() = publicPrefs?.getString(Constants.Prefs.PUBLIC_PHONE_NUMBER, "") ?: ""
        set(value) = setField(Constants.Prefs.PUBLIC_PHONE_NUMBER, value)

    private fun setField(key: String, value: Any?) {
        val editor = prefs.edit()

        if (value != null) {
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is String -> editor.putString(key, value)
                else -> throw IllegalArgumentException("This type not supported")
            }
        } else {
            editor.remove(key)
        }

        editor.apply()
    }

    private fun getPublicSharedPref(): SharedPreferences? {
        return try {
            context.createPackageContext(Constants.PACKAGE_NAME, 0).getSharedPreferences(
                Constants.Prefs.PUBLIC_PREFS_NAME, Context.MODE_PRIVATE
            )
        } catch (e: NameNotFoundException) {
            Log.e("Not data shared", e.toString())
            null
        }
    }
}