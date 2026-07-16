package com.example.core.model.videoEntity

import com.example.core.model.author.*
import com.example.core.model.interact.*
import com.example.core.model.media.*
import com.example.core.model.tag.*

/**
 * 包名称： com.example.core.model.banner
 * 类名称：VideoSmallCardData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:55
 *
 */
data class VideoSmallCardData(
    val dataType: String,           // "VideoSmallCard"
    val id: Long,
    val title: String,
    val description: String?,
    val category: String?,          // 分类（创意/旅行/记录/搞笑/...）
    val cover: Cover,
    val playUrl: String?,
    val playInfo: List<PlayInfo>?,
    val duration: Long,
    val author: Author?,
    val consumption: Consumption?,
    val webUrl: WebUrl?,
    val actionUrl: String?,
    val tags: List<Tag>?
)