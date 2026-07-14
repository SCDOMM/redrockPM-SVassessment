package com.example.core.model.navigation

/**   
 * 包名称： com.example.core.model.text
 * 类名称：TextFooterData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:54
 *
 */
data class TextFooterData(
    val dataType: String,           // "TextFooter"
    val id: Long,
    val text: String,               // "查看更多"
    val actionUrl: String?          // 跳转链接
)