package com.example.core.model.navigation

/**   
 * 包名称： com.example.core.model
 * 类名称：NavigationData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:57
 *
 */
data class NavigationData(
    val dataType: String,           // "Navigation"
    val id: Long,
    val tabList: List<TabInfo>?
)