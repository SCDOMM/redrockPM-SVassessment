package com.example.ept.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.GetPageCard
import com.example.core.model.GetPageResponse
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 分类详情页 ViewModel — 加载 header 信息和 nav tabs
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
class CategoryDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()
    private val gson = Gson()

    var loaded = false
        private set

    private var _liveData = MutableLiveData<CategoryDetailState>()
    val liveData: LiveData<CategoryDetailState> get() = _liveData

    /**
     * 加载分类详情
     */
    fun loadCategoryDetail(pageLabel: String) {
        loaded = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPageRaw(pageLabel = pageLabel).execute()
                }
                val rawBody = response.body()?.string() ?: ""
                if (!response.isSuccessful()) {
                    _liveData.postValue(CategoryDetailState.ErrorState("HTTP错误: ${response.code()}"))
                    return@launch
                }
                if (rawBody.isEmpty()) {
                    _liveData.postValue(CategoryDetailState.ErrorState("响应体为空"))
                    return@launch
                }

                val body = gson.fromJson(rawBody, GetPageResponse::class.java)
                if (body?.code != 0) {
                    _liveData.postValue(CategoryDetailState.ErrorState("接口返回错误: code=${body?.code}"))
                    return@launch
                }

                val result = body.result
                if (result == null) {
                    _liveData.postValue(CategoryDetailState.ErrorState("接口返回数据为空"))
                    return@launch
                }

                val cardList = result.card_list
                findTagInfo(cardList)
            } catch (e: Exception) {
                Log.e("CategoryDetail", "loadCategoryDetail failed", e)
                _liveData.postValue(CategoryDetailState.ErrorState(e.message.toString()))
            }
        }
    }

    /**
     * 从 card_list 中提取头图、描述、统计信息和 nav tabs
     */
    private fun findTagInfo(cardList: List<GetPageCard>) {
        var desc = ""
        var headerImage = ""
        var stats = ""
        val feedTabs = mutableListOf<Pair<String, String>>()

        for (card in cardList) {
            val metroList = card.card_data?.body?.metro_list ?: continue
            for (metro in metroList) {
                val data = metro.metro_data ?: continue

                // 头图：cover → background
                if (headerImage.isEmpty()) {
                    headerImage = data.cover?.url?.takeIf { it.isNotEmpty() }
                        ?: data.background?.url?.takeIf { it.isNotEmpty() }
                        ?: ""
                }

                // 描述：description → text → subtitle
                if (desc.isEmpty()) {
                    desc = data.description.takeIf { it.isNotEmpty() }
                        ?: data.text.takeIf { it.isNotEmpty() }
                        ?: data.subtitle?.takeIf { it.isNotEmpty() }
                        ?: ""
                }

                // 统计
                if (stats.isEmpty()) {
                    stats = data.tags?.joinToString(" · ") { it.title } ?: ""
                }

                // nav tabs
                if (metro.type == "nav") {
                    data.nav_list?.forEach { feedTabs.add(Pair(it.title, it.page_label)) }
                }
            }
        }

        _liveData.postValue(CategoryDetailState.RefreshState(TagInfo(desc, headerImage, stats, feedTabs)))
    }
}

data class TagInfo(
    val description: String,
    val headerImage: String,
    val stats: String,
    val feedPageLabels: List<Pair<String, String>> = emptyList()
)

sealed class CategoryDetailState {
    data class RefreshState(val tagInfo: TagInfo) : CategoryDetailState()
    data class ErrorState(val errorMsg: String) : CategoryDetailState()
}
