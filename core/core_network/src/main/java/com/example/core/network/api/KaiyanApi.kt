package com.example.core.network.api

import com.example.core.model.CallMetroListResponse
import com.example.core.model.EyepetizerResponse
import com.example.core.model.GetNavResponse
import com.example.core.model.GetPageResponse
import com.example.core.model.LightTopicsResponse
import com.example.core.model.ItemDetailResponse
import com.example.core.model.PreSearchResponse
import com.example.core.model.RelatedRecommendResponse
import com.example.core.model.RankListResponse
import com.example.core.model.SearchResponse
import com.example.core.model.SearchResponseV2
import com.example.core.model.TabListResponse
import com.example.core.model.WeeklyRankResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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
        @Query("page") page: Int = 0
    ): Call<EyepetizerResponse>

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

    @GET
    fun getTabDetailByUrl(@Url url: String): Call<EyepetizerResponse>

    @GET("v7/index/tab/discovery")
    fun getDiscovery(): Call<EyepetizerResponse>

    @GET("v5/category/list")
    fun getCategoryList(): Call<EyepetizerResponse>

    @GET("v4/categories/all")
    fun getAllCategories(): Call<EyepetizerResponse>

    @GET("v5/index/tab/category/{categoryId}")
    fun getCategoryDetail(
        @Path("categoryId") categoryId: Int,
        @Query("udid") udid: String = "435865baacfc49499632ea13c5a78f944c2f28aa"
    ): Call<EyepetizerResponse>

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
        @Query("page") page: Int = 0,
        @Query("udid") udid: String = "435865baacfc49499632ea13c5a78f944c2f28aa"
    ): Call<EyepetizerResponse>

    @GET("v1/tag/videos")
    fun getTagVideos(
        @Query("id") tagId: Int,
        @Query("page") page: Int = 0
    ): Call<EyepetizerResponse>

    // ========== 专题相关 ==========
    @GET("v3/specialTopics")
    fun getSpecialTopics(): Call<EyepetizerResponse>

    @GET("http://baobab.kaiyanapp.com/api/v3/lightTopics/internal/{topicId}")
    fun getTopicDetail(@Path("topicId") topicId: Int): Call<LightTopicsResponse>

    @GET("http://baobab.kaiyanapp.com/api/v3/lightTopics/internal/{topicId}")
    fun getTopicDetailRaw(@Path("topicId") topicId: Int): Call<okhttp3.ResponseBody>

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
    fun getTagTabList(): Call<TabListResponse>

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

    @GET("v1/content/item/get_item_detail_v2")
    fun getItemDetail(
        @Query("resource_id") resourceId: String,
        @Query("resource_type") resourceType: String = "pgc_video"
    ): Call<ItemDetailResponse>

    @FormUrlEncoded
    @POST("v1/content/item/get_related_recommend")
    fun getRelatedRecommend(
        @Field("resource_id") resourceId: String,
        @Field("resource_type") resourceType: String = "pgc_video"
    ): Call<RelatedRecommendResponse>

    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun callMetroListV2(
        @Field("card_index") cardIndex: Int,
        @Field("material") material: String,
        @Field("last_item_id") lastItemId: String,
        @Field("page_params") pageParams: String,
        @Field("page_label") pageLabel: String,
        @Field("card") card: String,
        @Field("data_source") dataSource: String
    ): Call<CallMetroListResponse>

    // ========== 搜索相关 ==========
    @GET("v3/queries/hot")
    fun getHotQueries(): Call<List<String>>

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



    // ========== 日历相关 ==========
    @GET("v7/roamingCalendar/index")
    fun getCalendar(
        @Query("date") date: String
    ): Call<EyepetizerResponse>

    // ========== 新版卡片页面 ==========
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

    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPage(
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
    ): Call<GetPageResponse>

    @FormUrlEncoded
    @POST("v1/card/page/get_nav")
    fun getNav(
        @Field("tab_label") tabLabel: String,
        @Field("page_type") pageType: String = "card",
        @Field("udid") udid: String = "275d0202c9e74b93a4d5c417f131cf8c",
        @Field("vc") vc: String = "7090000",
        @Field("vn") vn: String = "7.9.0",
        @Field("deviceModel") deviceModel: String = "25019PNF3C",
        @Field("first_channel") firstChannel: String = "huawei",
        @Field("size") size: String = "1440X2971",
        @Field("system_version_code") systemVersionCode: String = "36",
        @Field("token") token: String = ""
    ): Call<GetNavResponse>
}