package com.example.core.model

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：NoticeResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 15:02
 *
 */

data class ApiResponse<T>(
    val code: Int = 0,
    val message: Any? = null,
    val result: T? = null
)
/**
 * 单条通知消息
 */
data class NoticeItem(
    val id: String = "",
    val type: String = "",           // "single"
    @SerializedName("avatar_list")
    val avatarList: List<String> = emptyList(),
    val title: String = "",
    val desc: String = "",
    val time: String = "",           // 日期字符串 "2025/07/18"
    val unread: Boolean = false,
    val link: String = "",
    @SerializedName("tracking_data")
    val trackingData: Any? = null
)