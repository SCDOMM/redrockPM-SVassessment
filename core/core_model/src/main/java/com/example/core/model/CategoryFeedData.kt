package com.example.core.model

/**
 * description ：分类详情 Feed 数据项
 */
sealed class FeedCategoryItem {
    data class Header(val text: String) : FeedCategoryItem()
    data class Video(
        val videoId: Long,
        val title: String,
        val coverUrl: String,
        val duration: Long,
        val authorName: String,
        val authorIcon: String,
        val description: String,
        val collectionCount: Int = 0,
        val shareCount: Int = 0,
        val replyCount: Int = 0,
        val webUrl: String = ""
    ) : FeedCategoryItem()
    data class Image(
        val id: Long,
        val title: String,
        val imageUrls: List<String>,
        val authorName: String,
        val authorIcon: String,
        val description: String = "",
        val likeCount: Int = 0,
        val commentCount: Int = 0,
        val collectionCount: Int = 0,
        val shareCount: Int = 0
    ) : FeedCategoryItem()
}
