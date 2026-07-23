package com.example.core.network.api

import com.example.core.model.ApiResponse
import com.example.core.model.Card
import com.example.core.model.PaginatedResult
import com.example.core.model.MetroItem
import com.example.core.model.NoticeItem
import com.example.core.model.PageResult
import com.example.core.model.UserInfo
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * description ： api接口
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
interface KaiyanApi {
    @GET("v1/user/center/get_user_info")
    fun getUserInfo(
        @Query("uid") uid: Long,
        @Query("user_type") userType: String = ""
    ): Call<ApiResponse<UserInfo>>

    @GET("v1/recommend/search/get_hot_queries")
    fun getHotQueries(
        @Query("udid") udid: String = "58d1cf919db5480fbf33d4e306642a4e",
        @Query("vc") vc: String = "7090000",
        @Query("vn") vn: String = "7.9.0",
        @Query("deviceModel") deviceModel: String = "V2410A",
        @Query("size") size: String = "1080X2163",
        @Query("first_channel") firstChannel: String = "huawei",
        @Query("last_channel") lastChannel: String = "huawei",
        @Query("system_version_code") systemVersionCode: String = "35",
        @Query("token") token: String = ""
    ): Call<ApiResponse<PaginatedResult<String>>>

    @GET("v1/common/notice/get_push_list")
    fun getPushList(
        @Query("last_item_id") lastItemId: String
    ): Call<ApiResponse<PaginatedResult<NoticeItem>>>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun search(
        @Field("query") query: String,
        @Field("start") start: Int = 0,
        @Field("num") num: Int = 10
    ): Call<ApiResponse<PaginatedResult<PageResult>>>

    @GET("v1/recommend/search/get_pre_search")
    fun getPreSearch(
        @Query("query") query: String
    ): Call<ApiResponse<PaginatedResult<String>>>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_recommend_card_list")
    fun getWeeklyRank(
        @Field("udid") udid: String = "58d1cf919db5480fbf33d4e306642a4e",
        @Field("vc") vc: String = "7090000",
        @Field("vn") vn: String = "7.9.0",
        @Field("deviceModel") deviceModel: String = "V2410A",
        @Field("first_channel") firstChannel: String = "huawei",
        @Field("size") size: String = "1080X2163",
        @Field("system_version_code") systemVersionCode: String = "35",
        @Field("token") token: String = ""
    ): Call<ApiResponse<PageResult>>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun searchLoad(
        @Field("query") query: String,
        @Field("type") type: String,          // "video", "graphic" 等
        @Field("last_item_id") lastItemId: String,
        @Field("num") num: Int = 10,
    ): Call<ApiResponse<PaginatedResult<MetroItem>>>

    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPage(
        @Field("page_label") pageLabel: String,
        @Field("page_type") pageType: String = "card",
    ): Call<ApiResponse< PageResult>>

    @FormUrlEncoded
    @POST("v1/card/card/call_card_list")
    fun loadMoreAlbum(
        @Field("last_item_id") lastItemId: String,
        @Field("card_list") cardList: String,
        @Field("page_label") pageLabel: String = "user_center_album",
        @Field("version") version: Int = 1
    ): Call<ApiResponse<PaginatedResult<Card>>>


    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun getMorePage(
        @Field("data_source") dataSource: String="",
        @Field("page_label") pageLabel: String="",
        @Field("material") materialJSON: String,
        @Field("card") cardJSON: String="",
        @Field("last_item_id") lastItemId: String,
    ): Call<ApiResponse<PaginatedResult<MetroItem>>>
}