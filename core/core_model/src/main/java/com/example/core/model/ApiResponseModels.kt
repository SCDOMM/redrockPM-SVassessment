package com.example.core.model

import com.google.gson.annotations.SerializedName


/**
 * 标签列表响应
 */
data class TabListResponse(
    @SerializedName("tabInfo")
    val tabInfo: TabInfo? = null
)

data class TabInfo(
    @SerializedName("tabList")
    val tabList: List<TabItem> = emptyList(),
    @SerializedName("defaultIdx")
    val defaultIdx: Int = 0
)

data class TabItem(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("api_url")
    val apiUrl: String = ""
)
