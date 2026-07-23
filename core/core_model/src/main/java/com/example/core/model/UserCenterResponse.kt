package com.example.core.model

/**   
 * 包名称： com.example.core.model.official
 * 类名称：UserCenterResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 16:30
 *
 */

data class UserInfo(
    val uid: Long,
    val nick: String,
    val avatar: Avatar?,
    val description: String,
    val link: String,
    val type: String,                 // "pgc" / "ugc"
    val followed: Boolean,
    val author_id: Int,
    val cover: String?,
    val gender: String?,
    val country: String?,
    val city: String?,
    val job: String?,
    val university: String?,
    val birthday: Long?,
    val constellation: String?,
    val medal_count: Int,
    val follow_count: Int,
    val fans_count: Int,
    val collected_count: Int,
    val shared_count: Int,
    val item_count: Int,
    val featured_count: Int,
    val recommend_count: Int,
    val expert: Boolean,
    val is_mine: Boolean,
    val medal_count_link: String?,
    val fans_count_link: String?,
    val follow_count_link: String?,
    val private_msg_link: String?,
    val watch_history_link: String?,
    val show_follow_btn: Boolean,
    val show_private_msg_btn: Boolean,
    val enable_share: Boolean,
    val mobile: String?,
    val brief: String?,
    val tags: List<Any>?,
    val nav_tabs: NavTabs?,          // 核心导航数据
    val follow_page_label: String?,
    val location: String?,
    val organization_info: List<Any>?
)
data class NavTabs(
    val style: String,                // "detail"
    val nav_list: List<NavTab>,
    val nav_item: NavItem?
)

data class NavTab(
    val page_label: String,          // 专辑页的 page_label
    val page_type: String,           // "card"
    val icon_type: String,           // "index"/"work"/"album"
    val title: String,               // "首页"/"作品"/"专辑"
    val url: String,                 // 深链接
    val default_display: Boolean,
    val force_refresh: Boolean,
    val api_request: Any?,
    val page_url: String?,
    val page_url_parameter: String?,
    val is_recommend: Boolean,
    val child_nav: ChildNav?
)

data class ChildNav(
    val fixed: List<Any>?,
    val slide: List<Any>?
)

data class NavItem(
    val left: List<Any>?,
    val right: List<Any>?
)