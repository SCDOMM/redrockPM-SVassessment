package com.example.core.model.official

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model.official
 * 类名称：WorkResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 23:33
 *
 */
data class UserWorkResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: UserWorkResult? = null
)

data class UserWorkResult(
    @SerializedName("page_info")
    val pageInfo: PageInfo? = null,          // 复用 PageInfo（已在 HomePageResponse 中定义）
    @SerializedName("card_list")
    val cardList: List<WorkCard> = emptyList()
)

/**
 * 作品页中的卡片，结构与 SearchCard 完全一致，独立定义避免耦合
 */
data class WorkCard(
    @SerializedName("card_id")
    val cardId: Long = 0,
    @SerializedName("card_unique_id")
    val cardUniqueId: String? = null,
    val type: String? = null,                // "set_metro_list" / "set_slide_metro_list"
    val style: Any? = null,
    val interaction: Any? = null,
    @SerializedName("card_data")
    val cardData: WorkCardData? = null,
    @SerializedName("tracking_data")
    val trackingData: Any? = null,
    @SerializedName("api_request")
    val apiRequest: ApiRequest? = null,
    @SerializedName("last_item_id")
    val lastItemId: String? = null,
    @SerializedName("page_label")
    val pageLabel: String? = null,
    @SerializedName("page_params")
    val pageParams: String? = null,
    @SerializedName("material")
    val material: String? = null,
    @SerializedName("card_index")
    val cardIndex: Int? = null,
    @SerializedName("material_index")
    val materialIndex: Int? = null,
    @SerializedName("material_relative_index")
    val materialRelativeIndex: Int? = null,
    @SerializedName("data_source")
    val dataSource: String? = null
)

data class WorkCardData(
    val header: WorkCardHeaderFooter? = null,
    val body: WorkBody? = null,
    val footer: WorkCardHeaderFooter? = null
)

data class WorkCardHeaderFooter(
    val style: Any? = null,
    val left: List<WorkMetroItem>? = emptyList(),
    val center: List<WorkMetroItem>? = emptyList(),
    val right: List<WorkMetroItem>? = emptyList()
)

data class WorkBody(
    @SerializedName("metro_list")
    val metroList: List<WorkMetroItem> = emptyList(),
    @SerializedName("api_request")
    val apiRequest: ApiRequest? = null
)

data class WorkMetroItem(
    @SerializedName("metro_id")
    val metroId: Long = 0,
    @SerializedName("metro_unique_id")
    val metroUniqueId: String? = null,
    val type: String? = null,                // "video" / "item" / "text" / "user" / "link"
    val style: Any? = null,
    @SerializedName("metro_data")
    val metroData: WorkMetroData? = null,
    @SerializedName("tracking_data")
    val trackingData: Any? = null,
    val link: String? = null,
    @SerializedName("tracking_params")
    val trackingParams: Any? = null
)

/**
 * 作品页核心数据类，包含视频和图文的所有属性
 */
