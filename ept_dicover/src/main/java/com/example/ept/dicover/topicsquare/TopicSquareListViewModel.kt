package com.example.ept.dicover.topicsquare

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroItem
import com.example.core.model.TopicSquareListItem
import com.example.core.model.utils.safeString
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题广场列表页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/27
 */
class TopicSquareListViewModel : ViewModel() {

    private val api = RetrofitClient.create<UniversalApi>()

    private val _liveData = MutableLiveData<TopicSquareListState>()
    val liveData: LiveData<TopicSquareListState> get() = _liveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var hasMore = true

    private var lastItemId: String = ""
    private var materialJSON: String = ""
    private var dataSource: String = ""
    private var pageLabelParam: String = ""

    fun loadTopics(pageLabel: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val items = fetchTopics(pageLabel)
                _liveData.postValue(TopicSquareListState.RefreshState(items))
            } catch (e: Exception) {
                Log.e("TopicSquareListVM", "loadTopics failed", e)
                _liveData.postValue(TopicSquareListState.ErrorState(e.message.toString()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (_isLoading.value == true || !hasMore) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newItems = fetchMoreTopics()
                val current = liveData.value.let {
                    when (it) {
                        is TopicSquareListState.RefreshState -> it.topics
                        is TopicSquareListState.LoadingMoreState -> it.topics
                        else -> emptyList()
                    }
                }
                _liveData.postValue(TopicSquareListState.LoadingMoreState(current + newItems))
            } catch (e: Exception) {
                Log.e("TopicSquareListVM", "loadMore failed", e)
                _liveData.postValue(TopicSquareListState.ErrorState(e.message.toString()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh(pageLabel: String) {
        lastItemId = ""
        materialJSON = ""
        dataSource = ""
        pageLabelParam = ""
        hasMore = true
        loadTopics(pageLabel)
    }

    private suspend fun fetchTopics(pageLabel: String): List<TopicSquareListItem> {
        return withContext(Dispatchers.IO) {
            val response = api.getPage(pageLabel = pageLabel).execute()
            val body = response.body()

            val items = mutableListOf<TopicSquareListItem>()

            if (body?.code == 0 && body.result != null) {
                val result = body.result!!

                for (card in result.cardList) {
                    if (card.type != "set_metro_list") continue
                    val metroList = card.cardData?.body?.metroList ?: continue
                    for (metro in metroList) {
                        parseTopicItem(metro)?.let { items.add(it) }
                    }
                }

                val params = result.cardList.firstOrNull { it.type == "call_metro_list" }
                    ?.cardData?.body?.apiRequest?.params
                if (params != null) {
                    materialJSON = params.safeString("material")
                    lastItemId = params.safeString("last_item_id")
                    dataSource = params.safeString("data_source")
                    pageLabelParam = params.safeString("page_label")
                }
            }

            items
        }
    }

    private suspend fun fetchMoreTopics(): List<TopicSquareListItem> {
        return withContext(Dispatchers.IO) {
            val response = api.getMorePage(
                dataSource = dataSource,
                pageLabel = pageLabelParam,
                materialJSON = materialJSON,
                lastItemId = lastItemId
            ).execute()

            val body = response.body()
            val items = mutableListOf<TopicSquareListItem>()

            if (body?.code == 0 && body.result != null) {
                val result = body.result!!
                lastItemId = result.lastItemId ?: ""

                if (lastItemId.isEmpty() || result.itemList.isEmpty()) {
                    hasMore = false
                }

                for (metro in result.itemList) {
                    parseTopicItem(metro)?.let { items.add(it) }
                }
            } else {
                hasMore = false
            }

            items
        }
    }

    private fun parseTopicItem(metro: MetroItem): TopicSquareListItem? {
        if (metro.type != "topic") return null
        val data = metro.metroData ?: return null
        val topicId = data.topicId?.toLongOrNull() ?: return null

        return TopicSquareListItem(
            id = topicId,
            title = data.title ?: "",
            description = data.description ?: "",
            coverUrl = data.cover?.url ?: "",
            participantCount = data.tags?.firstOrNull()?.title ?: "",
            pageLabel = extractPageLabel(metro.link ?: "")
        )
    }

    private fun extractPageLabel(link: String): String {
        return try {
            val decoded = java.net.URLDecoder.decode(link, "UTF-8")
            val regex = Regex("page_label[\"\\s]*:[\"\\s]*([^\"]+)\"")
            val match = regex.find(decoded)
            match?.groupValues?.get(1) ?: ""
        } catch (e: Exception) {
            Log.e("TopicSquareListVM", "extractPageLabel failed: $link", e)
            ""
        }
    }

    companion object {

    }
}

sealed class TopicSquareListState {
    data class RefreshState(val topics: List<TopicSquareListItem>) : TopicSquareListState()
    data class LoadingMoreState(val topics: List<TopicSquareListItem>) : TopicSquareListState()
    data class ErrorState(val errorMsg: String) : TopicSquareListState()
}
