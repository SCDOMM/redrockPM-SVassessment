package com.example.core.model
/**
 * 包名称： com.example.core.model
 * 类名称：FollowCardData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:55
 *
 */

data class FollowCardData(
    val dataType: String,                  // "FollowCard"
    val header: FollowCardHeader? = null,
    val content: FollowCardContent? = null, // ⭐ content 有 {type, data} 两层包装
    val adTrack: List<Any>? = null,
    val trackingData: Any? = null,
    val tag: Any? = null
)

data class FollowCardContent(
    val type: String,                      // "video"
    val data: VideoData? = null,           // ⭐ 真正的视频数据
    val trackingData: Any? = null,
    val tag: Any? = null,
    val id: Int = 0,
    val adIndex: Int = -1
)

data class FollowCardHeader(
    val id: Long? = null,
    val title: String? = null,
    val font: Any? = null,
    val subTitle: Any? = null,
    val subTitleFont: Any? = null,
    val textAlign: String? = null,
    val cover: Any? = null,
    val label: Any? = null,
    val actionUrl: String? = null,
    val labelList: Any? = null,
    val rightText: Any? = null,
    val icon: String? = null,              // 作者头像
    val iconType: String? = null,
    val description: String? = null,
    val time: Long? = null,
    val showHateVideo: Boolean? = null,
    val followType: String? = null,
    val tagId: Int? = null,
    val tagName: String? = null,
    val issuerName: String? = null,       // 作者/发布者名称
    val topShow: Boolean? = null
)

data class TextCardData(
    val dataType: String,                  // "TextCard"
    val id: Int = 0,
    val type: String? = null,              // "header5" / "footer2" / ...
    val text: String? = null,
    val subTitle: Any? = null,
    val actionUrl: String? = null,
    val adTrack: Any? = null,
    val follow: Any? = null                // 可能为 Follow 对象
)
data class SquareCardCollectionData(
    val dataType: String,                  // "ItemCollection"
    val header: CollectionHeader? = null,
    val itemList: List<Item> = emptyList(), // ⭐ 里面的 Item.type = "followCard"
    val count: Int = 0,
    val adTrack: Any? = null,
    val footer: Any? = null
)

data class CollectionHeader(
    val id: Int = 0,
    val title: String? = null,
    val font: String? = null,
    val subTitle: String? = null,
    val subTitleFont: String? = null,
    val textAlign: String? = null,
    val actionUrl: String? = null,
    val rightText: String? = null,
    val cover: Any? = null,
    val label: Any? = null,
    val labelList: Any? = null
)

data class BannerData(
    val dataType: String,                  // "Banner"
    val id: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,             // ⭐ Banner图
    val actionUrl: String? = null,         // 点击跳转
    val bannerType: String? = null,        // "video" / "article"
    val videoId: Long? = null,
    val webUrl: WebUrl? = null
)

data class AtmosphericData(
    val dataType: String,                  // "Atmospheric"
    val id: Long = 0,
    val image: String? = null,             // ⭐ 背景大图
    val actionUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
    val textColorType: String? = null      // "light" / "dark"
)

data class InformationCardData(
    val dataType: String,                  // "InformationCard"
    val id: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,
    val actionUrl: String? = null,
    val webUrl: WebUrl? = null,
    val consumption: Consumption? = null
)