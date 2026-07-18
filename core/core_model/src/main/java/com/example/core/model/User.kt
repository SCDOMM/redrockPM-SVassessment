package com.example.core.model

/**
 * 包名称： com.example.core.model
 * 类名称：Author
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:48
 *
 */
data class Author(
    val id: Long = 0,                       // 作者ID
    val icon: String = "",                  // ⭐ 头像地址
    val name: String = "",                  // ⭐ 昵称
    val description: String? = null,        // 描述
    val link: String? = null,               // 链接
    val latestReleaseTime: Long? = null,    // 最新发布时间
    val videoNum: Int? = null,              // 视频数量
    val adTrack: Any? = null,
    val follow: Follow? = null,             // 关注信息
    val shield: Shield? = null,             // 屏蔽信息
    val approvedNotReadyVideoCount: Int? = null,
    val ifPgc: Boolean? = null,             // 是否PGC
    val recSort: Int? = null,
    val expert: Boolean? = null             // 是否专家
)
/**
 * 关注信息
 */
data class Follow(
    val itemType: String? = null,           // "author" / "tag"
    val itemId: Long? = null,               // 关注对象ID
    val followed: Boolean = false           // 是否已关注
)
/**
 * 屏蔽信息
 */
data class Shield(
    val itemType: String? = null,           // "author"
    val itemId: Long? = null,               // 屏蔽对象ID
    val shielded: Boolean = false           // 是否已屏蔽
)
/**
 * 互动消费数据
 */
data class Consumption(
    val collectionCount: Int = 0,           // ⭐ 收藏数
    val shareCount: Int = 0,                // ⭐ 分享数
    val replyCount: Int = 0,                // ⭐ 评论数
    val realCollectionCount: Int? = null     // 真实收藏数
)
/**
 * 作者推荐卡片
 */
data class AuthorRecommendationCardData(
    val dataType: String = "",               // "AuthorRecommendationCard"
    val id: Long = 0,
    val author: Author? = null,
    val videoNum: Int? = null,
    val latestVideo: VideoData? = null,
    val description: String? = null,
    val actionUrl: String? = null
)
/**
 * 评论数据
 */
data class ReplyData(
    val dataType: String = "",               // "Reply"
    val id: Long = 0,
    val content: String? = null,
    val user: Author? = null,                // 评论者
    val video: VideoData? = null,            // 关联视频
    val consumption: Consumption? = null,
    val createTime: Long? = null
)