package com.example.core.model

import com.google.gson.annotations.SerializedName

/**
 * 通用 Item 模型，用于 lightTopics 等接口
 */
data class Item(
    val type: String = "",
    val data: Any? = null
)

/**
 * autoPlayFollowCard 类型的 data 字段
 */
data class FollowCardData(
    val header: FollowCardHeader? = null,
    val content: Any? = null
) {
    /** 从 content 中提取视频数据 */
    fun getVideoData(): VideoData? {
        val map = when (content) {
            is Map<*, *> -> content
            else -> return null
        }
        // content 可能直接是视频字段，或包裹在 data 中
        val videoMap = map["data"] as? Map<*, *> ?: map
        return parseVideoMap(videoMap)
    }

    private fun parseVideoMap(map: Map<*, *>): VideoData? {
        return try {
            val id = when (val v = map["id"]) {
                is Number -> v.toLong()
                is String -> v.toLongOrNull() ?: 0L
                else -> 0L
            }
            val title = map["title"] as? String ?: ""
            if (title.isEmpty()) return null

            val coverMap = map["cover"] as? Map<*, *>
            val authorMap = map["author"] as? Map<* , *>

            val duration = map["duration"]

            VideoData(
                id = id,
                title = title,
                cover = if (coverMap != null) VideoCover(feed = coverMap["feed"] as? String ?: "") else null,
                duration = duration,
                author = if (authorMap != null) VideoAuthor(
                    name = authorMap["name"] as? String ?: "",
                    icon = authorMap["icon"] as? String ?: ""
                ) else null,
                description = map["description"] as? String ?: "",
                playUrl = map["play_url"] as? String ?: map["playUrl"] as? String ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class FollowCardHeader(
    @SerializedName("issuerName")
    val issuerName: String? = null,
    val icon: String? = null
)

data class VideoData(
    val id: Long = 0,
    val title: String = "",
    val cover: VideoCover? = null,
    val duration: Any? = null,
    val author: VideoAuthor? = null,
    val description: String = "",
    @SerializedName("play_url")
    val playUrl: String = ""
) {
    fun getDurationLong(): Long {
        return when (duration) {
            is Number -> duration.toLong()
            is Map<*, *> -> (duration["value"] as? Number)?.toLong() ?: 0L
            else -> 0L
        }
    }
}

data class VideoCover(
    val feed: String = ""
)

data class VideoAuthor(
    val name: String = "",
    val icon: String = ""
)

/**
 * lightTopics 接口响应模型
 */
data class LightTopicsResponse(
    val headerImage: String? = null,
    val brief: String? = null,
    val text: String? = null,
    val itemList: List<Item> = emptyList()
)
