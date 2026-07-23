package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * 包名称： com.example.core.model
 * 类名称：SearchResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 14:28
 *
 */
/**
 * 搜索接口顶层响应
 */
data class PaginatedResult<T>(
    @SerializedName("item_list") val itemList: List<T> = emptyList(),
    @SerializedName("item_count") val itemCount: Int = 0,
    @SerializedName("last_item_id") val lastItemId: String? = ""
)
data class Card(
    @SerializedName("card_id") val cardId: Long = 0,
    @SerializedName("card_unique_id") val cardUniqueId: String? = null,
    val type: String? = null,             // "set_metro_list" / "call_metro_list"
    val style: Any? = null,
    val interaction: Any? = null,
    @SerializedName("card_data") val cardData: CardData? = null,
    @SerializedName("api_request") val apiRequest: ApiRequest? = null,
    @SerializedName("tracking_data") val trackingData: Any? = null,

    // 以下字段仅在“作品页”等场景出现
    @SerializedName("last_item_id") val lastItemId: String? = null,
    @SerializedName("page_label") val pageLabel: String? = null,
    @SerializedName("page_params") val pageParams: String? = null,
    @SerializedName("material") val material: String? = null,
    @SerializedName("card_index") val cardIndex: Int? = null,
    @SerializedName("material_index") val materialIndex: Int? = null,
    @SerializedName("material_relative_index") val materialRelativeIndex: Int? = null,
    @SerializedName("data_source") val dataSource: String? = null,
)

data class ApiRequest(
    val url: String = "",
    val params: Map<String, Any?>? = null
)

data class CardData(
    val header: CardHeaderFooter? = null,
    val body: MetroBody? = null,
    val footer: CardHeaderFooter? = null
)

data class CardHeaderFooter(
    val style: Any? = null,
    val left: List<MetroItem>? = emptyList(),
    val center: List<MetroItem>? = emptyList(),
    val right: List<MetroItem>? = emptyList()
)

data class MetroBody(
    @SerializedName("metro_list") val metroList: List<MetroItem> = emptyList(),
    @SerializedName("api_request") val apiRequest: ApiRequest? = null
)

data class MetroItem(
    @SerializedName("metro_id") val metroId: Long = 0,
    @SerializedName("metro_unique_id") val metroUniqueId: String? = null,
    val type: String? = null,          // "video" / "item" / "text" / "user" / "link"
    val style: Any? = null,
    @SerializedName("metro_data") val metroData: MetroData? = null,
    @SerializedName("tracking_data") val trackingData: Any? = null,
    val link: String? = null,
    @SerializedName("tracking_params") val trackingParams: Any? = null,
    // 周榜专用
    @SerializedName("show_duration") val showDuration: Int? = null
)

data class MetroData(
    val uid: Long? = null,
    val nick: String? = null,
    val avatar: Avatar? = null,
    val description: String? = null,
    @SerializedName("followed")
    val isFollowed: Boolean? = null,
    @SerializedName("follow_count")
    val followCount: Int? = null,
    @SerializedName("fans_count")
    val fansCount: Int? = null,
    val link: String? = null,
    val type: String? = null,          // "pgc" / "ugc"
    val resourceId: String? = null,
    @SerializedName("resource_type")
    val resourceType: String? = null, // "pgc_video" / "ugc_picture" / "collection"
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("video_id")
    val videoId: String? = null,
    val title: String? = null,
    val duration: DurationInfo? = null,
    @SerializedName("play_ctrl")
    val playCtrl: PlayCtrl? = null,
    @SerializedName("play_url")
    val playUrl: String? = null,
    @SerializedName("preview_url")
    val previewUrl: String? = null,
    @SerializedName("recommend_level")
    val recommendLevel: String? = null,
    val tags: List<Tag>? = null,
    val cover: CoverInfo? = null,
    val author: Author? = null,
    @SerializedName("hot_value")
    val hotValue: Int? = null,
    val consumption: Consumption? = null,
    val liked: Boolean? = null,
    val collected: Boolean? = null,

    @SerializedName("image_id")
    val imageId: Long? = null,
    @SerializedName("publish_time")
    val publishTime: String? = null,
    @SerializedName("raw_publish_time")
    val rawPublishTime: String? = null,
    val location: Location? = null,
    val topics: List<Topic>? = null,
    @SerializedName("image_count")
    val imageCount: Int? = null,
    @SerializedName("private_msg_link")
    val privateMsgLink: String? = null,

    // ========== 话题（collection）特有字段 ==========
    @SerializedName("topic_id")
    val topicId: String? = null,
    val labels: List<Any>? = null,
    val status: String? = null,

    @SerializedName("item_id")
    val itemId: String?=null,
    @SerializedName("images")
    val images: List<Image>?=null,
    @SerializedName("video")
    val video: VideoDetail?=null,
    @SerializedName("is_mine")
    val isMine: Boolean?=null,
    @SerializedName("show_lock_icon")
    val showLockIcon: Boolean?=null,
    @SerializedName("show_follow_btn")
    val showFollowBtn: Boolean?=null,
    @SerializedName("show_more_btn")
    val showMoreBtn: Boolean?=null,
    @SerializedName("more_option")
    val moreOption: List<MoreOption>?=null,
    @SerializedName("real_location")
    val realLocation: String?=null,
    @SerializedName("comment_extra_tracking_fields")
    val commentExtraTrackingFields: Map<String, String>?=null
    )

data class DurationInfo(
    val value: Int = 0,
    val text: String = ""
)

data class PlayCtrl(
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

data class Tag(
    val id: Int = 0,
    val title: String = "",
    val link: String? = null
)

data class CoverInfo(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: ImageInfo? = null
)

data class ImageInfo(
    val width: Double = 0.0,
    val height: Double = 0.0,
    val scale: Double = 0.0
)

data class Author(
    val uid: Long = 0,
    val nick: String = "",
    val avatar: Avatar? = null,
    val description: String? = null,
    val link: String? = null,
    val type: String? = null,
    val followed: Boolean = false,
    @SerializedName("follow_count")
    val followCount: Int? = null,
    @SerializedName("fans_count")
    val fansCount: Int? = null
)

data class Avatar(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: ImageInfo? = null,
    val shape: String? = null
)

data class Consumption(
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

data class Location(
    val area: String? = null,
    val city: String? = null,
    val longitude: String? = "0.0000000",
    val latitude: String? = "0.0000000"
)

data class Topic(
    val id: Long = 0,
    val title: String = "",
    val link: String? = null
)
data class Image(
    @SerializedName("image_id")
    val imageId: String? = null,
    val cover: CoverInfo? = null
)
data class VideoDetail(
    @SerializedName("video_id")
    val videoId: String? = null,
    val title: String? = null,
    val duration: DurationInfo? = null,
    @SerializedName("play_ctrl")
    val playCtrl: PlayCtrl? = null,
    @SerializedName("play_url")
    val playUrl: String? = null,
    @SerializedName("preview_url")
    val previewUrl: String? = null,
    @SerializedName("recommend_level")
    val recommendLevel: String? = null,
    val tags: List<Tag>? = null,
    val cover: CoverInfo? = null,
    val author: Author? = null
)

data class MoreOption(
    val title: String = "",
    val type: String = ""                // "report_item"
)
data class WeeklyRankResult(
    @SerializedName("card_list") val cardList: List<Card> = emptyList()
)