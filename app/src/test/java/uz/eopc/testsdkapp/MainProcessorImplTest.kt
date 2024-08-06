package uz.eopc.testsdkapp

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.Mockito.*
import uz.Constants
import uz.MainProcessorImpl
import uz.ProviderDataLoader
import uz.StateStatus

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class MainProcessorImplTest {

    private lateinit var mainProcessor: MainProcessorImpl

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var cursor: Cursor
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dataLoader: ProviderDataLoader


    @Before
    fun setup() {
        context = mock(Context::class.java)
        contentResolver = mock(ContentResolver::class.java)
        dataLoader = mock(ProviderDataLoader::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        cursor = mock(Cursor::class.java)
        `when`(context.createPackageContext(anyString(), anyInt())).thenReturn(context)
        `when`(context.contentResolver).thenReturn(contentResolver)
        mainProcessor = MainProcessorImpl(context, dataLoader)
    }

    @Test
    fun testCheckPhoneNumberStatus_PhoneNumberMatched() {
        val phoneNumber = "+998977771358"
        val phoneNumberUri = Uri.parse(Constants.URI_PHONENUMBER)
        doReturn(cursor).`when`(contentResolver).query(eq(phoneNumberUri), isNull(), isNull(), isNull(), isNull())
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex("phoneNumber")).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn(phoneNumber)
        `when`(dataLoader.loadPhoneNumberFromUzcardPay()).thenReturn(phoneNumber)

        val result = mainProcessor.checkPhoneNumberStatus(phoneNumber)

        assertEquals(StateStatus.PHONE_NUMBER_MATCHED, result)
    }


    @Test
    fun testCheckPhoneNumberStatus_RealPhoneNumber_NotMatched() {
        val realPhoneNumber = "+998911122233"
        val inputPhoneNumber = "+998901234567"
        val phoneNumberUri = Uri.parse(Constants.URI_PHONENUMBER)

        `when`(contentResolver.query(eq(phoneNumberUri), isNull(), isNull(), isNull(), isNull())).thenReturn(cursor)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex("phoneNumber")).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn(realPhoneNumber)

        `when`(dataLoader.loadPhoneNumberFromUzcardPay()).thenReturn(realPhoneNumber)

        val result = mainProcessor.checkPhoneNumberStatus(inputPhoneNumber)

        assertEquals(StateStatus.PHONE_NUMBER_NOT_MATCHED, result)
    }

    @Test
    fun checkCardNumberStatus_CardTokenized() {
        val cardNumbers = listOf("544081******1438", "123456******7890")
        val maskedPan = "123456******7890"
        val cardNumberUri = Uri.parse(Constants.URI_CARDSNUMBER)
        `when`(context.contentResolver.query(cardNumberUri, null, null, null, null)).thenReturn(cursor)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex("cardNumbers")).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn("544081******1438;123456******7890")
        `when`(dataLoader.loadCardNumbersUzcardPay()).thenReturn(cardNumbers)

        val result = mainProcessor.checkCardNumberStatus(maskedPan)

        assertEquals(StateStatus.CARD_TOKENIZED, result)
    }

}