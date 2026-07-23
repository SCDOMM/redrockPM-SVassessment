package com.example.core.network.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * description ： api接口
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
interface SpecficApi {

    @GET("http://baobab.kaiyanapp.com/api/v3/lightTopics/internal/{topicId}")
    fun getTopicDetailRaw(@Path("topicId") topicId: Int): Call<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPageRaw(
        @Field("page_type") pageType: String = "card",
        @Field("page_label") pageLabel: String,
        @Field("udid") udid: String = "275d0202c9e74b93a4d5c417f131cf8c",
        @Field("vc") vc: String = "7090000",
        @Field("vn") vn: String = "7.9.0",
        @Field("deviceModel") deviceModel: String = "25019PNF3C",
        @Field("first_channel") firstChannel: String = "huawei",
        @Field("size") size: String = "1440X2971",
        @Field("system_version_code") systemVersionCode: String = "36",
        @Field("token") token: String = ""
    ): Call<okhttp3.ResponseBody>

}