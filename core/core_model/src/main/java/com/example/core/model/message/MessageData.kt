package com.example.core.model.message

/**   
 * 包名称： com.example.core.model.message
 * 类名称：MessageData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 20:10
 *
 */
data class MessageData(
    val id: Long,               // ⭐ 消息唯一ID
    val title: String?,         // ⭐【标题】"官方通知" / "点赞通知" / "评论通知" / null
    val content: String,        // ⭐【消息正文/描述】如"影史获奖电影绝美镜头合集"
    val date: Long,             // ⭐【日期】时间戳（毫秒），如 1625204370116
    val actionUrl: String?,     // ⭐【跳转链接】点击后跳转到视频/活动/网页
    val icon: String?,          // ⭐【头像URL】官方消息→眼睛logo，用户消息→用户头像
    val viewed: Boolean,        // 是否已读（已读/未读状态标记）
    val ifPush: Boolean,        // 是否推送消息
    val pushStatus: Int,        // 推送状态 0=未推送
    val uid: Long?              // 关联用户ID（自己的互动消息时有值）
)