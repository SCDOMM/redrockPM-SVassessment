package com.example.core.model

/**
 * 搜索结果响应模型 (v1)
 */
data class SearchResponse(
    val code: Int = -1,
    val result: SearchResponseResult? = null
)

data class SearchResponseResult(
    val item_list: List<SearchCategory> = emptyList()
)

data class SearchCategory(
    val nav: PageInfo? = null,
    val card_list: List<Card> = emptyList()
)

/**
 * 搜索结果响应模型 (v2)
 */
data class SearchResponseV2(
    val code: Int = -1,
    val result: SearchResponseV2Result? = null
)

data class SearchResponseV2Result(
    val item_list: List<MetroItem> = emptyList(),
    val last_item_id: String = ""
)
