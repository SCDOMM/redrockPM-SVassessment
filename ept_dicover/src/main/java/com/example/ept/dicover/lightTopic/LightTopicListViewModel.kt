package com.example.ept.dicover.lightTopic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.GetPageCard
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 主题播单列表页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicListViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    var loaded = false
        private set

    /** 主题列表数据 */
    private val _items = MutableLiveData<List<LightTopicItem>>()
    val items: LiveData<List<LightTopicItem>> = _items

    /** 加载状态 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 加载主题播单列表数据 */
    fun loadTopics() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPage(pageLabel = "discover_special_topic").execute()
                }
                Log.d("LightTopicListVM", "HTTP status=${response.code()}, isSuccessful=${response.isSuccessful()}")
                val body = response.body()
                Log.d("LightTopicListVM", "body=${body != null}, code=${body?.code}")
                if (body?.code != 0) {
                    _error.value = "加载失败: code=${body?.code}"
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    _error.value = "数据为空"
                    return@launch
                }
                Log.d("LightTopicListVM", "card_list.size=${result.card_list.size}")

                val topicItems = parseTopics(result.card_list)
                Log.d("LightTopicListVM", "parsed ${topicItems.size} topics")
                _items.value = topicItems
                _error.value = null
            } catch (e: Exception) {
                Log.e("LightTopicListVM", "loadTopics failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** 解析 API 返回的卡片列表，提取主题信息 */
    private fun parseTopics(cards: List<GetPageCard>): List<LightTopicItem> {
        val result = mutableListOf<LightTopicItem>()

        for ((index, card) in cards.withIndex()) {
            Log.d("LightTopicListVM", "card[$index] type=${card.type}")
            if (card.type != "set_metro_list") continue

            val cardData = card.card_data
            if (cardData == null) {
                Log.w("LightTopicListVM", "card[$index] cardData is null")
                continue
            }
            val header = cardData.header
            if (header == null) {
                Log.w("LightTopicListVM", "card[$index] header is null")
                continue
            }
            val body = cardData.body
            if (body == null) {
                Log.w("LightTopicListVM", "card[$index] body is null")
                continue
            }

            // 获取标题
            val titleText = header.left?.firstOrNull()?.metro_data?.text
            if (titleText == null) {
                Log.w("LightTopicListVM", "card[$index] title is null, left=${header.left?.size}")
                continue
            }

            // 获取描述
            val description = body.metro_list?.firstOrNull { it.type == "text" }
                ?.metro_data?.text ?: ""

            // 获取详情链接
            val detailLink = header.right?.firstOrNull()?.metro_data?.link ?: ""

            // 从链接中提取 topicId
            val topicId = Regex("lightTopic/detail/(\\d+)").find(detailLink)
                ?.groupValues?.get(1)?.toIntOrNull() ?: 0

            // 获取预览视频（最多2个）
            val videos = mutableListOf<TopicPlaylistVideo>()
            val videoItems = body.metro_list?.filter { it.type == "video" } ?: emptyList()
            for (metro in videoItems.take(2)) {
                val metroData = metro.metro_data ?: continue
                val videoId = metroData.video_id?.toLongOrNull() ?: continue
                if (videoId == 0L) continue

                videos.add(
                    TopicPlaylistVideo(
                        id = videoId,
                        title = metroData.title ?: "",
                        coverUrl = metroData.cover?.url ?: "",
                        duration = metroData.duration?.value ?: 0L,
                        authorName = metroData.author?.nick ?: "",
                        authorIcon = metroData.author?.avatar?.url ?: "",
                        description = "",
                        playUrl = metroData.play_url ?: ""
                    )
                )
            }

            if (topicId > 0) {
                result.add(
                    LightTopicItem(
                        topicId = topicId,
                        title = titleText,
                        description = description,
                        videos = videos
                    )
                )
            }
        }

        return result
    }
}
