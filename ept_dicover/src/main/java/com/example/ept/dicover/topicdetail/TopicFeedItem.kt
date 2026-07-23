package com.example.ept.dicover.topicdetail

/**
 * description ： 话题详情页 Feed 项
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
data class TopicFeedItem(
    val id: Long,
    val text: String,
    val coverUrl: String,
    val isVideo: Boolean,
    val authorName: String,
    val authorAvatar: String,
    val likeCount: Int,
    val collectionCount: Int = 0,
    val commentCount: Int,
    val publishTime: String
)
