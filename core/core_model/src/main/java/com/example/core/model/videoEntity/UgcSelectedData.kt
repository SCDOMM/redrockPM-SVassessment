package com.example.core.model.videoEntity

import com.example.core.model.author.*
import com.example.core.model.interact.*
import com.example.core.model.media.*
import com.example.core.model.tag.*


/**
 * 包名称： com.example.core.model.interact
 * 类名称：UgcSelectedData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:56
 *
 */
data class UgcSelectedData(
    val dataType: String,           // "UgcSelected"
    val id: Long,
    val title: String?,
    val description: String?,
    val cover: Cover?,
    val playUrl: String?,
    val duration: Long?,
    val author: Author?,
    val consumption: Consumption?,
    val webUrl: WebUrl?,
    val tags: List<Tag>?
)