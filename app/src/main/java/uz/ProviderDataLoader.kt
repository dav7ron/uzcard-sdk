package uz

import android.content.Context
import android.net.Uri
import android.util.Log

class ProviderDataLoader(private val context: Context) {

    fun loadPhoneNumberFromUzcardPay(): String? {
        val uri = Uri.parse(Constants.URI_PHONENUMBER)
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("phoneNumber")
                val phoneNumber = it.getString(columnIndex)
                return if (phoneNumber.isNullOrEmpty()) null else phoneNumber
            }
        }
        return null  // when phone number not found or cursor is null
    }

    fun loadCardNumbersUzcardPay(): List<String>? {
        val uri = Uri.parse(Constants.URI_CARDSNUMBER)
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("cardNumbers")
                val cardNumbersFromUzcardPay = it.getString(columnIndex)
                return cardNumbersFromUzcardPay.split(";")
            }
        }
        return null
    }
}