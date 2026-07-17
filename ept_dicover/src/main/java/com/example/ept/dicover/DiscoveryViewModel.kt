package com.example.ept.dicover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 发现页 ViewModel，加载分类列表和推荐主题
 * email : 3014386984@qq.com
 * date : 2026/7/16 11:39
 */
class DiscoveryViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()
    private val gson = Gson()

    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> = _categories

    private val _topics = MutableLiveData<List<TopicItem>>()
    val topics: LiveData<List<TopicItem>> = _topics

    private val _authors = MutableLiveData<List<TopicItem>>()
    val authors: LiveData<List<TopicItem>> = _authors

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 标记是否已加载过数据，防止返回时重复加载 */
    var loaded = false
        private set

    /**
     * 下拉刷新：重新加载分类和推荐主题
     */
    fun refresh() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 分类
                val catResponse = withContext(Dispatchers.IO) {
                    api.getTabList().execute()
                }
                val list = catResponse.body()?.tabInfo?.tabList
                    ?.filter { it.id > 0 }
                    ?.map { CategoryItem(it.name, it.apiUrl, CategoryAdapter.getIconForCategory(it.name)) }
                    ?: emptyList()
                Log.d("DiscoveryViewModel", "Loaded ${list.size} categories")
                _categories.value = list

                val discResponse = withContext(Dispatchers.IO) {
                    api.getDiscovery().execute()
                }
                val discItems = discResponse.body()?.itemList ?: emptyList()
                _topics.value = parseBriefCardsAfterHeader(discItems, "推荐主题")
                _authors.value = parseBriefCardsAfterHeader(discItems, "推荐作者")
                Log.d("DiscoveryViewModel", "Loaded ${_topics.value?.size} topics, ${_authors.value?.size} authors")

                _error.value = null
            } catch (e: Exception) {
                Log.e("DiscoveryViewModel", "refresh failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 discovery 响应中解析指定 header7 标题后面的 briefCard 列表
     */
    private fun parseBriefCardsAfterHeader(items: List<com.example.core.model.Item>, headerText: String): List<TopicItem> {
        val result = mutableListOf<TopicItem>()
        var foundHeader = false

        for (item in items) {
            val data = item.data as? Map<*, *> ?: continue

            if (item.type == "textCard") {
                val dataType = data["dataType"] as? String ?: ""
                val type = data["type"] as? String ?: ""
                val text = data["text"] as? String ?: ""
                if (dataType == "TextCardWithRightAndLeftTitle" && type == "header7") {
                    if (text == headerText) {
                        foundHeader = true
                        continue
                    }
                    // 遇到下一个 header7 就停止（如果已经在收集）
                    if (foundHeader) break
                }
            }

            if (foundHeader && item.type == "briefCard") {
                val id = (data["id"] as? Double)?.toLong() ?: continue
                val title = (data["title"] as? String)?.removePrefix("#") ?: continue
                val icon = data["icon"] as? String ?: ""
                val description = data["description"] as? String ?: ""
                val actionUrl = data["actionUrl"] as? String ?: ""
                result.add(TopicItem(id, title, description, icon, actionUrl))
            }
        }
        return result
    }
}

data class TopicItem(
    val id: Long,
    val title: String,
    val description: String,
    val icon: String,
    val actionUrl: String
)
