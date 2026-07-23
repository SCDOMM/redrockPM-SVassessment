package com.example.core.model

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：UserCenterResponse
 * 类描述：个人中心专用的特殊数据类
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
    @SerializedName("author_id")
    val authorId: Int,
    val cover: String?,
    val gender: String?,
    val country: String?,
    val city: String?,
    val job: String?,
    val university: String?,
    val birthday: Long?,
    val constellation: String?,
    @SerializedName("medal_count")
    val medalCount: Int,
    @SerializedName("follow_count")
    val followCount: Int,
    @SerializedName("fans_count")
    val fansCount: Int,
    @SerializedName("collected_count")
    val collectedCount: Int,
    @SerializedName("shared_count")
    val sharedCount: Int,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("featured_count")
    val featuredCount: Int,
    @SerializedName("recommend_count")
    val recommendCount: Int,
    val expert: Boolean,
    @SerializedName("is_mine")
    val isMine: Boolean,
    @SerializedName("medal_count_link")
    val medalCountLink: String?,
    @SerializedName("fans_count_link")
    val fansCountLink: String?,
    @SerializedName("follow_count_link")
    val followCountLink: String?,
    @SerializedName("private_msg_link")
    val privateMsgLink: String?,
    @SerializedName("watch_history_link")
    val watchHistoryLink: String?,
    @SerializedName("show_follow_btn")
    val showFollowBtn: Boolean,
    @SerializedName("show_private_msg_btn")
    val showPrivateMsgBtn: Boolean,
    @SerializedName("enable_share")
    val enableShare: Boolean,
    val mobile: String?,
    val brief: String?,
    val tags: List<Any>?,
    @SerializedName("nav_tabs")
    val navTabs: NavTabs?,          // 核心导航数据
    @SerializedName("follow_page_label")
    val followPageLabel: String?,
    val location: String?,
    @SerializedName("organization_info")
    val organizationInfo: List<Any>?
)
data class NavTabs(
    val style: String,                // "detail"
    @SerializedName("nav_list") val navList: List<NavTab>,
    @SerializedName("nav_item") val navItem: NavItem?
)
data class NavTab(
    @SerializedName("page_label") val pageLabel: String,
    @SerializedName("page_type") val pageType: String,
    @SerializedName("icon_type") val iconType: String,
    val title: String,               // "首页"/"作品"/"专辑"
    val url: String,                 // 深链接
    @SerializedName("default_display") val defaultDisplay: Boolean,
    @SerializedName("force_refresh") val forceRefresh: Boolean,
    @SerializedName("api_request") val apiRequest: Any?,
    @SerializedName("page_url") val pageUrl: String?,
    @SerializedName("page_url_parameter") val pageUrlParameter: String?,
    @SerializedName("is_recommend") val isRecommend: Boolean,
    @SerializedName("child_nav") val childNav: ChildNav?
)

data class ChildNav(
    val fixed: List<Any>?,
    val slide: List<Any>?
)

data class NavItem(
    val left: List<Any>?,
    val right: List<Any>?
)

// 这里的数据类不是原生传入的，是为了适配个人中心的专辑的Adapter存在的
data class AlbumData(
    val albumName: String,           // 专辑名称
    val albumDescription: String,    // 专辑描述
    val albumCoverUrl: String,       // 专辑封面图URL（来自footer中avatar.url）
    val albumLink: String,           // 点击专辑跳转的链接
    val videos: List<AlbumVideoPreview> // 该专辑下的视频预览列表（最多3个）
)

data class AlbumVideoPreview(
    val videoId: String,
    val title: String,
    val coverUrl: String,
    val duration: Int
)