package uz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.Constants.googlePlay
import uz.Constants.requestCode
import uz.connection.runAsync

class MainProcessorImpl(
    private val context: Context,
    private val dataLoader: ProviderDataLoader
) : MainProcessor {

    override fun startProcess(
        sdkId: String,
        deviceId: String,
        phoneNumber: String,
        maskedPan: String,
        listener: MainProcessorErrorListener,
    ) {
        if ( sdkId.isBlank() || deviceId.isBlank() || phoneNumber.isBlank() || maskedPan.isBlank() ) {
            listener.onErrorListener(StateStatus.SOME_PROPS_ARE_EMPTY)
            return
        }

        checkStatus(deviceId, phoneNumber, maskedPan, object: MainProcessorErrorListener {
            override fun onErrorListener(status: StateStatus) {
                when(status) {
                    StateStatus.USER_NOT_REGISTERED -> {
                        startTokenization(sdkId, phoneNumber, UserStatus.NEW_USER)
                    }

                    StateStatus.CARD_NOT_TOKENIZED -> {
                       startTokenization(sdkId, phoneNumber, UserStatus.OLD_USER)
                    }

                    StateStatus.PHONE_NUMBER_NOT_MATCHED -> {
                        listener.onErrorListener(StateStatus.PHONE_NUMBER_NOT_MATCHED)
                    }

                    StateStatus.SOME_PROPS_ARE_EMPTY -> {
                        listener.onErrorListener(StateStatus.SOME_PROPS_ARE_EMPTY)
                    }

                    StateStatus.CARD_TOKENIZED -> {
                        listener.onErrorListener(StateStatus.CARD_TOKENIZED)
                    }

                    StateStatus.PHONE_NUMBER_MATCHED -> {
                        listener.onErrorListener(StateStatus.PHONE_NUMBER_MATCHED)
                    }

                    StateStatus.DEVICE_ID_NOT_MATCHED -> {
                        listener.onErrorListener(StateStatus.DEVICE_ID_NOT_MATCHED )
                    }
                }
            }
        })


    }

    override fun checkStatus(
        deviceId: String,
        phoneNumber: String,
        maskedPan: String,
        listener: MainProcessorErrorListener,
    ) {
        if (deviceId.isBlank() || phoneNumber.isBlank() || maskedPan.isBlank()) {
            listener.onErrorListener(StateStatus.SOME_PROPS_ARE_EMPTY)
        } else {
            val currentDeviceID = provideDeviceId(context)
            Log.i("TAG", "DeviceID: $currentDeviceID")
            if (deviceId == currentDeviceID) {

                if (isAppInstalled()) {
                    PermissionManager.requestPermissions(context) {
                        val status = checkPhoneNumberStatus(phoneNumber)

                        if (status == StateStatus.USER_NOT_REGISTERED) {
                            listener.onErrorListener(StateStatus.USER_NOT_REGISTERED)
                        } else {

                            if (status == StateStatus.PHONE_NUMBER_MATCHED) {
                                val statusCard = checkCardNumberStatus(maskedPan)

                                if (statusCard == StateStatus.CARD_TOKENIZED) {
                                    listener.onErrorListener(StateStatus.CARD_TOKENIZED)
                                } else {
                                    listener.onErrorListener(StateStatus.CARD_NOT_TOKENIZED)
                                }

                            } else {
                                listener.onErrorListener(StateStatus.PHONE_NUMBER_NOT_MATCHED)
                            }
                        }
                    }
                } else {
                    goToGooglePlay()
                }

            } else {
                listener.onErrorListener(StateStatus.DEVICE_ID_NOT_MATCHED)
            }
        }

    }


    private fun isPhoneNumberMatched(phoneNumber: String, phoneNumberFromProvider: String?): Boolean {
        return phoneNumber == phoneNumberFromProvider
    }

    fun checkPhoneNumberStatus(phoneNumber: String): StateStatus {
        val phoneNumberFromProvider = dataLoader.loadPhoneNumberFromUzcardPay()
        if (phoneNumberFromProvider.isNullOrEmpty()) {
            return StateStatus.USER_NOT_REGISTERED
        }
        // Check if the phone numbers match
        return if (isPhoneNumberMatched(phoneNumber, phoneNumberFromProvider)) {
            StateStatus.PHONE_NUMBER_MATCHED
        } else {
            StateStatus.PHONE_NUMBER_NOT_MATCHED
        }
    }

    private fun isCardTokenized(listOfCards: List<String>?, panPrefix: String, panSuffix: String): Boolean {
        listOfCards?.let { cards ->
            if (cards.isEmpty()) {
                return false
            }
            for (card in cards) {
                if (card.isEmpty()) {
                    continue
                }
                val cardPrefix = card.substring(0, minOf(card.length, 6))
                val cardSuffix = card.takeLast(minOf(card.length, 4))

                if (cardPrefix == panPrefix && cardSuffix == panSuffix) {
                    return true
                }
            }
        }
        return false
    }

    fun checkCardNumberStatus(maskedPan: String): StateStatus? {
        val listOfCards = dataLoader.loadCardNumbersUzcardPay()
        val panPrefix = maskedPan.substring(0, 6)
        val panSuffix = maskedPan.takeLast(4)

        return when {
            listOfCards == null -> null
            isCardTokenized(listOfCards, panPrefix, panSuffix) -> StateStatus.CARD_TOKENIZED
            else -> StateStatus.CARD_NOT_TOKENIZED
        }
    }


    private fun isAppInstalled(): Boolean {
        val packageManager: PackageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(Constants.PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    private fun startTokenization(
        sdkId: String,
        phoneNumber: String,
        mode: UserStatus,
    ) {
        runAsync {
            withContext(Dispatchers.Main) {
                if (sdkId.isNotEmpty()) {
                    val resultOfMode = when(mode) {
                        UserStatus.NEW_USER -> "new_user"
                        UserStatus.OLD_USER -> "old_user"
                    }
                    toUzcardPaymentAppApk(sdkId, phoneNumber, resultOfMode)
                } else {
                    Toast.makeText(context, "Error SDK ID is empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toUzcardPaymentAppApk(
        sdkId: String, phoneNumber: String, mode: String
    ) {
        val manager = context.packageManager
        try {
            val intent = manager.getLaunchIntentForPackage(Constants.PACKAGE_NAME)
            intent?.putExtra("sdkId", sdkId)
            intent?.putExtra("phoneNumber", phoneNumber)
            intent?.putExtra("mode", mode)
            intent?.addCategory(Intent.CATEGORY_LAUNCHER)
            (context as Activity).startActivityForResult(intent, requestCode)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            goToGooglePlay()
        }
    }

    private fun goToGooglePlay() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googlePlay))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, "You not have Uzcard Pay", Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("HardwareIds")
    private fun provideDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}