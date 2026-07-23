package com.example.core.common

import com.example.core.model.AlbumData
import com.example.core.model.AlbumVideoPreview
import com.example.core.model.ApiResponse
import com.example.core.model.Card
import com.example.core.model.MetroData
import com.example.core.model.MetroItem
import com.example.core.model.PageResult

/**   
 * 包名称： com.example.core.common
 * 类名称：parseUserUtil
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-23 12:04
 *
 */

fun parseUserItemFromCard(response: ApiResponse<PageResult>): List<UserHomeItem> {
    return response.result?.cardList.orEmpty()
        .flatMap { card -> parseCardToItems(card) }
}

// 根据卡片类型分发到对应的处理函数
private fun parseCardToItems(card: Card): List<UserHomeItem> {
    return when (card.type) {
        "set_metro_list" -> parseSetMetroListCard(card)
        "set_slide_metro_list" -> parseSetSlideMetroListCard(card)
        else -> emptyList()
    }
}

// 处理 "set_metro_list" 类型的卡片
private fun parseSetMetroListCard(card: Card): List<UserHomeItem> {
    val items = mutableListOf<UserHomeItem>()
    // 检查是否有标题头（第一个元素为 text）
    val headerLefts = card.cardData?.header?.left
    if (!headerLefts.isNullOrEmpty() && headerLefts.firstOrNull()?.type == "text") {
        val title = headerLefts.first().metroData?.text ?: ""
        val moreLink = card.cardData?.header?.right?.firstOrNull()?.metroData?.link
        items.add(UserHomeItem.SectionTitle(title, moreLink))
        return items
    }
    // 否则尝试解析正文中的 "item" 或 "video" 内容
    val bodyItems = card.cardData?.body?.metroList
    if (!bodyItems.isNullOrEmpty() && bodyItems.firstOrNull()?.type == "item") {
        for (metroItem in bodyItems) {
            val data = metroItem.metroData ?: continue
            if (data.resourceType in listOf(
                    "ugc_picture", "pgc_picture",
                    "ugc_video", "pgc_video"
                )
            ) {
                items.add(UserHomeItem.VideoPopular(data))
            }
        }
    }
    return items
}

// 处理 "set_slide_metro_list" 类型的卡片
private fun parseSetSlideMetroListCard(card: Card): List<UserHomeItem> {
    val items = mutableListOf<UserHomeItem>()
    val hasFooter = !card.cardData?.footer?.left.isNullOrEmpty()
    // 无 footer -> 最近更新的视频
    if (!hasFooter) {
        val videoItems = card.cardData?.body?.metroList?.mapNotNull { item ->
            item.metroData?.takeIf { d ->
                d.resourceType in listOf("pgc_video", "ugc_video")
            }
        } ?: emptyList()
        if (videoItems.isNotEmpty()) {
            items.add(UserHomeItem.VideoRecent(videoItems))
        }
        return items
    }
    // 有 footer -> 专辑卡片
    val album = extractAlbumFromCard(card)
    if (album != null) {
        items.add(UserHomeItem.Album(album, album.videos))
    }
    return items
}

fun parseAlbumCards(cardList: List<Card>)=cardList
    .filter { it.type == "set_slide_metro_list" }
    .mapNotNull { extractAlbumFromCard(it) }
private fun extractAlbumFromCard(card: Card): AlbumData? {
    val albumMeta = card.cardData?.footer?.left
        ?.firstOrNull { it is MetroItem && it.type == "user" }
        ?.let { it as MetroItem }?.metroData ?: return null
    val videoPreviews = extractAlbumVideoPreviews(card)
    return AlbumData(
        albumName = albumMeta.nick ?: "未知专辑",
        albumDescription = albumMeta.description ?: "",
        albumCoverUrl = albumMeta.avatar?.url ?: "",
        albumLink = albumMeta.link ?: "",
        videos = videoPreviews
    )
}

fun extractAlbumVideoPreviews(card: Card): List<AlbumVideoPreview> {
    return card.cardData?.body?.metroList
        ?.filter { it.type == "video" }
        ?.map { video ->
            AlbumVideoPreview(
                videoId = video.metroData?.videoId ?: "",
                title = video.metroData?.title ?: "",
                coverUrl = video.metroData?.cover?.url ?: "",
                duration = video.metroData?.duration?.value ?: 0
            )
        } ?: emptyList()
}

sealed class UserHomeItem {
    data class SectionTitle(val text: String, val moreLink: String? = null) : UserHomeItem()

    data class VideoRecent(val videoItems: List<MetroData>) : UserHomeItem()

    data class VideoPopular(val data: MetroData) : UserHomeItem()

    data class Album(val albumData: AlbumData, val videoPreviews: List<AlbumVideoPreview>) :
        UserHomeItem()
}
