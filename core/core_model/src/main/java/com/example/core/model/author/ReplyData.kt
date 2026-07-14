package com.example.core.model.author

import com.example.core.model.interact.Consumption
import com.example.core.model.videoEntity.VideoData

/**
 * 包名称： com.example.core.model.interact
 * 类名称：ReplyData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:56
 *
 */
data class ReplyData(
    val dataType: String,           // "Reply"
    val id: Long,
    val content: String?,
    val user: Author?,              // 评论者
    val video: VideoData?,          // 关联视频
    val consumption: Consumption?,
    val createTime: Long?
)