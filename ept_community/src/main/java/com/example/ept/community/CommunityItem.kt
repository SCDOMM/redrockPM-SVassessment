package com.example.ept.community

/**
 * description ： 社区页数据项
 * email : 3014386984@qq.com
 * date : 2026/7/17  18:15
 */
sealed class CommunityItem {
    /** 话题详情页 header（头图 + 标题 + 描述） */
    data class HeaderCard(
        val title: String,
        val description: String,
        val headerImage: String
    ) : CommunityItem()

    /** 入口卡片（主题创作广场、话题讨论大厅） */
    data class EntryCard(
        val title: String,
        val subTitle: String,
        val bgPicture: String
    ) : CommunityItem()

    /** 社区内容卡片（视频或图片） */
    data class ContentCard(
        val id: Long,
        val nickname: String,
        val avatar: String,
        val coverUrl: String,
        val imageUrls: List<String>,
        val description: String,
        val collectionCount: Int,
        val replyCount: Int,
        val isVideo: Boolean,
        val duration: Long,
        val playUrl: String
    ) : CommunityItem()
}
