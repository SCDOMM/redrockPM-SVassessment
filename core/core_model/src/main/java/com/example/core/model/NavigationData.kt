package com.example.core.model

import com.example.core.model.TabInfoWrapper
import com.example.core.model.TabItem
import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：NavigationData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:57
 *
 */

data class NavigationData(
    val dataType: String = "",               // "Navigation"
    val id: Long = 0,
    val tabList: List<TabInfo>? = null
)

data class TabInfo(
    val id: Long = 0,
    val name: String = "",                   // "推荐" / "发现" / "关注"
    val actionUrl: String? = null,
    val type: String? = null
)

/**
 * 文本底部（"查看更多"）
 */
data class TextFooterData(
    val dataType: String = "",               // "TextFooter"
    val id: Long = 0,
    val text: String = "",                   // "查看更多"
    val actionUrl: String? = null
)

/**
 * 文本头部（分区标题如"今日精选"）
 */
data class TextHeaderData(
    val dataType: String = "",               // "TextHeader"
    val id: Long = 0,
    val text: String = "",                   // ⭐ 标题文字
    val subText: String? = null,
    val font: String? = null,
    val actionUrl: String? = null,
    val adTrack: Any? = null
)

/**
 * Tab列表响应（首页Tab）
 */
data class TabListResponse(
    @SerializedName("tabInfo")
    val tabInfo: TabInfoWrapper? = null
)

data class TabInfoWrapper(
    @SerializedName("tabList")
    val tabList: List<TabItem> = emptyList(),
    @SerializedName("defaultIdx")
    val defaultIdx: Int = 0
)

data class TabItem(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("apiUrl")
    val apiUrl: String = "",
    @SerializedName("tabType")
    val tabType: Int = 0,
    @SerializedName("nameType")
    val nameType: Int = 0
)

/**
 * 排行榜Tab响应
 */
data class RankListResponse(
    @SerializedName("tabInfo")
    val tabInfo: RankTabInfo? = null
)

data class RankTabInfo(
    @SerializedName("tabList")
    val tabList: List<RankTab> = emptyList(),
    @SerializedName("defaultIdx")
    val defaultIdx: Int = 0
)

data class RankTab(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("apiUrl")
    val apiUrl: String = ""
)