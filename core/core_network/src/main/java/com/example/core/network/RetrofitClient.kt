package com.example.core.network

import com.example.core.network.NetworkConfig.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * description ： retrofit客户端
 * email : 3014386984@qq.com
 * date : 2026/7/14 17:57
 */
object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val cookieInterceptor = Interceptor { chain ->
        val original = chain.request()
        val newRequest = original.newBuilder()
            .header("x-api-key", NetworkConfig.X_API_KEY)
            .header("X-THEFAIR-APPID", NetworkConfig.X_THEFAIR_APPID)
            .header("X-THEFAIR-AUTH", NetworkConfig.X_THEFAIR_AUTH)
            .header("X-THEFAIR-CID", NetworkConfig.X_THEFAIR_CID)
            .header("X-THEFAIR-UA", NetworkConfig.X_THEFAIR_UA)
            .header("User-Agent", NetworkConfig.USER_AGENT)
            .header("Cookie", NetworkConfig.COOKIE)
            .build()
        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor ( cookieInterceptor )
        .followRedirects(true)
        .followSslRedirects(true)
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    suspend fun resolvePlayUrl(redirectUrl: String): String {
        return try {
            val client = okHttpClient.newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
            val request = okhttp3.Request.Builder().url(redirectUrl).build()
            val response = client.newCall(request).execute()
            response.request.url.toString()
        } catch (e: Exception) {
            redirectUrl
        }
    }
}
