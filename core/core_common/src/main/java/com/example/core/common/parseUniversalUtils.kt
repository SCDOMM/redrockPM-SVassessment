package com.example.core.common

import android.util.Log
import com.example.core.model.ApiResponse
import com.example.core.model.MetroData
import com.example.core.model.PageResult

/**   
 * 包名称： com.example.ept.daily
 * 类名称：parseUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 17:20
 *
 */
private inline fun extractMetroDataFromPageResult(
    response: ApiResponse<PageResult>,
    cardTypeFilter: (String) -> Boolean = { true },
    itemTypeFilter: (String) -> Boolean = { true },
    dataFilter: (MetroData) -> Boolean = { true }
): List<MetroData> {
    val result = mutableListOf<MetroData>()
    response.result?.cardList?.forEach { card ->
        if (cardTypeFilter(card.type.orEmpty())) {
            card.cardData?.body?.metroList?.forEach { metro ->
                if (itemTypeFilter(metro.type.orEmpty())) {
                    metro.metroData?.takeIf(dataFilter)?.let { result.add(it) }
                }
            }
        }
    }
    return result
}

data class WorkListResult(
    val title: String?,
    val items: List<MetroData>
)

fun parseVideosFromCardList(response: ApiResponse<PageResult>) = extractMetroDataFromPageResult(
    response,
    cardTypeFilter = { it == "set_metro_list" },
    itemTypeFilter = { it == "video" }
)

fun parseWorkListFromCardList(response: ApiResponse<PageResult>): WorkListResult {
    var title: String? = null
    if (response.result?.cardList?.firstOrNull()?.type == "set_metro_list") {
        response.result?.cardList?.firstOrNull()?.cardData?.header?.left?.firstOrNull()
            ?.let { headerItem ->
                if (headerItem.type == "text") {
                    title = headerItem.metroData?.text
                }
            }
    }
    val list = extractMetroDataFromPageResult(
        response,
        itemTypeFilter = { it in listOf("item", "video") },
        dataFilter = { data ->
            data.resourceType in listOf("ugc_picture", "pgc_picture", "ugc_video", "pgc_video")
        }
    )
    return WorkListResult(title, list)
}


fun findDelimiterIndex(text: String?, delimiter: String): Int {
    if (text == null) return -1
    val idx = text.indexOf(delimiter)
    if (idx >= 0) return idx
    val variants = listOf(
        "$delimiter ",    // "- "
        "$delimiter\n",   // "-\n"
        "\n$delimiter",   // "\n-"
        " $delimiter",    // " -"
        " $delimiter ",   // " - "
        "\n$delimiter\n"  // "\n-\n"
    )
    for (variant in variants) {
        val vIdx = text.indexOf(variant)
        if (vIdx >= 0) {
            return if (variant.startsWith(" ")) {
                vIdx + 1
            } else {
                vIdx
            }
        }
    }
    return -1
}
