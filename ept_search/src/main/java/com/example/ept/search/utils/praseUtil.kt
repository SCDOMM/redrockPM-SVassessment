package com.example.ept.search.utils

import com.example.core.model.*

/**   
 * 包名称： com.example.ept.search.utils
 * 类名称：praseUtil
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 15:26
 *
 */
data class SearchParsedResult(
    val videoList: List<MetroData>,
    val authorList: List<MetroData>,
    val graphicList: List<MetroData>,
    val topicList: List<MetroData>,
    val userList: List<MetroData>
)
fun parseSearchResponse(response: SearchResponse): SearchParsedResult {
    val videoList = mutableListOf<MetroData>()
    val authorList = mutableListOf<MetroData>()
    val graphicList = mutableListOf<MetroData>()
    val topicList = mutableListOf<MetroData>()
    val userList = mutableListOf<MetroData>()
    response.result?.item_list?.forEach { category ->
        val navType = category.nav?.type ?: return@forEach

        val metroItems = category.card_list.flatMap { card ->
            card.card_data?.body?.metro_list ?: emptyList()
        }
        when (navType) {
            "video" -> {
                // 只取 type == "video" 的 metro 项（可能还有别的 type，但通常都是 video）
                videoList.addAll(metroItems.filter { it.type == "video" }.mapNotNull { it.metroData })
            }
            "pgc" -> {
                // 作者栏目，type 为 "user"
                authorList.addAll(metroItems.filter { it.type == "user" }.mapNotNull { it.metroData })
            }
            "graphic" -> {
                // 图文栏目，type 为 "image"
                graphicList.addAll(metroItems.filter { it.type == "image" }.mapNotNull { it.metroData })
            }
            "topic" -> {
                // 话题栏目，type 为 "topic"
                topicList.addAll(metroItems.filter { it.type == "topic" }.mapNotNull { it.metroData })
            }
            "ugc" -> {
                // 普通用户栏目，type 为 "user"
                userList.addAll(metroItems.filter { it.type == "user" }.mapNotNull { it.metroData })
            }
        }
    }
    return SearchParsedResult(
        videoList = videoList,
        authorList = authorList,
        graphicList = graphicList,
        topicList = topicList,
        userList = userList
    )
}
fun findDelimiterIndex(text: String?, delimiter: String): Int {
    if (text==null)return -1
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