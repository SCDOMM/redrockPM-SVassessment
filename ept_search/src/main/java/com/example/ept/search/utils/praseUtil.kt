package com.example.ept.search.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.core.model.*
import com.example.core.network.RetrofitClient.gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

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
    val creatorList: List<MetroData> = emptyList(),
    val articleList: List<MetroData> = emptyList(),
    val topicList: List<MetroData> = emptyList(),
    val userList: List<MetroData> = emptyList(),
    val query: String
)
fun parseSearchResponse(response: SearchResponse,query: String): SearchResultData {
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
        userList = userList,
        query
    )
}
fun parseSearchResponseV2(response: SearchResponseV2): SearchResultData {
    val videoList = mutableListOf<MetroData>()
    val creatorList = mutableListOf<MetroData>()
    val articleList = mutableListOf<MetroData>()
    val topicList = mutableListOf<MetroData>()
    val userList = mutableListOf<MetroData>()
    response.result?.item_list?.forEach { item ->
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
        creatorList = creatorList,
        articleList = articleList,
        topicList = topicList,
        userList = userList,
        ""
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
fun getHistoryFromPrefs(context: Context): List<String> {
    val prefs=context.getSharedPreferences("searchHistory", 0)
    val json = prefs.getString("search_history", null) ?: return emptyList()
    return try {
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}
fun saveHistoryToPrefs(list: List<String>,context: Context) {
    val prefs=context.getSharedPreferences("searchHistory", 0)
    prefs.edit { putString("search_history", gson.toJson(list)) }
}
fun addSearchHistory(context: Context,query: String){
    if (query.isBlank()) return
    val currentList = getHistoryFromPrefs(context).toMutableList()
    currentList.remove(query)
    currentList.add(0, query)
    val newList = if (currentList.size > 10) currentList.subList(0, 10) else currentList
    saveHistoryToPrefs(newList,context)
}
fun clearSearchHistory(context: Context) {
    saveHistoryToPrefs(emptyList(),context)
}