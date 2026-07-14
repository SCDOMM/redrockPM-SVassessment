package com.example.core.model.navigation

/**
 * 包名称： com.example.core.model.general
 * 类名称：TextHeaderData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:53
 *
 */
data class TextHeaderData(
    val dataType: String,           // "TextHeader"
    val id: Long,
    val text: String,               // ⭐ 标题文字（如"今日精选"）
    val subText: String?,           // 副标题
    val font: String?,              // 字体
    val actionUrl: String?,         // 点击跳转
    val adTrack: Any?
)