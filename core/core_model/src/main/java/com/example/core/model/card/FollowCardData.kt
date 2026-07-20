package com.example.core.model.card

import com.example.core.model.author.Follow
import com.example.core.model.navigation.TextHeaderData
import com.example.core.model.videoEntity.VideoData

/**
 * 包名称： com.example.core.model.banner
 * 类名称：FollowCardData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:55
 *
 */
data class FollowCardData(
    val dataType: String,           // "FollowCard"
    val id: Long,
    val content: VideoData?,        // 内容视频
    val header: TextHeaderData?,    // 标题
    val follow: Follow?
)