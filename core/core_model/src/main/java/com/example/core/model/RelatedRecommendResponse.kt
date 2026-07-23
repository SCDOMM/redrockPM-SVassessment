package com.example.core.model

/**
 * description ： 相关推荐接口响应模型
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
data class RelatedRecommendResponse(
    val code: Int = -1,
    val message: Any? = null,
    val result: RelatedRecommendResult? = null
)

data class RelatedRecommendResult(
    val item_list: List<RelatedRecommendItem> = emptyList(),
    val ad_item_list: List<Any> = emptyList(),
    val item_count: Int = 0,
    val item_per_page: Int = 0
)

data class RelatedRecommendItem(
    val item_id: String = "",
    val text: String = "",
    val category: ItemDetailCategory? = null,
    val author: ItemDetailAuthor? = null,
    val consumption: ItemDetailConsumption? = null,
    val video: ItemDetailVideo? = null,
    val resource_type: String = ""
)
