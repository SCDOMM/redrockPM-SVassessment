package com.example.core.common

import android.content.Context
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken
import com.therouter.router.gson

/**   
 * 包名称： com.example.core.common
 * 类名称：sharedPreferenceUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-23 11:55
 *
 */
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