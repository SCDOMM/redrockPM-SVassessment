package com.example.core.model.card

import com.example.core.model.Item
import com.example.core.model.media.WebUrl
import com.example.core.model.navigation.TextFooterData
import com.example.core.model.navigation.TextHeaderData

/**   
 * 包名称： com.example.core.model.banner
 * 类名称：BannerData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:54
 *
 */
data class BannerData(
    val dataType: String,           // "Banner"
    val id: Long,
    val title: String?,
    val description: String?,
    val image: String,              // ⭐ Banner图
    val actionUrl: String?,         // 点击跳转
    val bannerType: String?,        // "video" / "article"
    val videoId: Long?,
    val webUrl: WebUrl?
)
data class SquareCardCollectionData(
    val dataType: String,           // "SquareCardCollection"
    val id: Long,
    val header: TextHeaderData?,    // 标题头部
    val footer: TextFooterData?,    // 尾部
    val itemList: List<Item>,      // ⭐ 内部的卡片列表
    val count: Int,
    val adTrack: Any?
)