data class WorkMetroData(
    // ========== 通用字段（视频/图文共有） ==========
    @SerializedName("item_id")
    val itemId: String? = null,              // 用于图文（video类型没有）
    @SerializedName("resource_id")
    val resourceId: String? = null,
    @SerializedName("resource_type")
    val resourceType: String? = null,        // "pgc_video" / "ugc_picture" / "pgc_picture" / "ugc_video"
    @SerializedName("publish_time")
    val publishTime: String? = null,
    @SerializedName("raw_publish_time")
    val rawPublishTime: String? = null,
    val location: WorkLocation? = null,
    val topics: List<WorkTopic>? = null,
    @SerializedName("private_msg_link")
    val privateMsgLink: String? = null,
    @SerializedName("text")
    val text: String? = null,                // 图文描述/视频简介
    @SerializedName("images")
    val images: List<WorkImage>? = null,     // 图文图片列表（视频为空数组）
    @SerializedName("video")
    val video: WorkVideoDetail? = null,      // 视频详情（图文为空对象）
    val consumption: WorkConsumption? = null,
    val liked: Boolean? = null,
    val collected: Boolean? = null,
    @SerializedName("is_mine")
    val isMine: Boolean? = null,
    @SerializedName("show_lock_icon")
    val showLockIcon: Boolean? = null,
    @SerializedName("show_follow_btn")
    val showFollowBtn: Boolean? = null,
    @SerializedName("show_more_btn")
    val showMoreBtn: Boolean? = null,
    @SerializedName("more_option")
    val moreOption: List<WorkOption>? = null,
    @SerializedName("real_location")
    val realLocation: String? = null,
    @SerializedName("comment_extra_tracking_fields")
    val commentExtraTrackingFields: Map<String, String>? = null,

    // ========== 视频特有字段 ==========
    @SerializedName("video_id")
    val videoId: String? = null,             // 视频ID（视频类型存在）
    val title: String? = null,               // 视频标题
    val duration: WorkDuration? = null,
    @SerializedName("play_ctrl")
    val playCtrl: WorkPlayCtrl? = null,
    @SerializedName("play_url")
    val playUrl: String? = null,
    @SerializedName("preview_url")
    val previewUrl: String? = null,
    @SerializedName("recommend_level")
    val recommendLevel: String? = null,      // "recommend" / "not_recommend"
    val tags: List<WorkTag>? = null,
    val cover: WorkCover? = null,
    val author: WorkAuthor? = null,
    @SerializedName("hot_value")
    val hotValue: Int? = null,

    val link: String? = null,
    @SerializedName("nick")
    val nick: String? = null,
    @SerializedName("avatar")
    val avatar: WorkAvatar? = null,
    @SerializedName("description")
    val description: String? = null
)

data class WorkDuration(
    val value: Int = 0,
    val text: String = ""
)

data class WorkPlayCtrl(
    val autoplay: Boolean? = false,
    @SerializedName("autoplay_times")
    val autoplayTimes: Int? = 0,
    @SerializedName("need_wifi")
    val needWifi: Boolean? = true,
    @SerializedName("need_cellular")
    val needCellular: Boolean? = true,
    @SerializedName("need_wifi_preload")
    val needWifiPreload: Boolean? = false
)

data class WorkTag(
    val id: Int = 0,
    val title: String = "",
    val link: String? = null
)

data class WorkCover(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: WorkImageInfo? = null
)

data class WorkImageInfo(
    val width: Double = 0.0,
    val height: Double = 0.0,
    val scale: Double = 0.0
)

data class WorkAuthor(
    val uid: Long = 0,
    val nick: String = "",
    val avatar: WorkAvatar? = null,
    val description: String? = null,
    val link: String? = null,
    val type: String? = null,             // "pgc" / "ugc"
    val followed: Boolean = false,
    @SerializedName("follow_count")
    val followCount: Int? = null,
    @SerializedName("fans_count")
    val fansCount: Int? = null
)

data class WorkAvatar(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: WorkImageInfo? = null,
    val shape: String? = null
)

data class WorkConsumption(
    @SerializedName("like_count")
    val likeCount: Int = 0,
    @SerializedName("collection_count")
    val collectionCount: Int = 0,
    @SerializedName("comment_count")
    val commentCount: Int = 0,
    @SerializedName("share_count")
    val shareCount: Int = 0,
    @SerializedName("play_count")
    val playCount: Int = 0
)

data class WorkLocation(
    val area: String? = null,
    val city: String? = null,
    val longitude: String? = "0.0000000",
    val latitude: String? = "0.0000000"
)

data class WorkTopic(
    val id: Long = 0,
    val title: String = "",
    val link: String? = null
)

data class WorkImage(
    @SerializedName("image_id")
    val imageId: String? = null,
    val cover: WorkCover? = null
)

data class WorkVideoDetail(
    @SerializedName("video_id")
    val videoId: String? = null,
    val title: String? = null,
    val duration: WorkDuration? = null,
    @SerializedName("play_ctrl")
    val playCtrl: WorkPlayCtrl? = null,
    @SerializedName("play_url")
    val playUrl: String? = null,
    @SerializedName("preview_url")
    val previewUrl: String? = null,
    @SerializedName("recommend_level")
    val recommendLevel: String? = null,
    val tags: List<WorkTag>? = null,
    val cover: WorkCover? = null,
    val author: WorkAuthor? = null
)

data class WorkOption(
    val title: String = "",
    val type: String = ""                // "report_item"
)