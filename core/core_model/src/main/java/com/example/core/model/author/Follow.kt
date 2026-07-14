package com.example.core.model.author

/**
 * 包名称： com.example.core.model
 * 类名称：Follow
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:48
 *
 */
data class Follow(
    val itemType: String?,          // "author" / "pgc"
    val itemId: Long,              // 关注对象ID
    val followed: Boolean           // 是否已关注
)