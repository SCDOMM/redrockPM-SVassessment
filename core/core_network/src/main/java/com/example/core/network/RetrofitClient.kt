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
        val newRequest = if (original.url.encodedPath.contains("get_search_result_v2")) {
            original.newBuilder()
               .header("Cookie", "ky_udid=58d1cf919db5480fbf33d4e306642a4e;ky_auth=;APPID=ahpagrcrf2p7m6rg;PHPSESSID=fb950631740ecf259abd1519ed26d1c3")
                .build()
        } else {
            original
        }
        chain.proceed(newRequest)
    }
    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()
        val path = original.url.encodedPath
        if (path.contains("/v1/card/") || path.contains("/v1/content/") || path.contains("/v3/lightTopics/")) {
            val ua = "EYEPETIZER/7090000 (25019PNF3C;android;16;zh_CN;android;7.9.0;cn-bj;huawei;cffcf01d6faf3dcdb1aba83e769d3a5b;WIFI;1440*2971) native/1.0"
            val request = original.newBuilder()
                .header("x-api-key", "0530ee4341324ce2b26c23fcece80ea2")
                .header("X-THEFAIR-APPID", "ahpagrcrf2p7m6rg")
                .header("X-THEFAIR-AUTH", "rZDEwH+a/Lx9QsC2VDBSb0NWsHhTz7jpMQxcWt1L9E0wr13VyMyDpIRkOt9XNklP7MnCAxHh8Az1c+gz7M/lm/3FOGQWqf+lr/gHuOmp4+yBwrsmVnGBW6Bk8zkvstBJEG6B22QYwD2kjH+IhRRJ2zubGUTGWmnL6/yyxY1NokXdxggrffV71BGqTjCEPRrl3dCaJin6iQUQyxSJW4kVhszNOFgc/ftBQepeErMxwtsKSVtuzCxiTm6x5szx706TONgaQ33KtEPVI/5KUx1vBQ==")
                .header("X-THEFAIR-CID", "cffcf01d6faf3dcdb1aba83e769d3a5b")
                .header("X-THEFAIR-UA", ua)
                .header("User-Agent", ua)
                .header("Cookie", "ky_udid=275d0202c9e74b93a4d5c417f131cf8c;ky_auth=_V1MTkyMzQzMjkwMjA6MTgxNTQwNTA0MTY5MjoyMDdjOGFlNzkxNTg0NWY0ZWE5NzUzYTA0YmMyZDUwOQ;APPID=ahpagrcrf2p7m6rg;PHPSESSID=24e472ac022a90fe114f68e3f6df6e13")
                .build()
            chain.proceed(request)
        } else {
            chain.proceed(original)
        }
    }
    private val commonParamsInterceptor = Interceptor { chain ->
        val original = chain.request()
        // POST 请求不添加 query 参数（参数通过 @Field 在 body 中传递）
        if (original.method == "POST") {
            chain.proceed(original)
        } else {
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
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(commonParamsInterceptor)
        .addInterceptor(apiKeyInterceptor)
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
        .setLenient()
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
