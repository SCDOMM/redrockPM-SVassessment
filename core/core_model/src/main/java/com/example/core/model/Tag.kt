package com.example.core.model

/**
 * 包名称： com.example.core.model.general
 * 类名称：Tag
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:50
 *
 */
data class Tag(
    val id: Long = 0,                        // 标签ID
    val name: String = "",                   // ⭐ 标签名
    val actionUrl: String? = null,           // 点击跳转地址
    val desc: String? = null,                // 描述
    val bgPicture: String? = null,           // 背景图
    val headerImage: String? = null,         // 头部图
    val tagRecType: String? = null,          // 推荐类型
    val childTagList: List<Any>? = null,
    val childTagIdList: List<Int>? = null,
    val haveReward: Boolean? = null,
    val ifNewest: Boolean? = null,
    val newestEndTime: Long? = null,
    val communityIndex: Int? = null          // 社区索引
)
/**
 * 标签资源（视频上的小标签）
 */
data class Label(
    val text: String? = null,
    val card: String? = null,
    val detail: LabelDetail? = null,
    val actionUrl: String? = null
)
data class LabelDetail(
    val text: String? = null,
    val icon: String? = null,
    val color: ColorData? = null,
    val bgColor: ColorData? = null
)
data class ColorData(
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0,
    val alpha: Float = 0f
)
/**
 * 标签合集卡片
 */
data class TagInfoCollectionData(
    val dataType: String = "",
    val id: Long = 0,
    val header: TextHeaderData? = null,
    val footer: TextFooterData? = null,
    val tagList: List<Tag>? = null,
    val count: Int = 0
)