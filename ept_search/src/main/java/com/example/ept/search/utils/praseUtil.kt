package com.example.ept.search.utils

import android.content.Context
import com.example.core.model.*
import com.example.core.model.SearchResultData
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

fun getHistoryFromPrefs(context: Context): List<String> {
    val prefs = context.getSharedPreferences("searchHistory", 0)
    val json = prefs.getString("search_history", null) ?: return emptyList()
    return try {
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

fun saveHistoryToPrefs(list: List<String>, context: Context) {
    val prefs = context.getSharedPreferences("searchHistory", 0)
    prefs.edit { putString("search_history", gson.toJson(list)) }
}

fun addSearchHistory(context: Context, query: String) {
    if (query.isBlank()) return
    val currentList = getHistoryFromPrefs(context).toMutableList()
    currentList.remove(query)
    currentList.add(0, query)
    val newList = if (currentList.size > 10) currentList.subList(0, 10) else currentList
    saveHistoryToPrefs(newList, context)
}

fun clearSearchHistory(context: Context) {
    saveHistoryToPrefs(emptyList(), context)
}
