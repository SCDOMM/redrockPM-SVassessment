package com.example.core.model.interact

/**
 * 包名称： com.example.core.model.author
 * 类名称：Consumption
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:50
 *
 */
data class Consumption(
    val collectionCount: Int,       // ⭐ 收藏数
    val shareCount: Int,            // ⭐ 分享数
    val replyCount: Int,            // ⭐ 评论数
    val realCollectionCount: Int?,  // 真实收藏数
    val realShareCount: Int?,       // 真实分享数
    val realReplyCount: Int?        // 真实评论数
)