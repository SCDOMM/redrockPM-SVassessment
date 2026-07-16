package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * description ： 排行榜Tab列表响应
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
data class RankListResponse(
    @SerializedName("tabInfo")
    val tabInfo: RankTabInfo // 排行榜Tab信息
)

/**
 * description ： 排行榜Tab信息
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
data class RankTabInfo(
    @SerializedName("tabList")
    val tabList: List<RankTab>, // Tab列表（月排行/周排行/总排行）
    @SerializedName("defaultIdx")
    val defaultIdx: Int // 默认选中的Tab索引
)

/**
 * description ： 排行榜单个Tab
 * email : 3014386984@qq.com
 * date : 2026/7/14 19:57
 */
data class RankTab(
    @SerializedName("id")
    val id: Int, // Tab唯一ID
    @SerializedName("name")
    val name: String, // Tab名称（monthly/weekly/historical）
    @SerializedName("apiUrl")
    val apiUrl: String // Tab对应的API请求地址
)
