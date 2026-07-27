package com.example.core.network.api

import com.example.core.model.ApiResponse
import com.example.core.model.CallMetroListResponse
import com.example.core.model.Card
import com.example.core.model.GetNavResponse
import com.example.core.model.Item
import com.example.core.model.MetroItem
import com.example.core.model.NoticeItem
import com.example.core.model.PageResult
import com.example.core.model.PaginatedResult
import com.example.core.model.PaginatedResult1
import com.example.core.model.TabListResponse
import com.example.core.model.UserInfo
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * description ： api接口
 */
interface UniversalApi {
    @FormUrlEncoded
    @POST("v1/card/page/get_page")
    fun getPage(
        @Field("page_label") pageLabel: String,
        @Field("page_type") pageType: String = "card",
    ): Call<ApiResponse<PageResult>>

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
        @Field("data_source") dataSource: String = "",
        @Field("page_label") pageLabel: String = "",
        @Field("material") materialJSON: String=defaultMaterialJSON,
        @Field("card") cardJSON: String = defaultCardJSON,
        @Field("last_item_id") lastItemId: String,
    ): Call<ApiResponse<PaginatedResult<MetroItem>>>

    @GET("v1/user/center/get_user_info")
    fun getUserInfo(
        @Query("uid") uid: String,
        @Query("user_type") userType: String = ""
    ): Call<ApiResponse<UserInfo>>

    @FormUrlEncoded
    @POST("v1/card/metro/call_metro_list_v2")
    fun loadMorePage(
        @Field("data_source") dataSource: String = "",
        @Field("material") materialJSON: String="",
        @Field("last_item_id") lastItemId: String,
    ): Call<ApiResponse<PaginatedResult<MetroItem>>>

    // ========== 从 KaiyanApi 整合的方法 ==========

    @FormUrlEncoded
    @POST("v1/card/page/get_nav")
    fun getNav(
        @Field("tab_label") tabLabel: String
    ): Call<GetNavResponse>

    @FormUrlEncoded
    @POST("v1/content/item/get_item_detail_v2")
    fun getItemDetail(
        @Field("resource_id") resourceId: String,
        @Field("resource_type") resourceType: String = "pgc_video"
    ): Call<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("v1/content/item/get_related_recommend")
    fun getRelatedRecommend(
        @Field("resource_id") resourceId: String,
        @Field("resource_type") resourceType: String = "pgc_video"
    ): Call<okhttp3.ResponseBody>


    // ========== 主题播单加载更多 ==========

    @FormUrlEncoded
    @POST("v1/card/card/call_card_list")
    fun callCardList(
        @Field("last_item_id") lastItemId: String,
        @Field("card_list") cardList: String,
        @Field("page_label") pageLabel: String
    ): Call<okhttp3.ResponseBody>

    // ========== Metro 列表加载更多 ==========

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

    // ========== 排行榜 ==========

    @GET("v4/rankList")
    fun getRankListTabs(): Call<TabListResponse>

    @GET
    fun getRankList(@Url url: String): Call<PaginatedResult1<Item>>


}
const val defaultMaterialJSON="{\"metro_id\":2000001,\"metro_unique_id\":\"metro-nl8cvt0nn7n5lpjdjq22gts4rr\",\"type\":\"item\",\"style\":{\"tpl_label\":\"feed_item_detail\",\"padding\":{\"top\":10,\"right\":15,\"bottom\":23,\"left\":15},\"separator_line\":{\"top\":{\"color\":\"transparent\",\"margin\":{\"top\":0,\"right\":0,\"bottom\":0,\"left\":0},\"height\":0.5},\"bottom\":{\"color\":\"#E7E7E7\",\"margin\":{\"top\":0,\"right\":0,\"bottom\":0,\"left\":0},\"height\":0.5}},\"background\":{\"color\":\"transparent\"}},\"metro_data\":[],\"tracking_data\":[],\"data_source\":{\"label\":\"home_user_work_list\",\"params\":{\"uid\":301175241,\"user_type\":\"pgc\",\"page_label\":\"user_center_work\"}}}"
const val defaultCardJSON="{\"card_data\":{\"body\":{\"api_request\":{\"params\":{\"card\":\"{\\\"card_id\\\":1000001,\\\"card_unique_id\\\":\\\"card-kg2so3rhr8tcvohf6dqhb04goi\\\",\\\"type\\\":\\\"call_metro_list\\\",\\\"style\\\":{\\\"padding\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"background\\\":{\\\"color\\\":\\\"#FFFFFF\\\"},\\\"separator_line\\\":{\\\"top\\\":{\\\"color\\\":\\\"transparent\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0},\\\"bottom\\\":{\\\"color\\\":\\\"transparent\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0}}},\\\"interaction\\\":{\\\"scroll\\\":\\\"v-scroll\\\"},\\\"card_data\\\":{\\\"header\\\":{\\\"style\\\":{\\\"padding\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0}},\\\"left\\\":[],\\\"center\\\":[],\\\"right\\\":[]},\\\"body\\\":{\\\"api_request\\\":[],\\\"metro_list\\\":[{\\\"metro_id\\\":2000001,\\\"metro_unique_id\\\":\\\"metro-jestslqetap0k7b1heq7vaq670\\\",\\\"type\\\":\\\"item\\\",\\\"style\\\":{\\\"tpl_label\\\":\\\"feed_item_detail\\\",\\\"padding\\\":{\\\"top\\\":10,\\\"right\\\":15,\\\"bottom\\\":23,\\\"left\\\":15},\\\"separator_line\\\":{\\\"top\\\":{\\\"color\\\":\\\"transparent\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0.5},\\\"bottom\\\":{\\\"color\\\":\\\"#E7E7E7\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0.5}},\\\"background\\\":{\\\"color\\\":\\\"transparent\\\"}},\\\"metro_data\\\":[],\\\"tracking_data\\\":[],\\\"data_source\\\":{\\\"label\\\":\\\"home_user_work_list\\\",\\\"params\\\":{\\\"uid\\\":301175241,\\\"user_type\\\":\\\"pgc\\\",\\\"page_label\\\":\\\"user_center_work\\\"}}}]},\\\"footer\\\":{\\\"style\\\":{\\\"padding\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0}},\\\"left\\\":[],\\\"center\\\":[],\\\"right\\\":[]}}}\",\"card_index\":2.0,\"material_index\":12.0,\"material_relative_index\":0.0,\"material\":\"{\\\"metro_id\\\":2000001,\\\"metro_unique_id\\\":\\\"metro-jestslqetap0k7b1heq7vaq670\\\",\\\"type\\\":\\\"item\\\",\\\"style\\\":{\\\"tpl_label\\\":\\\"feed_item_detail\\\",\\\"padding\\\":{\\\"top\\\":10,\\\"right\\\":15,\\\"bottom\\\":23,\\\"left\\\":15},\\\"separator_line\\\":{\\\"top\\\":{\\\"color\\\":\\\"transparent\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0.5},\\\"bottom\\\":{\\\"color\\\":\\\"#E7E7E7\\\",\\\"margin\\\":{\\\"top\\\":0,\\\"right\\\":0,\\\"bottom\\\":0,\\\"left\\\":0},\\\"height\\\":0.5}},\\\"background\\\":{\\\"color\\\":\\\"transparent\\\"}},\\\"metro_data\\\":[],\\\"tracking_data\\\":[],\\\"data_source\\\":{\\\"label\\\":\\\"home_user_work_list\\\",\\\"params\\\":{\\\"uid\\\":301175241,\\\"user_type\\\":\\\"pgc\\\",\\\"page_label\\\":\\\"user_center_work\\\"}}}\",\"data_source\":\"home_user_work_list\",\"last_item_id\":\"pgc_video#168879\",\"page_label\":\"user_center_work\",\"page_params\":\"{\\\"uid\\\":301175241,\\\"user_type\\\":\\\"pgc\\\"}\"},\"url\":\"https://api.eyepetizer.net/v1/card/metro/call_metro_list_v2\"},\"metro_list\":[]},\"footer\":{\"center\":[],\"left\":[],\"right\":[],\"style\":{\"padding\":{\"top\":0.0,\"right\":0.0,\"bottom\":0.0,\"left\":0.0}}},\"header\":{\"center\":[],\"left\":[],\"right\":[],\"style\":{\"padding\":{\"top\":0.0,\"right\":0.0,\"bottom\":0.0,\"left\":0.0}}}},\"card_id\":1000001,\"card_unique_id\":\"card-kg2so3rhr8tcvohf6dqhb04goi\",\"interaction\":{\"scroll\":\"v-scroll\"},\"style\":{\"padding\":{\"top\":0.0,\"right\":0.0,\"bottom\":0.0,\"left\":0.0},\"background\":{\"color\":\"#FFFFFF\"},\"separator_line\":{\"top\":{\"color\":\"transparent\",\"margin\":{\"top\":0.0,\"right\":0.0,\"bottom\":0.0,\"left\":0.0},\"height\":0.0},\"bottom\":{\"color\":\"transparent\",\"margin\":{\"top\":0.0,\"right\":0.0,\"bottom\":0.0,\"left\":0.0},\"height\":0.0}}},\"tracking_data\":{},\"type\":\"call_metro_list\"}"
