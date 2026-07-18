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
data class SearchResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: SearchResult? = null
)

data class SearchResult(
    val item_list: List<SearchCategory> = emptyList()
)
data class SearchCategory(
    val nav: SearchNav? = null,
    val page_info: SearchPageInfo? = null,
    val card_list: List<SearchCard> = emptyList()
)

data class SearchNav(
    val title: String? = null,
    val type: String? = null,          // "video" / "pgc" / "graphic" / "topic" / "ugc"
    val tracking_data: Any? = null
)

data class SearchPageInfo(
    val title: String? = null,
    val tracking_data: Any? = null
)

data class SearchCard(
    val card_id: Long = 0,
    val card_unique_id: String? = null,
    val type: String? = null,             // "set_metro_list" / "call_metro_list"
    val style: Any? = null,
    val interaction: Any? = null,
    val card_data: MetroCardData? = null,
    val tracking_data: Any? = null
)

data class MetroCardData(
    val header: CardHeaderFooter? = null,
    val body: MetroBody? = null,
    val footer: CardHeaderFooter? = null
)
data class CardHeaderFooter(
    val style: Any? = null,
    val left: List<Any>? = emptyList(),
    val center: List<Any>? = emptyList(),
    val right: List<Any>? = emptyList()
)

data class MetroBody(
    val metro_list: List<MetroItem> = emptyList()
)

data class MetroItem(
    @SerializedName("metro_id")
    val metroId: Long = 0,
    @SerializedName("metro_unique_id")
    val metroUniqueId: String? = null,
    val type: String? = null,          // "video" / "user" / "image" / "topic"
    val style: Any? = null,
    @SerializedName("metro_data")
    val metroData: MetroData? = null,
    val tracking_data: Any? = null,
    val link: String? = null,
    val tracking_params: Any? = null
)
data class MetroData(
    // ========== 通用字段 ==========
    val uid: Long? = null,
    val nick: String? = null,
    val avatar: SearchAvatar? = null,
    val description: String? = null,
    @SerializedName("followed")
    val isFollowed: Boolean? = null,
    @SerializedName("follow_count")
    val followCount: Int? = null,
    @SerializedName("fans_count")
    val fansCount: Int? = null,
    val link: String? = null,
    val type: String? = null,          // "pgc" / "ugc"
    val resource_id: String? = null,
    val resource_type: String? = null, // "pgc_video" / "ugc_picture" / "collection"

    // ========== 视频特有字段 ==========
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
    val tags: List<SearchTag>? = null,
    val cover: CoverInfo? = null,
    val author: SearchAuthor? = null,
    @SerializedName("hot_value")
    val hotValue: Int? = null,
    val consumption: SearchConsumption? = null,
    val liked: Boolean? = null,
    val collected: Boolean? = null,

    // ========== 图文（ugc_picture）特有字段 ==========
    @SerializedName("image_id")
    val imageId: Long? = null,
    @SerializedName("publish_time")
    val publishTime: String? = null,
    @SerializedName("raw_publish_time")
    val rawPublishTime: String? = null,
    val location: SearchLocation? = null,
    val topics: List<SimpleTopic>? = null,
    @SerializedName("image_count")
    val imageCount: Int? = null,
    @SerializedName("private_msg_link")
    val privateMsgLink: String? = null,

    // ========== 话题（collection）特有字段 ==========
    @SerializedName("topic_id")
    val topicId: String? = null,
    val labels: List<Any>? = null,
    val status: String? = null
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

data class SearchTag(
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
    val width: Int = 0,
    val height: Int = 0,
    val scale: Double = 0.0
)

data class SearchAuthor(
    val uid: Long = 0,
    val nick: String = "",
    val avatar: SearchAvatar? = null,
    val description: String? = null,
    val link: String? = null,
    val type: String? = null,
    val followed: Boolean = false,
    @SerializedName("follow_count")
    val followCount: Int? = null,
    @SerializedName("fans_count")
    val fansCount: Int? = null
)

data class SearchAvatar(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: ImageInfo? = null,
    val shape: String? = null
)

data class SearchConsumption(
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

data class SearchLocation(
    val area: String? = null,
    val city: String? = null,
    val longitude: String? = "0.0000000",
    val latitude: String? = "0.0000000"
)
data class SimpleTopic(
    val id: Long = 0,
    val title: String = "",
    val link: String? = null
)