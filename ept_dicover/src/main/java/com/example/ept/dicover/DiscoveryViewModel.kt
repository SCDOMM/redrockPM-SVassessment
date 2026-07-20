package com.example.ept.dicover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.TextCardData
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

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<KaiyanApi>()
    /** Gson 实例，用于 JSON 解析 */
    private val gson = Gson()

    /** 分类列表，展示视频分类入口 */
    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> = _categories

    /** 推荐话题列表，水平滚动展示 */
    private val _topics = MutableLiveData<List<TopicItem>>()
    val topics: LiveData<List<TopicItem>> = _topics

    /** 推荐作者列表，水平滚动展示 */
    private val _authors = MutableLiveData<List<TopicItem>>()
    val authors: LiveData<List<TopicItem>> = _authors

    /** 加载状态，控制下拉刷新动画显示 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息，显示网络请求失败时的错误提示 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 标记是否已加载过数据，防止返回时重复加载 */
    var loaded = false
        private set

    /**
     * 下拉刷新：重新加载分类和推荐主题
     * 并行加载分类列表和发现页数据
     */
    fun refresh() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 加载分类列表
                val catResponse = withContext(Dispatchers.IO) {
                    api.getTabList().execute()
                }
                // 过滤掉 id<=0 的分类，并映射为 CategoryItem
                val list = catResponse.body()?.tabInfo?.tabList
                    ?.filter { it.id > 0 }
                    ?.map { CategoryItem(it.name, it.apiUrl, CategoryAdapter.getIconForCategory(it.name)) }
                    ?: emptyList()
                Log.d("DiscoveryViewModel", "Loaded ${list.size} categories")
                _categories.value = list

                // 加载发现页数据（推荐主题、推荐作者等）
                val discResponse = withContext(Dispatchers.IO) {
                    api.getDiscovery().execute()
                }
                val discItems = discResponse.body()?.itemList ?: emptyList()
                // 从发现页数据中解析推荐主题和推荐作者
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
     * @param items 原始数据列表
     * @param headerText 要查找的标题文本（如"推荐主题"、"推荐作者"）
     * @return 解析后的 TopicItem 列表
     */
    private fun parseBriefCardsAfterHeader(items: List<com.example.core.model.Item>, headerText: String): List<TopicItem> {
        val result = mutableListOf<TopicItem>()
        var foundHeader = false

        for (item in items) {
            // 查找指定的 header7 标题卡片
            if (item.type == "textCard") {
                val card = item.data as? TextCardData ?: continue
                if (card.dataType == "TextCardWithRightAndLeftTitle" && card.type == "header7") {
                    if (card.text == headerText) {
                        foundHeader = true
                        continue
                    }
                    // 找到目标 header 后，遇到下一个 header 则停止
                    if (foundHeader) break
                }
            }

            // 解析 header 后面的 briefCard 数据
            if (foundHeader && item.type == "briefCard") {
                val data = item.data as? Map<*, *> ?: continue
                val id = (data["id"] as? Double)?.toLong() ?: continue
                // 移除标题前的 # 符号
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

/**
 * 话题/作者数据项
 * @param id 话题/作者 ID
 * @param title 标题/名称
 * @param description 描述
 * @param icon 图标 URL
 * @param actionUrl 点击跳转的 URL
 */
data class TopicItem(
    val id: Long,
    val title: String,
    val description: String,
    val icon: String,
    val actionUrl: String
)
