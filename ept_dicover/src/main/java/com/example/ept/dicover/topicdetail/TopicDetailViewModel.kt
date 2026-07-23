package com.example.ept.dicover.topicdetail

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
 * description ： 话题详情页 ViewModel，使用 get_page 接口加载
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
class TopicDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    var loaded = false
        private set

    private val _items = MutableLiveData<List<TopicPlaylistVideo>>()
    val items: LiveData<List<TopicPlaylistVideo>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var nextPageLabel: String? = null
    var hasNextPage = true
        private set

    private val allItems = mutableListOf<TopicPlaylistVideo>()

    /**
     * 加载话题详情
     * @param pageLabel 页面标识 (如 "lightTopic/detail-805")
     * @param tagName 话题名称
     */
    fun loadDetail(pageLabel: String, tagName: String) {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
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

                val cardList = result.card_list
                allItems.clear()

                for (card in cardList) {
                    allItems.addAll(parseVideoCards(card))
                }

                nextPageLabel = findNextPageLabel(cardList)
                hasNextPage = nextPageLabel != null

                _items.value = allItems.toList()
                _error.value = null
                Log.d("TopicDetailVM", "Loaded ${allItems.size} items for $tagName")
            } catch (e: Exception) {
                Log.e("TopicDetailVM", "loadDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载下一页
     */
    fun loadNextPage() {
        val label = nextPageLabel ?: return
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPage(pageLabel = label).execute()
                }
                val body = response.body()
                if (body?.code != 0) {
                    hasNextPage = false
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    hasNextPage = false
                    return@launch
                }

                val cardList = result.card_list
                val newItems = mutableListOf<TopicPlaylistVideo>()
                for (card in cardList) {
                    newItems.addAll(parseVideoCards(card))
                }

                allItems.addAll(newItems)
                nextPageLabel = findNextPageLabel(cardList)
                hasNextPage = nextPageLabel != null
                _items.value = allItems.toList()
                _error.value = null
            } catch (e: Exception) {
                Log.e("TopicDetailVM", "loadNextPage failed", e)
                hasNextPage = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 card 中解析视频项
     */
    private fun parseVideoCards(card: GetPageCard): List<TopicPlaylistVideo> {
        if (card.type != "set_metro_list") return emptyList()

        val metroList = card.card_data?.body?.metro_list ?: return emptyList()
        val result = mutableListOf<TopicPlaylistVideo>()

        for (metro in metroList) {
            val data = metro.metro_data ?: continue
            if (metro.type == "nav") continue

            val video = data.video ?: continue
            val title = video.title
            if (title.isEmpty()) continue

            result.add(
                TopicPlaylistVideo(
                    id = video.video_id.toLongOrNull() ?: 0L,
                    title = title,
                    coverUrl = video.cover?.url ?: "",
                    duration = video.duration?.value ?: 0L,
                    authorName = data.author?.nick ?: "",
                    authorIcon = data.author?.avatar?.url ?: "",
                    description = data.text ?: "",
                    playUrl = video.play_url.replace("\\u003d", "=").replace("\\u0026", "&")
                )
            )
        }
        return result
    }

    /**
     * 从 card_list 中查找下一页的 page_label
     */
    private fun findNextPageLabel(cardList: List<GetPageCard>): String? {
        for (card in cardList) {
            val metroList = card.card_data?.body?.metro_list ?: continue
            for (metro in metroList) {
                if (metro.type == "nav") {
                    val navList = metro.metro_data?.nav_list
                    if (!navList.isNullOrEmpty()) {
                        return navList.firstOrNull()?.page_label
                    }
                }
            }
        }
        return null
    }
}

/**
 * 主题播单视频项
 */
data class TopicPlaylistVideo(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String,
    val description: String,
    val playUrl: String
)
