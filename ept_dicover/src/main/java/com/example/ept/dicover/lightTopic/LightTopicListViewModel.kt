package com.example.ept.dicover.lightTopic

import com.example.core.model.LightTopicItem
import com.example.core.model.LightTopicPlaylistVideo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CallCardListResponse
import com.example.core.model.GetPageCard
import com.example.core.model.GetPageMetroItem
import com.example.core.model.GetPageResponse
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import com.example.core.network.api.SpecficApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 主题播单列表页 ViewModel，支持下滑加载更多
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicListViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()
    private val universalApi = RetrofitClient.create<UniversalApi>()

    var loaded = false
        private set

    private var _liveData = MutableLiveData<LightTopicListState>()
    val liveData: LiveData<LightTopicListState> get() = _liveData

    private var lastItemId: String = ""
    private var cardListJson: String = ""
    private val pageLabel = "discover_special_topic"
    private var isLoadingMore = false
    private var hasMore = true

    private val allItems = mutableListOf<LightTopicItem>()

    fun loadTopics() {
        loaded = true
        allItems.clear()
        lastItemId = "0"
        cardListJson = ""
        hasMore = true

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPageRaw(pageLabel = pageLabel).execute()
                }
                Log.d("LightTopicListVM", "HTTP status=${response.code()}, isSuccessful=${response.isSuccessful()}")
                val rawBody = response.body()?.string() ?: ""
                Log.d("LightTopicListVM", "RAW_LEN=${rawBody.length}")

                if (!response.isSuccessful()) {
                    _liveData.postValue(LightTopicListState.ErrorState("HTTP错误: ${response.code()}"))
                    return@launch
                }
                if (rawBody.isEmpty()) {
                    _liveData.postValue(LightTopicListState.ErrorState("响应体为空"))
                    return@launch
                }

                val body = RetrofitClient.gson.fromJson(rawBody, GetPageResponse::class.java)
                Log.d("LightTopicListVM", "Parsed code=${body?.code}, result=${body?.result != null}")

                if (body?.code != 0) {
                    _liveData.postValue(LightTopicListState.ErrorState("加载失败: code=${body?.code}"))
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    _liveData.postValue(LightTopicListState.ErrorState("数据为空"))
                    return@launch
                }
                Log.d("LightTopicListVM", "card_list.size=${result.card_list.size}")

                val topicItems = parseTopics(result.card_list)
                Log.d("LightTopicListVM", "parsed ${topicItems.size} topics")

                allItems.clear()
                allItems.addAll(topicItems)

                extractPaginationParams(result.card_list)
                _liveData.postValue(LightTopicListState.RefreshState(allItems.toList()))
            } catch (e: Exception) {
                Log.e("LightTopicListVM", "loadTopics failed", e)
                _liveData.postValue(LightTopicListState.ErrorState(e.message.toString()))
            }
        }
    }

    private fun extractPaginationParams(cards: List<GetPageCard>) {
        val callCard = cards.firstOrNull { it.type == "call_card_list" }
        if (callCard != null) {
            val params = callCard.card_data?.body?.api_request?.params
            if (params != null) {
                lastItemId = (params["last_item_id"] ?: "0").toString()
                val rawCardList = params["card_list"]
                cardListJson = when (rawCardList) {
                    is String -> rawCardList
                    else -> try { RetrofitClient.gson.toJson(rawCardList) } catch (e: Exception) { "" }
                }
                Log.d("LightTopicListVM", "Found call_card_list: lastItemId=$lastItemId, cardListJson length=${cardListJson.length}")
            }
        } else {
            lastItemId = "0"
            cardListJson = "[]"
            Log.d("LightTopicListVM", "No call_card_list card found")
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore) return
        if (cardListJson.isEmpty() && lastItemId == "0") return
        isLoadingMore = true
        Log.d("LightTopicListVM", "loadMore: lastItemId=$lastItemId, cardListJson length=${cardListJson.length}")

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    universalApi.callCardList(
                        lastItemId = lastItemId,
                        cardList = cardListJson,
                        pageLabel = pageLabel
                    ).execute()
                }
                val rawBody = response.body()?.string() ?: ""
                Log.d("LightTopicListVM", "loadMore HTTP status=${response.code()}, body length=${rawBody.length}")

                if (!response.isSuccessful() || rawBody.isEmpty()) {
                    hasMore = false
                    return@launch
                }

                val body = RetrofitClient.gson.fromJson(rawBody, CallCardListResponse::class.java)
                if (body?.code != 0 || body?.result == null) {
                    hasMore = false
                    return@launch
                }

                val result = body.result!!
                val newCards = result.itemList
                Log.d("LightTopicListVM", "loadMore returned ${newCards.size} cards, lastItemId=${result.lastItemId}")

                //复用parseTopics
                val newTopics = parseTopics(newCards)

                if (newTopics.isNotEmpty()) {
                    allItems.addAll(newTopics)
                }

                // 更新分页状态
                val newLastItemId = result.lastItemId
                if (newLastItemId.isNotEmpty() && newLastItemId != lastItemId) {
                    lastItemId = newLastItemId
                    val callCard = newCards.firstOrNull { it.type == "call_card_list" }
                    if (callCard != null) {
                        val params = callCard.card_data?.body?.api_request?.params
                        if (params != null) {
                            val rawCardList = params["card_list"]
                            cardListJson = when (rawCardList) {
                                is String -> rawCardList
                                else -> try { RetrofitClient.gson.toJson(rawCardList) } catch (e: Exception) { cardListJson }
                            }
                        }
                    }
                } else {
                    hasMore = false
                }

                if (newTopics.isEmpty() || newCards.isEmpty()) {
                    hasMore = false
                }
                _liveData.postValue(LightTopicListState.LoadingMoreState(allItems.toList()))
                Log.d("LightTopicListVM", "loadMore done: totalItems=${allItems.size}, hasMore=$hasMore, lastItemId=$lastItemId")
            } catch (e: Exception) {
                Log.e("LightTopicListVM", "loadMore failed", e)
            } finally {
                isLoadingMore = false
            }
        }
    }

    private fun parseTopics(cards: List<GetPageCard>): List<LightTopicItem> {
        return cards.mapNotNull { parseTopicFromCard(it) }
    }

    private fun parseTopicFromCard(card: GetPageCard): LightTopicItem? {
        if (card.type != "set_metro_list") return null

        val cardData = card.card_data ?: return null
        val header = cardData.header ?: return null
        val body = cardData.body ?: return null

        val titleText = header.left?.firstOrNull()?.metro_data?.text ?: return null

        val description = body.metro_list?.firstOrNull { it.type == "text" }
            ?.metro_data?.text ?: ""

        val detailLink = header.right?.firstOrNull()?.metro_data?.link ?: ""
        val topicId = Regex("lightTopic/detail/(\\d+)").find(detailLink)
            ?.groupValues?.get(1)?.toIntOrNull() ?: 0

        val videos = body.metro_list
            ?.filter { it.type == "video" }
            ?.take(2)
            ?.mapNotNull { parseVideoFromMetro(it) }
            ?: emptyList()

        return if (topicId > 0) {
            LightTopicItem(
                topicId = topicId,
                title = titleText,
                description = description,
                videos = videos
            )
        } else null
    }

    private fun parseVideoFromMetro(metro: GetPageMetroItem): LightTopicPlaylistVideo? {
        val metroData = metro.metro_data ?: return null
        val videoId = metroData.video_id?.toLongOrNull() ?: return null
        if (videoId == 0L) return null

        return LightTopicPlaylistVideo(
            id = videoId,
            title = metroData.title ?: "",
            coverUrl = metroData.cover?.url ?: "",
            duration = metroData.duration?.value?.toLong() ?: 0L,
            authorName = metroData.author?.nick ?: "",
            authorIcon = metroData.author?.avatar?.url ?: "",
            description = "",
            playUrl = metroData.play_url ?: ""
        )
    }
}

sealed class LightTopicListState {
    data class RefreshState(val items: List<LightTopicItem>) : LightTopicListState()
    data class LoadingMoreState(val items: List<LightTopicItem>) : LightTopicListState()
    data class ErrorState(val errorMsg: String) : LightTopicListState()
}