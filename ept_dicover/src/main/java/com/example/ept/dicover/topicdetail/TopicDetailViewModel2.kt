package com.example.ept.dicover.topicdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题详情页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailViewModel2 : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    var loaded = false
        private set

    private val _tagInfo = MutableLiveData<TopicTagInfo>()
    val tagInfo: LiveData<TopicTagInfo> = _tagInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDetail(pageLabel: String) {
        loaded = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPage(pageLabel = pageLabel).execute()
                }
                val body = response.body()
                if (body?.code != 0) {
                    _error.value = "加载失败: code=${body?.code}"
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    _error.value = "数据为空"
                    return@launch
                }

                val info = parseTopicInfo(result.card_list)
                _tagInfo.value = info
                _error.value = null
                Log.d("TopicDetailVM2", "Loaded topic: ${info.title}")
            } catch (e: Exception) {
                Log.e("TopicDetailVM2", "loadDetail failed", e)
                _error.value = e.message
            }
        }
    }

    private fun parseTopicInfo(cards: List<com.example.core.model.GetPageCard>): TopicTagInfo {
        var title = ""
        var headerImage = ""
        var description = ""
        var stats = ""
        val feedTabs = mutableListOf<Pair<String, String>>()

        for (card in cards) {
            val metroList = card.card_data?.body?.metro_list ?: continue

            for (metro in metroList) {
                val data = metro.metro_data ?: continue

                // 话题标题和封面
                if (metro.type == "topic" && title.isEmpty()) {
                    title = data.title ?: ""
                    headerImage = data.background?.url ?: ""
                    stats = data.tags?.joinToString(" . ") { it.title ?: "" } ?: ""
                }

                // 描述（富文本）
                if (metro.type == "item" && description.isEmpty()) {
                    val content = data.content
                    if (content != null) {
                        val blocks = content.blocks
                        if (blocks != null) {
                            description = blocks.joinToString("\n") { it.text ?: "" }
                        }
                    }
                }

                // 导航标签（热门推荐、最新发布）
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

data class TopicTagInfo(
    val title: String,
    val headerImage: String,
    val description: String,
    val stats: String,
    val feedPageLabels: List<Pair<String, String>>
)
