package com.example.core.model.author

/**
 * 包名称： com.example.core.model.general
 * 类名称：Shield
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:50
 *
 */
data class Shield(
    val itemType: String?,          // "author"
    val itemId: Long,              // 屏蔽对象ID
    val shielded: Boolean           // 是否已屏蔽
)