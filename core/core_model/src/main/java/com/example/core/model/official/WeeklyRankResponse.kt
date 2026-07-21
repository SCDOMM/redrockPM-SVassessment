package com.example.core.model.official

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：WeeklyRankResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-19 20:54
 *
 */
data class WeeklyRankResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: WeeklyRankResult? = null
)

data class WeeklyRankResult(
    val card_list: List<WeeklyRankCard> = emptyList()
)

/**
 * 周榜卡片（与搜索的 SearchCard 类似，但字段略有差异）
 */
data class WeeklyRankCard(
    @SerializedName("card_id")
    val cardId: Long = 0,
    val type: String? = null,                 // "set_metro_list"
    val style: Any? = null,
    val interaction: Any? = null,
    @SerializedName("card_data")
    val cardData: WeeklyRankCardData? = null
)

data class WeeklyRankCardData(
    val header: WeeklyRankHeader? = null,
    val body: WeeklyRankBody? = null,
    val footer: Any? = null
)

/**
 * 卡片头部（包含标题信息，如 "视频周榜"）
 */
data class WeeklyRankHeader(
    val left: List<WeeklyRankHeaderItem>? = null,
    val center: List<Any>? = null,
    val right: List<Any>? = null
)

data class WeeklyRankHeaderItem(
    @SerializedName("metro_id")
    val metroId: Long = 0,
    val type: String? = null,                 // "link"
    val style: Any? = null,
    @SerializedName("metro_data")
    val metroData: WeeklyRankHeaderData? = null,
    val tracking_data: Any? = null
)

data class WeeklyRankHeaderData(
    val text: String? = null,                 // "视频周榜"
    val link: String? = null                  // "eyepetizer://ranklist"
)

/**
 * 卡片主体（包含视频列表）
 */
data class WeeklyRankBody(
    @SerializedName("metro_list")
    val metroList: List<WeeklyRankMetroItem> = emptyList()
)

/**
 * 单个视频条目（与搜索的 MetroItem 结构相似，但字段名略有不同）
 */
data class WeeklyRankMetroItem(
    @SerializedName("metro_id")
    val metroId: Long = 0,
    val type: String? = null,                 // "video"
    val style: Any? = null,
    @SerializedName("metro_data")
    val metroData: WeeklyRankMetroData? = null,
    val link: String? = null,
    @SerializedName("show_duration")
    val showDuration: Int? = null,
    val tracking_data: Any? = null
)

/**
 * 视频数据（与搜索的 MetroData 字段类似，但增加了 more 字段，移除了部分字段）
 */
data class WeeklyRankMetroData(
    @SerializedName("video_id")
    val videoId: Long = 0,
    val title: String = "",
    val duration: DurationInfo? = null,
    @SerializedName("play_ctrl")
    val playCtrl: PlayCtrl? = null,
    @SerializedName("play_url")
    val playUrl: String = "",
    @SerializedName("recommend_level")
    val recommendLevel: String? = null,
    val tags: List<SearchTag>? = null,
    val cover: CoverInfo? = null,
    val author: WeeklyRankAuthor? = null,
    val more: WeeklyRankMore? = null,
    @SerializedName("resource_type")
    val resourceType: String? = null,         // "video"
    @SerializedName("resource_id")
    val resourceId: Long = 0
)

/**
 * 作者信息（与搜索的 SearchAuthor 字段类似，但类型为 String 的 type 字段值不同，如 "PGC"）
 */
data class WeeklyRankAuthor(
    val uid: Long = 0,
    val nick: String = "",
    val description: String? = null,
    val avatar: WeeklyRankAvatar? = null,
    val link: String? = null,
    val type: String? = null,                 // "PGC"
    val followed: Boolean = false
)

data class WeeklyRankAvatar(
    val url: String = "",
    @SerializedName("img_info")
    val imgInfo: ImageInfo? = null
)

/**
 * 更多信息（分享平台、关注、缓存等）
 */
data class WeeklyRankMore(
    @SerializedName("share_platform")
    val sharePlatform: List<String>? = null,
    @SerializedName("enable_follow")
    val enableFollow: Boolean? = null,
    @SerializedName("enable_cache")
    val enableCache: Boolean? = null
)