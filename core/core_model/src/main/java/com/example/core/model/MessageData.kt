package com.example.core.model

/**   
 * 包名称： com.example.core.model.message
 * 类名称：MessageData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 20:10
 *
 */
data class MessageData(
    val id: Long = 0,
    val title: String? = null,
    val content: String = "",
    val date: Long = 0,                      // 时间戳（毫秒）
    val actionUrl: String? = null,
    val icon: String? = null,
    val viewed: Boolean = false,
    val ifPush: Boolean = false,
    val pushStatus: Int = 0,
    val uid: Long? = null
)

/**
 * 消息列表响应
 */
data class MessageListResponse(
    val messageList: List<MessageData> = emptyList(),
    val updateTime: Long? = null,
    val nextPageUrl: String? = null
)