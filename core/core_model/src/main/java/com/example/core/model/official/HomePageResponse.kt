package com.example.core.model.official

/**   
 * 包名称： com.example.core.model
 * 类名称：HomePageResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 15:53
 *
 */
data class HomePageResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: HomePageResult? = null
)
data class HomePageResult(
    val page_info: PageInfo? = null,
    val card_list: List<SearchCard> = emptyList()
)
data class PageInfo(
    val page_id: Long? = null,
    val title: String? = null,
    val enable_share: Boolean? = null,
    val page_label: String? = null,
    val tracking_data: Any? = null,
    val show_the_end: Boolean? = null
)
data class CallMetroListResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: CallMetroListResult? = null
)
data class CallMetroListResult(
    val item_list: List<MetroItem> = emptyList(),
    val item_count: Int = 0,
    val last_item_id: Int = 0
)