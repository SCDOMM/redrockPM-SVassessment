package com.example.core.network.api

import com.example.core.model.EyepetizerResponse
import com.example.core.model.RankListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface KaiyanApi {
    // ========== 首页相关 ==========

    @GET("v5/index/tab/list")
    suspend fun getTabList(): EyepetizerResponse

    @GET("v5/index/tab/{tabId}")
    suspend fun getTabDetail(
        @Path("tabId") tabId: String,
        @Query("page") page: Int = 0
    ): EyepetizerResponse

    @GET("v7/index/tab/discovery")
    suspend fun getDiscovery(): EyepetizerResponse

    @GET("v5/category/list")
    suspend fun getCategoryList(): EyepetizerResponse

    @GET("v4/categories/all")
    suspend fun getAllCategories(): EyepetizerResponse

    // ========== 排行榜 ==========

    @GET("v4/rankList")
    suspend fun getRankListTabs(): RankListResponse

    @GET("v4/rankList/videos")
    suspend fun getRankList(
        @Query("strategy") strategy: String = "historical"
    ): EyepetizerResponse

    @GET
    suspend fun getRankListByUrl(@Url url: String): EyepetizerResponse

    // ========== 分类/标签相关 ==========

    @GET("v6/tag/index")
    suspend fun getTagIndex(@Query("id") tagId: Int): EyepetizerResponse

    @GET("v6/tag/dynamics")
    suspend fun getTagDynamics(
        @Query("id") tagId: Int,
        @Query("page") page: Int = 0
    ): EyepetizerResponse

    @GET("v1/tag/videos")
    suspend fun getTagVideos(
        @Query("id") tagId: Int,
        @Query("page") page: Int = 0
    ): EyepetizerResponse

    // ========== 专题相关 ==========

    @GET("v3/specialTopics")
    suspend fun getSpecialTopics(): EyepetizerResponse

    @GET("v3/lightTopics/internal/{topicId}")
    suspend fun getTopicDetail(@Path("topicId") topicId: Int): EyepetizerResponse

    // ========== 社区相关 ==========

    @GET("v7/community/tab/rec")
    suspend fun getCommunityRec(): EyepetizerResponse

    @GET("v5/community/tab/list")
    suspend fun getCommunityTabList(): EyepetizerResponse

    @GET("v6/community/tab/follow")
    suspend fun getFollowList(@Query("page") page: Int = 0): EyepetizerResponse

    @GET("v5/community/tab/dynamicFeeds")
    suspend fun getDynamicFeeds(@Query("page") page: Int = 0): EyepetizerResponse

    // ========== 通知相关 ==========

    @GET("v3/messages/tabList")
    suspend fun getMessageTabList(): EyepetizerResponse

    @GET("v3/messages")
    suspend fun getMessages(
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): EyepetizerResponse

    @GET("v7/tag/tabList")
    suspend fun getTagTabList(): EyepetizerResponse

    @GET("v7/topic/list")
    suspend fun getTopicList(@Query("page") page: Int = 0): EyepetizerResponse

    // ========== 视频相关 ==========

    @GET("v4/video/related")
    suspend fun getVideoRelated(@Query("id") videoId: Long): EyepetizerResponse

    @GET("v2/replies/video")
    suspend fun getVideoReplies(
        @Query("videoId") videoId: Long,
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): EyepetizerResponse

    @GET("v1/playUrl")
    suspend fun getPlayUrl(
        @Query("vid") vid: Long,
        @Query("resourceType") resourceType: String = "video",
        @Query("editionType") editionType: String = "default",
        @Query("source") source: String = "aliyun"
    ): EyepetizerResponse

    // ========== 搜索相关 ==========

    @GET("v3/queries/hot")
    suspend fun getHotQueries(): List<String>

    @GET("v3/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("start") start: Int = 0,
        @Query("num") num: Int = 10
    ): EyepetizerResponse

    // ========== 日历相关 ==========

    @GET("v7/roamingCalendar/index")
    suspend fun getCalendar(
        @Query("date") date: String
    ): EyepetizerResponse
}
