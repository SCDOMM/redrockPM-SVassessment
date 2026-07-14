package com.example.core.model.author

import com.example.core.model.videoEntity.VideoData

/**
 * 包名称： com.example.core.model.banner
 * 类名称：AuthorRecommendationCardData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:57
 *
 */
data class AuthorRecommendationCardData(
    val dataType: String,           // "AuthorRecommendationCard"
    val id: Long,
    val author: Author?,
    val videoNum: Int?,
    val latestVideo: VideoData?,
    val description: String?,
    val actionUrl: String?
)