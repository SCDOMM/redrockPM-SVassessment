package com.example.core.network.api

import com.example.core.model.EyepetizerResponse
import com.example.core.model.RankListResponse
import com.example.core.model.TabListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * description ： api接口
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
interface KaiyanApi {
    // ========== 首页相关 ==========
    @GET("v5/index/tab/list")
     fun getTabList(): Call<TabListResponse>

    @GET("v5/index/tab/{tabId}")
    fun getTabDetail(
        @Path("tabId") tabId: String,
        @Query("page") page: Int = 0,
    ): Call<EyepetizerResponse>

    @GET
    fun getTabDetailByUrl(@Url url: String): Call<EyepetizerResponse>

    @GET("v7/index/tab/discovery")
    fun getDiscovery(): Call<EyepetizerResponse>

    @GET("v5/category/list")
    fun getCategoryList(): Call<EyepetizerResponse>

    @GET("v4/categories/all")
    fun getAllCategories(): Call<EyepetizerResponse>

    // ========== 排行榜 ==========
    @GET("v4/rankList")
     fun getRankListTabs(): Call<RankListResponse>

    @GET("v4/rankList/videos")
     fun getRankList(
        @Query("strategy") strategy: String = "historical"
    ): Call<EyepetizerResponse>

    @GET
     fun getRankListByUrl(@Url url: String): Call<EyepetizerResponse>

    // ========== 分类/标签相关 ==========
    @GET("v6/tag/index")
     fun getTagIndex(@Query("id") tagId: Int): Call<EyepetizerResponse>

    @GET("v6/tag/dynamics")
    fun getTagDynamics(
        @Query("id") tagId: Int,
        @Query("page") page: Int = 0
    ): Call<EyepetizerResponse>

    @GET("v1/tag/videos")
     fun getTagVideos(
        @Query("id") tagId: Int,
        @Query("page") page: Int = 0
    ): Call<EyepetizerResponse>

    // ========== 专题相关 ==========
    @GET("v3/specialTopics")
     fun getSpecialTopics(): Call<EyepetizerResponse>

    @GET("v3/lightTopics/internal/{topicId}")
     fun getTopicDetail(@Path("topicId") topicId: Int): Call<EyepetizerResponse>

    // ========== 社区相关 ==========
    @GET("v7/community/tab/rec")
     fun getCommunityRec(): Call<EyepetizerResponse>

    @GET("v5/community/tab/list")
     fun getCommunityTabList(): Call<EyepetizerResponse>

    @GET("v6/community/tab/follow")
     fun getFollowList(@Query("page") page: Int = 0): Call<EyepetizerResponse>

    @GET("v5/community/tab/dynamicFeeds")
     fun getDynamicFeeds(@Query("page") page: Int = 0): Call<EyepetizerResponse>

    // ========== 通知相关 ==========
    @GET("v3/messages/tabList")
     fun getMessageTabList(): Call<EyepetizerResponse>

    @GET("v3/messages")
     fun getMessages(
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): Call<EyepetizerResponse>

    @GET("v7/tag/tabList")
    fun getTagTabList(): Call<EyepetizerResponse>

    @GET("v7/topic/list")
    fun getTopicList(@Query("page") page: Int = 0): Call<EyepetizerResponse>

    // ========== 视频相关 ==========
    @GET("v4/video/related")
    fun getVideoRelated(@Query("id") videoId: Long): Call<EyepetizerResponse>

    @GET("v2/replies/video")
    fun getVideoReplies(
        @Query("videoId") videoId: Long,
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): Call<EyepetizerResponse>

    @GET("v1/playUrl")
    fun getPlayUrl(
        @Query("vid") vid: Long,
        @Query("resourceType") resourceType: String = "video",
        @Query("editionType") editionType: String = "default",
        @Query("source") source: String = "aliyun"
    ): Call<EyepetizerResponse>

    // ========== 搜索相关 ==========
    @GET("v3/queries/hot")
    fun getHotQueries(): Call<List<String>>

    @GET("v3/search")
    fun search(
        @Query("query") query: String,
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): Call<EyepetizerResponse>

    // ========== 日历相关 ==========
    @GET("v7/roamingCalendar/index")
    fun getCalendar(
        @Query("date") date: String
    ): Call<EyepetizerResponse>
}