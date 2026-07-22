package com.example.core.network

import com.example.core.model.Item
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
//    private const val BASE_URL = "http://baobab.kaiyanapp.com/api/"
    private const val BASE_URL = "https://api.eyepetizer.net/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val cookieInterceptor = Interceptor { chain ->
        val original = chain.request()
        val newRequest = original.newBuilder()
            .header("x-api-key", "0530ee4341324ce2b26c23fcece80ea2")
            .header("X-THEFAIR-APPID", "ahpagrcrf2p7m6rg")
            .header("X-THEFAIR-AUTH", "uHkwPEjMsV9UKJZpLT1nWjdmiBnNMr9FTqll6Foa5WUQ9sidzNXqwNpRx2t4Xb5IbX4zkYvaTVIb2HuP1My7l0fh0u8bMwrUQOxd6B6yPTzdRsw2QA0n1uCOyqO8vyFBZQPjLgvyf7RjVplheFSbAhvMrDeyHejkkFHWSQgpHTTjb9+to9Z9yzDqJ6dqbuKbe0d6m3GtIY4/nAiPZt9dYSgHqeUlMAMEo4f8a8qqf/JD2kSAPl2a8JPPgTi0egnoOSpi+tHf8dVnZSl8zd0y1A==")
            .header("X-THEFAIR-CID", "12a50409f39708370d69ee9951505c2c")
            .header("X-THEFAIR-UA", "EYEPETIZER/7090000 (V2410A;android;15;zh_CN;android;7.9.0;cn-bj;huawei;12a50409f39708370d69ee9951505c2c;NONE;1080*2163) native/1.0")
            .header("User-Agent", "EYEPETIZER/7090000 (V2410A;android;15;zh_CN;android;7.9.0;cn-bj;huawei;12a50409f39708370d69ee9951505c2c;NONE;1080*2163) native/1.0")
            .header("Cookie", "ky_udid=58d1cf919db5480fbf33d4e306642a4e;ky_auth=;APPID=ahpagrcrf2p7m6rg;PHPSESSID=a8ee7bee9cce9d3c1f8bdb0602d17781")
            .build()
        chain.proceed(newRequest)
    }
    private val commonParamsInterceptor = Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("udid", "435865baacfc49499632ea13c5a78f944c2f28aa")
            .addQueryParameter("vc", "381")
            .addQueryParameter("vn", "4.3")
            .addQueryParameter("deviceModel", "DUK-AL20")
            .addQueryParameter("first_channel", "eyepetizer_360_market")
            .addQueryParameter("last_channel", "eyepetizer_360_market")
            .addQueryParameter("system_version_code", "26")
            .build()
        chain.proceed(original.newBuilder().url(url).build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(commonParamsInterceptor)
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

    val gson = GsonBuilder()
        .registerTypeAdapter(Item::class.java, ItemDeserializer())
        .create()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    /**
     * 解析playUrl的302重定向，获取真实视频地址
     */
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
