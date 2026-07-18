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
data class SearchResultData(
    val videoList: List<MetroData> = emptyList(),
    val creatorList: List<MetroData> = emptyList(),   // 作者（pgc user）
    val articleList: List<MetroData> = emptyList(),   // 图文（image）
    val topicList: List<MetroData> = emptyList(),     // 话题（topic）
    val userList: List<MetroData> = emptyList(),
)
fun parseSearchResponse(response: SearchResponse): SearchResultData {
    val videoList = mutableListOf<MetroData>()
    val creatorList = mutableListOf<MetroData>()
    val articleList = mutableListOf<MetroData>()
    val topicList = mutableListOf<MetroData>()
    val userList = mutableListOf<MetroData>()
    response.result?.item_list?.forEach { category ->
        val navType = category.nav?.type ?: return@forEach
        val metroItems = category.card_list.flatMap { card ->
            card.card_data?.body?.metro_list ?: emptyList()
        }
        when (navType) {
            "video" -> videoList.addAll(metroItems.filter { it.type == "video" }.mapNotNull { it.metroData })
            "pgc"   -> creatorList.addAll(metroItems.filter { it.type == "user" }.mapNotNull { it.metroData })
            "graphic" -> articleList.addAll(metroItems.filter { it.type == "image" }.mapNotNull { it.metroData })
            "topic" -> topicList.addAll(metroItems.filter { it.type == "topic" }.mapNotNull { it.metroData })
            "ugc"   -> userList.addAll(metroItems.filter { it.type == "user" }.mapNotNull { it.metroData })
        }
    }
    return SearchResultData(
        videoList = videoList,
        creatorList = creatorList,
        articleList = articleList,
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