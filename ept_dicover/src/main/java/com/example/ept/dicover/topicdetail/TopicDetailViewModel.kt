package com.example.ept.dicover.topicdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.GetPageCard
import com.example.core.model.GetPageResponse
import com.example.core.model.TopicTagInfo
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题详情页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()

    var loaded = false
        private set

    private var _liveData = MutableLiveData<TopicDetailState>()
    val liveData: LiveData<TopicDetailState> get() = _liveData

    fun loadDetail(pageLabel: String) {
        loaded = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPageRaw(pageLabel = pageLabel).execute()
                }
                val rawBody = response.body()?.string() ?: ""
                if (!response.isSuccessful()) {
                    _liveData.postValue(TopicDetailState.ErrorState("HTTP错误: ${response.code()}"))
                    return@launch
                }
                if (rawBody.isEmpty()) {
                    _liveData.postValue(TopicDetailState.ErrorState("响应体为空"))
                    return@launch
                }

                val body = RetrofitClient.gson.fromJson(rawBody, GetPageResponse::class.java)
                if (body?.code != 0) {
                    _liveData.postValue(TopicDetailState.ErrorState("加载失败: code=${body?.code}"))
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    _liveData.postValue(TopicDetailState.ErrorState("数据为空"))
                    return@launch
                }

                val info = parseTopicInfo(result.card_list)
                _liveData.postValue(TopicDetailState.RefreshState(info))
                Log.d("TopicDetailVM", "Loaded topic: ${info.title}")
            } catch (e: Exception) {
                Log.e("TopicDetailVM", "loadDetail failed", e)
                _liveData.postValue(TopicDetailState.ErrorState(e.message.toString()))
            }
        }
    }

    /** 解析卡片列表，提取话题信息 */
    private fun parseTopicInfo(cards: List<GetPageCard>): TopicTagInfo {
        var title = ""
        var headerImage = ""
        var description = ""
        var stats = ""
        val feedTabs = mutableListOf<Pair<String, String>>()

        for (card in cards) {
            val metroList = card.card_data?.body?.metro_list ?: continue

            for (metro in metroList) {
                val data = metro.metro_data ?: continue


                if (metro.type == "topic" && title.isEmpty()) {
                    title = data.title ?: ""
                    headerImage = data.background?.url ?: ""
                    stats = data.tags?.joinToString(" . ") { it.title ?: "" } ?: ""
                }

                if (metro.type == "item" && description.isEmpty()) {
                    val content = data.content
                    if (content != null) {
                        val blocks = content.blocks
                        if (blocks != null) {
                            description = blocks.joinToString("\n") { it.text ?: "" }
                        }
                    }
                }

                if (metro.type == "nav") {
                    val navList = data.nav_list
                    if (navList != null) {
                        for (nav in navList) {
                            feedTabs.add(Pair(nav.title, nav.page_label))
                        }
                    }
                }
            }
        }

        return TopicTagInfo(
            title = title,
            headerImage = headerImage,
            description = description,
            stats = stats,
            feedPageLabels = feedTabs
        )
    }
}

sealed class TopicDetailState {
    data class RefreshState(val tagInfo: TopicTagInfo) : TopicDetailState()
    data class ErrorState(val errorMsg: String) : TopicDetailState()
}