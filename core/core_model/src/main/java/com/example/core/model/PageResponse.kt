package com.example.core.model

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：HomePageResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 15:53
 *
 */
data class PageResult(
    val nav: PageInfo? = null,
    @SerializedName("page_info")
    val pageInfo: PageInfo? = null,
    @SerializedName("card_list")
    val cardList: List<Card> = emptyList()
)
data class PageInfo(
    @SerializedName("page_id")
    val pageId: Long? = null,
    val title: String? = null,
    val enable_share: Boolean? = null,
    val page_label: String? = null,
    val tracking_data: Any? = null,
    val show_the_end: Boolean? = null,

    //搜索专用的字段
    val type: String? = null,          // "video" / "pgc" / "graphic" / "topic" / "ugc"
)

