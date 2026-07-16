package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * description ： 首页Tab列表响应
 * email : 3014386984@qq.com
 * date : 2026/7/16 10:00
 */
data class TabListResponse(
    @SerializedName("tabInfo")
    val tabInfo: TabInfoWrapper // Tab信息容器
)

/**
 * description ： Tab信息容器
 * email : 3014386984@qq.com
 * date : 2026/7/16 10:00
 */
data class TabInfoWrapper(
    @SerializedName("tabList")
    val tabList: List<TabItem>, // Tab列表（发现/推荐/日报/社区/各分类）
    @SerializedName("defaultIdx")
    val defaultIdx: Int // 默认选中的Tab索引
)

/**
 * description ： 首页单个Tab
 * email : 3014386984@qq.com
 * date : 2026/7/16 10:00
 */
data class TabItem(
    @SerializedName("id")
    val id: Int, // Tab唯一ID（负数为系统Tab，正数为分类Tab）
    @SerializedName("name")
    val name: String, // Tab名称（发现/推荐/广告/生活等）
    @SerializedName("apiUrl")
    val apiUrl: String, // Tab对应的API请求地址
    @SerializedName("tabType")
    val tabType: Int, // Tab类型
    @SerializedName("nameType")
    val nameType: Int // 名称类型
)
