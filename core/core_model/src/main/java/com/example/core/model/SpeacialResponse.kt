package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * 包名称： com.example.core.model.utils
 * 类名称：SpeacialResponse
 * 类描述：特殊数据类
 * 创建人：韦西波
 * 创建时间：2026-07-23 10:46
 *
 */

data class NoticeItem(
    val id: String = "",
    val type: String = "",
    @SerializedName("avatar_list")
    val avatarList: List<String> = emptyList(),
    val title: String = "",
    val desc: String = "",
    val time: String = "",
    val unread: Boolean = false,
    val link: String = "",
    @SerializedName("tracking_data")
    val trackingData: Any? = null
)