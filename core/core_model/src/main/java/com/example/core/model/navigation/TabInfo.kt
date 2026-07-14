package com.example.core.model.navigation

/**   
 * 包名称： com.example.core.model
 * 类名称：TabInfo
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:57
 *
 */
data class TabInfo(
    val id: Long,
    val name: String,               // "推荐" / "发现" / "关注"
    val actionUrl: String?,
    val type: String?
)