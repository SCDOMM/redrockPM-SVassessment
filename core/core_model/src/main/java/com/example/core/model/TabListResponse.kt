package com.example.core.model

import com.google.gson.annotations.SerializedName

data class TabListResponse(
    @SerializedName("tabInfo")
    val tabInfo: TabInfoWrapper
)

data class TabInfoWrapper(
    @SerializedName("tabList")
    val tabList: List<TabItem>,
    @SerializedName("defaultIdx")
    val defaultIdx: Int
)

data class TabItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("apiUrl")
    val apiUrl: String,
    @SerializedName("tabType")
    val tabType: Int,
    @SerializedName("nameType")
    val nameType: Int
)
