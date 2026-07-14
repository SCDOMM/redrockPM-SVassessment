package com.example.core.model

import com.google.gson.annotations.SerializedName

data class RankListResponse(
    @SerializedName("tabInfo")
    val tabInfo: RankTabInfo
)

data class RankTabInfo(
    @SerializedName("tabList")
    val tabList: List<RankTab>,
    @SerializedName("defaultIdx")
    val defaultIdx: Int
)

data class RankTab(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("apiUrl")
    val apiUrl: String
)
