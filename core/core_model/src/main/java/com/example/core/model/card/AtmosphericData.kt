package com.example.core.model.card

/**   
 * 包名称： com.example.core.model.banner
 * 类名称：AtmosphericData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:57
 *
 */
data class AtmosphericData(
    val dataType: String,           // "Atmospheric"
    val id: Long,
    val image: String?,             // ⭐ 背景大图
    val actionUrl: String?,
    val title: String?,
    val description: String?,
    val textColorType: String?       // "light" / "dark"
)