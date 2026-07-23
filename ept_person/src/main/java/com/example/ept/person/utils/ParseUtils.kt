package com.example.ept.person.utils

import com.example.core.model.AlbumData
import com.example.core.model.AlbumVideoPreview
import com.example.core.model.MetroItem
import com.example.core.model.Card
import com.example.core.model.ApiResponse
import com.example.core.model.MetroData
import com.example.core.model.PageResult

/**   
 * 包名称： com.example.ept.person.utils
 * 类名称：parseUtils
 * 类描述：解析API返回的数据
 * 创建人：韦西波
 * 创建时间：2026-07-21 17:07
 *
 */
fun parseAlbumCards(cardList: List<Card>): List<AlbumData> {
    return cardList
        .filter { it.type == "set_slide_metro_list" }
        .map { card ->
            val albumMeta = card.cardData?.footer?.left
                ?.firstOrNull { it is MetroItem && (it as MetroItem).type == "user" }
                ?.let { it as MetroItem }?.metroData
            val videoPreviews = card.cardData?.body?.metroList
                ?.filter { it.type == "video" }
                ?.map { video ->
                    AlbumVideoPreview(
                        videoId = video.metroData?.videoId ?: "",
                        title = video.metroData?.title ?: "",
                        coverUrl = video.metroData?.cover?.url ?: "",
                        duration = video.metroData?.duration?.value ?:0
                    )
                } ?: emptyList()
            AlbumData(
                albumName = albumMeta?.nick ?: "未知专辑",
                albumDescription = albumMeta?.description ?: "",
                albumCoverUrl = albumMeta?.avatar?.url ?: "",
                albumLink = albumMeta?.link ?: "",
                videos = videoPreviews
            )
        }
}
fun parseWorkDataList(response: ApiResponse<PageResult>): List<MetroData> {
    val result = mutableListOf<MetroData>()
    response.result?.cardList?.forEach { card ->
        val metroList = card.cardData?.body?.metroList ?: return@forEach
        for (metroItem in metroList) {
            if (metroItem.type != "item" && metroItem.type != "video") continue
            val data = metroItem.metroData ?: continue
            val resourceType = data.resourceType
            if (resourceType.isNullOrBlank()) continue
            val validTypes = listOf(
                "ugc_picture", "pgc_picture",
                "ugc_video", "pgc_video"
            )
            if (resourceType !in validTypes) continue
            result.add(data)
        }
    }
    return result
}
sealed class UserHomeItem {
    data class Title(val text: String, val moreLink: String? = null) : UserHomeItem()

    data class SlideVideoGroup(val videoItems: List<MetroData>) : UserHomeItem()

    data class SingleContent(val data: MetroData) : UserHomeItem()

    data class AlbumCard(val albumData: AlbumData, val videoPreviews: List<AlbumVideoPreview>) : UserHomeItem()
}
fun parseCardForAdapter(response: ApiResponse<PageResult>): List<UserHomeItem> {
    val result = mutableListOf<UserHomeItem>()
    val cards = response.result?.cardList ?: return result
    var i = 0
    while (i < cards.size) {
        val card = cards[i]
        when (card.type) {
            "set_metro_list" -> {
                val headerLefts = card.cardData?.header?.left
                if (!headerLefts.isNullOrEmpty() && headerLefts.firstOrNull()?.type == "text") {
                    val title = headerLefts.first()?.metroData?.text ?: ""
                    val moreLink = card.cardData?.header?.right?.firstOrNull()?.metroData?.link
                    result.add(UserHomeItem.Title(title, moreLink))
                    i++
                    continue
                }
                val bodyItems = card.cardData?.body?.metroList
                if (!bodyItems.isNullOrEmpty() && bodyItems.firstOrNull()?.type == "item") {
                    for (metroItem in bodyItems) {
                        val data = metroItem.metroData ?: continue
                        if (data.resourceType in listOf(
                                "ugc_picture", "pgc_picture",
                                "ugc_video", "pgc_video"
                            )
                        ) {
                            result.add(UserHomeItem.SingleContent(data))
                        }
                    }
                    i++
                    continue
                }
                i++
            }

            "set_slide_metro_list" -> {
                val hasFooter = !card.cardData?.footer?.left.isNullOrEmpty()
                if (!hasFooter) {
                    val videoItems = card.cardData?.body?.metroList?.mapNotNull { item ->
                        item.metroData?.takeIf { d ->
                            d.resourceType in listOf("pgc_video", "ugc_video")
                        }
                    } ?: emptyList()
                    if (videoItems.isNotEmpty()) {
                        result.add(UserHomeItem.SlideVideoGroup(videoItems))
                    }
                    i++
                    continue
                }
                val albumInfo = card.cardData?.footer?.left
                    ?.firstOrNull { it.type == "user" }?.metroData ?: run { i++; continue }
                val videoPreviews = card.cardData?.body?.metroList?.mapNotNull { item ->
                    val md = item.metroData ?: return@mapNotNull null
                    AlbumVideoPreview(
                        videoId = md.videoId ?: "",
                        title = md.title ?: "",
                        coverUrl = md.cover?.url ?: "",
                        duration = md.duration?.value ?: 0
                    )
                } ?: emptyList()
                val album = AlbumData(
                    albumName = albumInfo.nick ?: "未知专辑",
                    albumDescription = albumInfo.description ?: "",
                    albumCoverUrl = albumInfo.avatar?.url ?: "",
                    albumLink = albumInfo.link ?: "",
                    videos = videoPreviews
                )
                result.add(UserHomeItem.AlbumCard(album, videoPreviews))
                i++
            }

            else -> i++
        }
    }
    return result
}