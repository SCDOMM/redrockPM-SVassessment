package com.example.core.model.message

/**   
 * 包名称： com.example.core.model.message
 * 类名称：MessageListResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 20:11
 *
 */
data class MessageListResponse(
    val messageList: List<MessageData>,  // ⭐ 消息列表
    val updateTime: Long?,               // 最后更新时间戳
    val nextPageUrl: String?             // ⭐ 下一页URL（分页用）
)