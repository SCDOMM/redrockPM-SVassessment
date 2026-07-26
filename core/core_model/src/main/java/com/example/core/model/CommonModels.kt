package com.example.core.model

/**
 * description ：通用解析结果数据
 */
data class WorkListResult(
    val title: String?,
    val items: List<MetroData>
)

data class AlbumSection(
    val title: String?,
    val albums: List<AlbumData>
)

sealed class UserHomeItem {
    data class SectionTitle(val text: String, val moreLink: String? = null) : UserHomeItem()
    data class VideoRecent(val videoItems: List<MetroData>) : UserHomeItem()
    data class VideoPopular(val data: MetroData) : UserHomeItem()
    data class Album(val albumData: AlbumData, val videoPreviews: List<AlbumVideoPreview>) : UserHomeItem()
}
