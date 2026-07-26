package com.example.ept.dicover.discovery

import com.example.core.model.TopicItem
import com.example.core.model.DiscoverCategoryItem
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.ApiRequest
import com.example.core.model.GetPageResponse
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 发现页 ViewModel，加载分类列表、主题播单、话题广场
 * email : 3014386984@qq.com
 * date : 2026/7/16 11:39
 */

class DiscoveryViewModel : ViewModel() {

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<SpecficApi>()

    /** 分类列表，展示视频分类入口 */
    private val _categories = MutableLiveData<List<DiscoverCategoryItem>>()
    val categories: LiveData<List<DiscoverCategoryItem>> = _categories

    /** 主题播单列表，水平滚动展示 */
    private val _topics = MutableLiveData<List<TopicItem>>()
    val topics: LiveData<List<TopicItem>> = _topics

    /** 话题广场数据 */
    private val _squareItems = MutableLiveData<List<TopicItem>>()
    val squareItems: LiveData<List<TopicItem>> = _squareItems

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
     */
    fun refresh() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 通过 get_page 接口加载发现页
                val response = withContext(Dispatchers.IO) {
                    api.getPageRaw(pageLabel = "discover_v2").execute()
                }
                if (!response.isSuccessful()) {
                    _error.value = "HTTP错误: ${response.code()}"
                    return@launch
                }
                val rawBody = response.body()?.string() ?: ""
                val body = RetrofitClient.gson.fromJson(rawBody, GetPageResponse::class.java)

                if (body?.code != 0) {
                    _error.value = "接口返回错误: code=${body?.code}"
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    _error.value = "接口返回数据为空"
                    return@launch
                }

                val cardList = result.card_list

                val categories = mutableListOf<DiscoverCategoryItem>()
                val topics = mutableListOf<TopicItem>()
                val squareItems = mutableListOf<TopicItem>()

                for (card in cardList) {
                    val metroList = card.card_data?.body?.metro_list ?: continue
                    val headerLeft = card.card_data?.header?.left
                    val headerTitle = headerLeft?.firstOrNull()?.metro_data?.text ?: card.card_data?.header?.title ?: ""

                    for (metro in metroList) {
                        val metroData = metro.metro_data ?: continue
                        // 分类图标
                        val icons = metroData.icons
                        if (!icons.isNullOrEmpty()) {
                            for (iconItem in icons) {
                                val link = iconItem.link
                                val apiRequest = parseApiRequest(link)
                                if (apiRequest == null) continue
                                val pageLabel = apiRequest.params?.get("page_label") as? String ?: continue
                                val name = iconItem.name
                                if (name.isNotEmpty()) {
                                    categories.add(
                                        DiscoverCategoryItem(name = name, pageLabel = pageLabel, iconUrl = iconItem.icon)
                                    )
                                }
                            }
                        }
                        // 主题播单：header title 为"主题播单"，数据在 metro_data 的 image_id 字段
                        if (headerTitle == "主题播单" && metro.type == "image" && metroData.image_id > 0) {
                            topics.add(TopicItem(id = metroData.image_id, title = metroData.title ?: "", description = "", icon = metroData.cover?.url ?: "", actionUrl = metroData.link))
                        }
                        // 话题广场：header title 为"话题广场"，数据在 metro_data.item_list
                        if (headerTitle == "话题广场" && !metroData.item_list.isNullOrEmpty()) {
                            for (item in metroData.item_list) {
                                val id = item.image_id.toLongOrNull() ?: 0L
                                if (id > 0) {
                                    squareItems.add(TopicItem(id = id, title = item.title, description = "", icon = item.cover?.url ?: "", actionUrl = item.link))
                                }
                            }
                        }
                    }
                }

                _categories.value = categories
                _topics.value = topics
                _squareItems.value = squareItems
                _error.value = null
            } catch (e: Exception) {
                Log.e("DiscoveryViewModel", "refresh failed: ${e.javaClass.simpleName}: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 deep link 中解析 api_request JSON
     * 格式: eyepetizer://cardlist/common?title=xxx&api_request={"url":"...","params":{...}}
     */
    private fun parseApiRequest(deepLink: String): ApiRequest? {
        return try {
            val uri = Uri.parse(deepLink)
            val raw = uri.getQueryParameter("api_request") ?: return null
            RetrofitClient.gson.fromJson(raw, ApiRequest::class.java)
        } catch (e: Exception) {
            Log.e("DiscoveryViewModel", "parseApiRequest failed: $deepLink", e)
            null
        }
    }
}