package com.example.core.model

import com.google.gson.annotations.SerializedName


/**
 * 标签列表响应
 */
data class TabListResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: TabListResult? = null
)

data class TabListResult(
    @SerializedName("tab_list")
    val tabList: List<TabItem> = emptyList()
)

data class TabItem(
    val id: String = "",
    val name: String = "",
    @SerializedName("api_url")
    val apiUrl: String = ""
)
