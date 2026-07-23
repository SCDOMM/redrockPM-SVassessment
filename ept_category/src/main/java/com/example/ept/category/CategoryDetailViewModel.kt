package com.example.ept.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.GetPageCard
import com.example.core.model.GetPageResponse
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 分类详情页 ViewModel — 只加载 header 信息
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
class CategoryDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    data class TagInfo(
        val description: String,
        val headerImage: String,
        val stats: String,
        val feedPageLabels: List<Pair<String, String>> = emptyList() // (title, page_label)
    )

    var loaded = false
        private set

    private val _tagInfo = MutableLiveData<TagInfo?>()
    val tagInfo: LiveData<TagInfo?> = _tagInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * 加载分类详情 header 信息
     */
    fun loadCategoryDetail(pageLabel: String, categoryName: String) {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPage(pageLabel = pageLabel).execute()
                }
                val body = response.body()
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
                findTagInfo(cardList)

                _error.value = null
                Log.d("CategoryDetail", "Loaded header info")
            } catch (e: Exception) {
                Log.e("CategoryDetail", "loadCategoryDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 card_list 中提取头图、描述和统计信息
     */
    private fun findTagInfo(cardList: List<GetPageCard>) {
        var desc = ""
        var headerImage = ""
        var stats = ""
        val feedTabs = mutableListOf<Pair<String, String>>() // (title, page_label)

        for (card in cardList) {
            val metroList = card.card_data?.body?.metro_list ?: continue
            for (metro in metroList) {
                val data = metro.metro_data ?: continue

                // 从 topic 类型的 metro 中提取头图
                if (headerImage.isEmpty()) {
                    val bg = data.background
                    if (bg != null && bg.url.isNotEmpty()) {
                        headerImage = bg.url
                    }
                }

                // 提取描述
                if (desc.isEmpty()) {
                    if (data.description.isNotEmpty()) {
                        desc = data.description
                    } else {
                        val sub = data.subtitle
                        if (!sub.isNullOrEmpty()) {
                            desc = sub
                        }
                    }
                }

                // 提取统计信息
                if (stats.isEmpty()) {
                    val tags = data.tags
                    if (!tags.isNullOrEmpty()) {
                        stats = tags.joinToString(" · ") { it.title }
                    }
                }

                // 提取 nav tabs
                if (metro.type == "nav") {
                    val navList = data.nav_list
                    if (!navList.isNullOrEmpty()) {
                        for (nav in navList) {
                            if (nav.page_label.isNotEmpty()) {
                                feedTabs.add(nav.title to nav.page_label)
                            }
                        }
                    }
                }

                if (headerImage.isNotEmpty() && desc.isNotEmpty() && stats.isNotEmpty() && feedTabs.isNotEmpty()) break
            }
            if (headerImage.isNotEmpty() && desc.isNotEmpty() && stats.isNotEmpty() && feedTabs.isNotEmpty()) break
        }

        Log.d("CategoryDetail", "Found ${feedTabs.size} feed tabs: $feedTabs")
        _tagInfo.value = TagInfo(desc, headerImage, stats, feedTabs)
    }
}
