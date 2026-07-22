package com.example.core.network.api

import com.example.core.model.CallMetroListResponse
import com.example.core.model.HomePageResponse
import com.example.core.model.NoticeResponse
import com.example.core.model.PreSearchResponse
import com.example.core.model.SearchResponse
import com.example.core.model.SearchResponseV2
import com.example.core.model.CallAlbumCardListResponse
import com.example.core.model.CallWorkMetroListResponse
import com.example.core.model.UserCenterResponse
import com.example.core.model.UserWorkResponse
import com.example.core.model.WeeklyRankResponse
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
    ): Call<UserCenterResponse>

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
    ): Call<PreSearchResponse>

    @GET("v1/common/notice/get_push_list")
    fun getPushList(
        @Query("last_item_id") lastItemId: Int
    ): Call<NoticeResponse>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun search(
        @Field("query") query: String,
        @Field("start") start: Int = 0,
        @Field("num") num: Int = 10
    ): Call<SearchResponse>

    @GET("v1/recommend/search/get_pre_search")
    fun getPreSearch(
        @Query("query") query: String
    ): Call<PreSearchResponse>

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
    ): Call<WeeklyRankResponse>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun searchLoad(
        @Field("query") query: String,
        @Field("type") type: String,          // "video", "graphic" 等
        @Field("last_item_id") lastItemId: Int,
        @Field("num") num: Int = 10,
    ): Call<SearchResponseV2>

    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPage(
        @Field("page_label") pageLabel: String,
        @Field("page_type") pageType: String = "card",
    ): Call<HomePageResponse>

    @FormUrlEncoded
    @POST("v1/card/card/call_card_list")
    fun loadMoreAlbum(
        @Field("last_item_id") lastItemId: Int,
        @Field("card_list") cardList: String,
        @Field("page_label") pageLabel: String = "user_center_album",
        @Field("version") version: Int = 1
    ): Call<CallAlbumCardListResponse>

    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getWorkPage(
        @Field("page_label") pageLabel: String,
        @Field("page_type") pageType: String = "card",
    ): Call<UserWorkResponse>
    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun loadMoreWork(
        @Field("card_index") cardIndex: Int,
        @Field("material") material: String,
        @Field("material_index") materialIndex: Int,
        @Field("last_item_id") lastItemId: String,
        @Field("material_relative_index") materialRelativeIndex: Int = 0,
        @Field("page_label") pageLabel: String,
        @Field("card") card: String,
        @Field("data_source") dataSource: String = "home_user_work_list"
    ): Call<CallWorkMetroListResponse>

    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun getMoreHomePage(
        @Field("page_label") pageLabel: String ,
        @Field("data_source") dataSource: String="",
        @Field("material") material: String,
        @Field("card") card: String,
        @Field("last_item_id") lastItemId: Int
    ): Call<CallMetroListResponse>
    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun getMoreDailyPage(
        @Field("data_source") dataSource: String = "history_issue_feed",
        @Field("material") material: String,
        @Field("last_item_id") lastItemId: Int
    ): Call<CallMetroListResponse>
}