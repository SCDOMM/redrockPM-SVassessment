package com.example.ept.community

/**
 * 社区页数据项
 * 使用密封类（sealed class）表示三种卡片类型
 */
sealed class CommunityItem {
    /**
     * 标题卡片
     * 用于展示话题详情页的头部信息
     * @param title 标题
     * @param description 描述
     * @param headerImage 头图 URL
     */
    data class HeaderCard(
        val title: String,
        val description: String,
        val headerImage: String
    ) : CommunityItem()

    /**
     * 入口卡片
     * 用于展示推荐入口（如主题创作广场、话题讨论大厅）
     * @param title 入口标题
     * @param subTitle 副标题
     * @param bgPicture 背景图 URL
     */
    data class EntryCard(
        val title: String,
        val subTitle: String,
        val bgPicture: String
    ) : CommunityItem()

    /**
     * 内容卡片
     * 用于展示社区中的视频或图片帖子
     * @param id 内容 ID
     * @param nickname 作者昵称
     * @param avatar 作者头像 URL
     * @param coverUrl 封面图 URL（视频封面）
     * @param imageUrls 图片列表（图片帖子）
     * @param description 内容描述
     * @param collectionCount 收藏数
     * @param replyCount 评论数
     * @param isVideo 是否为视频类型
     * @param duration 视频时长（秒）
     * @param playUrl 视频播放地址
     */
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
