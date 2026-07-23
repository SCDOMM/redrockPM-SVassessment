package com.example.core.model

/**
 * description ： get_page 接口响应模型
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */

data class GetPageResponse(
    val code: Int = -1,
    val result: GetPageResult? = null
)

data class GetPageResult(
    val page_info: PageInfo? = null,
    val card_list: List<GetPageCard> = emptyList()
)

data class PageInfo(
    val page_id: Int = 0,
    val title: String = "",
    val page_label: String = "",
    val enable_share: Boolean = false,
    val show_the_end: Boolean = false
)

data class GetPageCard(
    val card_id: Int = 0,
    val card_unique_id: String = "",
    val type: String = "",
    val style: Any? = null,
    val card_data: GetPageCardData? = null,
    val tracking_data: Any? = null
)

data class GetPageCardData(
    val header: GetPageCardHeader? = null,
    val body: GetPageCardBody? = null,
    val footer: Any? = null
)

data class GetPageCardHeader(
    val type: String = "",
    val title: String = "",
    val font: String = "",
    val left: List<GetPageMetroItem>? = null,
    val center: List<GetPageMetroItem>? = null,
    val right: List<GetPageMetroItem>? = null
)

data class GetPageCardBody(
    val metro_list: List<GetPageMetroItem> = emptyList(),
    val api_request: GetPageApiRequest? = null
)

data class GetPageApiRequest(
    val url: String = "",
    val params: Map<String, String>? = null
)

data class GetPageMetroItem(
    val type: String = "",
    val tpl_label: String = "",
    val metro_data: GetPageMetroData? = null,
    val link: String = ""
)

data class GetPageMetroData(
    val link: String = "",
    val topic_id: String = "",
    val name: String = "",
    val title: String? = null,
    val icon: String = "",
    val description: String = "",
    val subtitle: String? = null,
    val background: GetPageBackground? = null,
    val tags: List<GetPageTag>? = null,
    val nav_list: List<GetPageNav>? = null,
    val items: List<Any>? = null,
    val item_list: List<GetPageSquareItem>? = null,
    val icons: List<GetPageIconItem>? = null,
    val text: String = "",
    val style: String? = null,
    val video: GetPageVideo? = null,
    val author: GetPageAuthor? = null,
    val consumption: GetPageConsumption? = null,
    val item_id: String = "",
    val image_id: Long = 0,
    val cover: GetPageCover? = null,
    val resource_type: String = "",
    val resource_id: Long = 0,
    // 视频相关字段（直接在 metro_data 中）
    val video_id: String = "",
    val duration: GetPageDuration? = null,
    val play_url: String = "",
    // UGC 相关字段
    val images: List<GetPageImage>? = null,
    val publish_time: String = "",
    val content: GetPageContent? = null
)

data class GetPageImage(
    val image_id: String = "",
    val cover: GetPageCover? = null
)

data class GetPageContent(
    val blocks: List<GetPageBlock>? = null
)

data class GetPageBlock(
    val text: String? = null
)

data class GetPageSquareItem(
    val image_id: String = "",
    val title: String = "",
    val cover: GetPageCover? = null,
    val resource_type: String = "",
    val resource_id: Long = 0,
    val link: String = ""
)

data class GetPageVideo(
    val video_id: String = "",
    val title: String = "",
    val duration: GetPageDuration? = null,
    val play_url: String = "",
    val cover: GetPageCover? = null,
    val tags: List<GetPageTag>? = null
)

data class GetPageDuration(
    val value: Long = 0,
    val text: String = ""
)

data class GetPageCover(
    val url: String = "",
    val img_info: GetPageImgInfo? = null
)

data class GetPageImgInfo(
    val width: Float = 0f,
    val height: Float = 0f,
    val scale: Float = 0f
)

data class GetPageAuthor(
    val uid: Long = 0,
    val nick: String = "",
    val description: String = "",
    val avatar: GetPageAvatar? = null,
    val followed: Boolean = false
)

data class GetPageAvatar(
    val url: String = "",
    val img_info: GetPageImgInfo? = null,
    val shape: String = ""
)

data class GetPageConsumption(
    val like_count: Int = 0,
    val collection_count: Int = 0,
    val comment_count: Int = 0,
    val share_count: Int = 0
)

data class GetPageIconItem(
    val name: String = "",
    val icon: String = "",
    val link: String = ""
)

data class GetPageBackground(
    val url: String = "",
    val width: Int = 0,
    val height: Int = 0
)

data class GetPageTag(
    val id: Long = 0,
    val title: String = "",
    val link: String = ""
)

data class GetPageNav(
    val page_label: String = "",
    val title: String = ""
)

data class ApiRequest(
    val url: String,
    val params: Map<String, String>
)

data class CallMetroListResponse(
    val code: Int = -1,
    val message: Any? = null,
    val result: CallMetroListResult? = null
)

data class CallMetroListResult(
    val item_list: List<GetPageMetroItem> = emptyList(),
    val item_count: Int = 0,
    val last_item_id: String = ""
)

// ========== get_nav 接口响应模型 ==========

data class GetNavResponse(
    val code: Int = -1,
    val result: GetNavResult? = null
)

data class GetNavResult(
    val nav_list: List<GetNavTab> = emptyList()
)

data class GetNavTab(
    val page_label: String = "",
    val title: String = "",
    val page_type: String = ""
)
