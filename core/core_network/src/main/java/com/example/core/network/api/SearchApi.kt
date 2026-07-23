package com.example.core.network.api

import com.example.core.model.ApiResponse
import com.example.core.model.MetroItem
import com.example.core.model.NoticeItem
import com.example.core.model.PageResult
import com.example.core.model.PaginatedResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**   
 * 包名称： com.example.core.network.api
 * 类名称：SearchApi
 * 类描述：搜索专用的API接口
 * 创建人：韦西波
 * 创建时间：2026-07-23 10:58
 *
 */
interface SearchApi {
    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun search(
        @Field("query") query: String,
        @Field("start") start: Int = 0,
        @Field("num") num: Int = 10
    ): Call<ApiResponse<PaginatedResult<PageResult>>>

    @FormUrlEncoded
    @POST("v1/search/search/get_search_result_v2")
    fun searchLoad(
        @Field("query") query: String,
        @Field("type") type: String,          // "video", "graphic" 等
        @Field("last_item_id") lastItemId: String,
        @Field("num") num: Int = 10,
    ): Call<ApiResponse<PaginatedResult<MetroItem>>>

    @GET("v1/recommend/search/get_pre_search")
    fun getPreSearch(
        @Query("query") query: String
    ): Call<ApiResponse<PaginatedResult<String>>>
    @FormUrlEncoded
    @POST("v1/search/search/get_search_recommend_card_list")
    fun getWeeklyRank(@Field("token") token: String = ""): Call<ApiResponse<PageResult>>

    @GET("v1/recommend/search/get_hot_queries")
    fun getHotQueries(@Query("token") token: String = ""): Call<ApiResponse<PaginatedResult<String>>>
}