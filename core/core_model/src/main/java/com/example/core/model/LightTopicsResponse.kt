package com.example.core.model

/**
 * lightTopics 接口响应体
 * GET v3/lightTopics/internal/{topicId}
 */
data class LightTopicsResponse(
    val id: Int = 0,
    val headerImage: String = "",
    val brief: String = "",
    val text: String = "",
    val shareLink: String = "",
    val itemList: List<Item> = emptyList(),
    val count: Int = 0,
    val adTrack: Any? = null
)
