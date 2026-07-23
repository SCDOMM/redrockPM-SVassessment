package com.example.core.common

import com.example.core.model.ApiResponse
import com.example.core.model.MetroData
import com.example.core.model.MetroItem
import com.example.core.model.PageResult
import com.example.core.model.PaginatedResult

/**   
 * 包名称： com.example.core.common
 * 类名称：parseSearchUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-23 12:00
 *
 */
fun parseSearch(
    response: ApiResponse<PaginatedResult<PageResult>>,
    query: String
): SearchResultData {
    val videoList = mutableListOf<MetroData>()
    val creatorList = mutableListOf<MetroData>()
    val articleList = mutableListOf<MetroData>()
    val topicList = mutableListOf<MetroData>()
    val userList = mutableListOf<MetroData>()
    response.result?.itemList?.forEach { category ->
        val navType = category.nav?.type ?: return@forEach
        val metroItems = category.cardList.flatMap { card ->
            card.cardData?.body?.metroList ?: emptyList()
        }
        when (navType) {
            "video" -> videoList.addAll(metroItems.filter { it.type == "video" }
                .mapNotNull { it.metroData })

            "pgc" -> creatorList.addAll(metroItems.filter { it.type == "user" }
                .mapNotNull { it.metroData })

            "graphic" -> articleList.addAll(metroItems.filter { it.type == "image" }
                .mapNotNull { it.metroData })

            "topic" -> topicList.addAll(metroItems.filter { it.type == "topic" }
                .mapNotNull { it.metroData })

            "ugc" -> userList.addAll(metroItems.filter { it.type == "user" }
                .mapNotNull { it.metroData })
        }
    }
    return SearchResultData(
        videoList = videoList,
        pgcList = creatorList,
        graphicList = articleList,
        topicList = topicList,
        ugcList = userList,
        query
    )
}

fun parseLoadSearch(response: ApiResponse<PaginatedResult<MetroItem>>): SearchResultData {
    val videoList = mutableListOf<MetroData>()
    val creatorList = mutableListOf<MetroData>()
    val articleList = mutableListOf<MetroData>()
    val topicList = mutableListOf<MetroData>()
    val userList = mutableListOf<MetroData>()
    response.result?.itemList?.forEach { item ->
        val data = item.metroData ?: return@forEach
        when (item.type) {
            "video" -> videoList.add(data)          // 视频 -> videoList
            "image" -> articleList.add(data)        // 图文 -> articleList
            "topic" -> topicList.add(data)          // 话题 -> topicList
            "user" -> {                             // 用户需要进一步区分
                when (data.type) {
                    "pgc" -> creatorList.add(data)  // pgc 创作者 -> creatorList
                    "ugc" -> userList.add(data)     // ugc 用户 -> userList
                    else -> userList.add(data)      // 未知类型默认放入 userList
                }
            }
        }
    }
    return SearchResultData(
        videoList = videoList,
        pgcList = creatorList,
        graphicList = articleList,
        topicList = topicList,
        ugcList = userList,
        ""
    )
}
data class SearchResultData(
    val videoList: List<MetroData> = emptyList(),
    val pgcList: List<MetroData> = emptyList(),
    val graphicList: List<MetroData> = emptyList(),
    val topicList: List<MetroData> = emptyList(),
    val ugcList: List<MetroData> = emptyList(),
    val query: String
)