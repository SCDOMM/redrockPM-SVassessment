package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * 视频详情响应
 */
data class ItemDetailResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: ItemDetailResult? = null
)

data class ItemDetailResult(
    val type: String = "",
    val data: Any? = null
)

/**
 * 相关推荐响应
 */
data class RelatedRecommendResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: RelatedRecommendResult? = null
)

data class RelatedRecommendResult(
    @SerializedName("item_list")
    val itemList: List<MetroItem> = emptyList()
)

/**
 * 标签列表响应
 */
data class TabListResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: TabListResult? = null
)

data class TabListResult(
    @SerializedName("tab_list")
    val tabList: List<TabItem> = emptyList()
)

data class TabItem(
    val id: String = "",
    val name: String = "",
    @SerializedName("api_url")
    val apiUrl: String = ""
)
