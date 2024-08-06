package uz.connection

import android.annotation.SuppressLint
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import uz.AppSettings
import uz.Constants
import uz.eopc.testsdkapp.BuildConfig
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class NetworkUtil(private val appSettings: AppSettings) {

    private fun getHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor(appSettings)
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private fun getTrustedManager(): Array<TrustManager> {
        return arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>, authType: String
            ) {
            }
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>, authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })
    }

    private fun getSslContext(): SSLContext? {
        val trustAllCerts = getTrustedManager()
        var sslContext: SSLContext? = null

        try {
            sslContext = SSLContext.getInstance("SSL")
            sslContext?.init(null, trustAllCerts, java.security.SecureRandom())

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return sslContext
    }

    private fun getSslSocketFactory(): SSLSocketFactory? {
        val sslContext = getSslContext()
        return sslContext?.socketFactory
    }

    fun getOkHttpClient(): OkHttpClient {

        val sslSocketFactory = getSslSocketFactory()
        val trustAllCerts = getTrustedManager()
        val headerInterceptor = getHeaderInterceptor()
        val httpLoggingInterceptor = getHttpLoggingInterceptor()

        val httpClientBuilder = if (null != sslSocketFactory && trustAllCerts.isNotEmpty()) {
            OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .addNetworkInterceptor(httpLoggingInterceptor).addInterceptor(headerInterceptor)
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)

        } else {
            OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .addNetworkInterceptor(httpLoggingInterceptor).addInterceptor(headerInterceptor)
        }

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return httpClientBuilder.build()
    }

    class HeaderInterceptor(private val appSettings: AppSettings) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequestBuilder = chain.request().newBuilder()
            val lanKey = appSettings.language
            val deviceId = appSettings.deviceId
//            val version = appSettings.appVersion

            // Adding locale headers
            newRequestBuilder.addHeader("Content-Type", "application/json")
            newRequestBuilder.addHeader("Accept-Language", lanKey)

            newRequestBuilder.addHeader(Constants.Headers.DEVICE_ID, deviceId)
//            newRequestBuilder.addHeader(Constants.Headers.VERSION, version)

//            val response = chain.proceed(newRequestBuilder.build())
//            val newAuthToken = response.header(Constants.Headers.AUTHORIZATION)

//            if (response.isSuccessful && !newAuthToken.isNullOrBlank()) {
//                appSettings.authToken = newAuthToken
//            }

            return chain.proceed(newRequestBuilder.build())
        }
    }

    fun getRestApi(): RetrofitRestApi {
        val okHttpClient = getOkHttpClient()
        val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(okHttpClient).build()

        return retrofit.create(RetrofitRestApi::class.java)
    }


}