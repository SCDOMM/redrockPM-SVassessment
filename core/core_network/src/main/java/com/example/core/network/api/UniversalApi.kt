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
interface UniversalApi {
    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPage(
        @Field("page_label") pageLabel: String,
        @Field("page_type") pageType: String = "card",
    ): Call<ApiResponse< PageResult>>

    @GET("v1/common/notice/get_push_list")
    fun getPushList(
        @Query("last_item_id") lastItemId: String
    ): Call<ApiResponse<PaginatedResult<NoticeItem>>>

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

    @GET("v1/user/center/get_user_info")
    fun getUserInfo(
        @Query("uid") uid: String,
        @Query("user_type") userType: String = ""
    ): Call<ApiResponse<UserInfo>>
}