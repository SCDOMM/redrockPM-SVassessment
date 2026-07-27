package com.example.core.model

/**
 * description ：发现页-分类数据项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class DiscoverCategoryItem(
    val name: String,
    val pageLabel: String,
    val iconUrl: String
)

/**
 * description ：发现页-话题广场和主题播单数据项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class TopicItem(
    val id: Long,
    val title: String,
    val description: String,
    val icon: String,
    val actionUrl: String
)

/**
 * description ：主题播单播放列表项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class LightTopicItem(
    val topicId: Int,
    val title: String,
    val description: String,
    val videos: List<LightTopicPlaylistVideo>
)

/**
 * description ：主题播单播放列表视频项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class LightTopicPlaylistVideo(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String,
    val description: String,
    val playUrl: String
)

/**
 * description ：话题详情 Feed 项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class TopicFeedItem(
    val id: Long,
    val videoId: String,
    val resourceType: String,
    val text: String,
    val coverUrl: String,
    val imageUrls: List<String>,
    val isVideo: Boolean,
    val authorName: String,
    val authorAvatar: String,
    val likeCount: Int,
    val collectionCount: Int = 0,
    val commentCount: Int,
    val publishTime: String
)

/**
 * description ：话题标签信息
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class TopicTagInfo(
    val title: String,
    val headerImage: String,
    val description: String,
    val stats: String,
    val feedPageLabels: List<Pair<String, String>>
)

/**
 * description ：话题广场列表项
 * email : 3014386984@qq.com
 * date : 2026/7/26 17:57
 */
data class TopicSquareListItem(
    val id: Long,
    val title: String,
    val description: String,
    val coverUrl: String,
    val participantCount: String,
    val pageLabel: String
)
