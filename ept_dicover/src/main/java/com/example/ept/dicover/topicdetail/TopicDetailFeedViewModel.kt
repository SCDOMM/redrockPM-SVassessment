package com.example.ept.dicover.topicdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CallMetroListResponse
import com.example.core.model.GetPageResponse
import com.example.core.model.TopicFeedItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题详情页 Feed ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/27
 */
class TopicDetailFeedViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()
    private val gson = RetrofitClient.gson

    private val _liveData = MutableLiveData<TopicFeedState>()
    val liveData: LiveData<TopicFeedState> get() = _liveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var hasMore = true

    private var lastItemId: String = ""
    private var materialJSON: String = ""
    private var dataSource: String = ""
    private var pageLabelParam: String = ""
    private var cardJSON: String = ""
    private var pageParams: String = ""
    private var cardIndex: String = ""
    private var materialIndex: String = ""

    fun loadFeed(pageLabel: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val items = fetchFeed(pageLabel)
                _liveData.postValue(TopicFeedState.RefreshState(items))
            } catch (e: Exception) {
                Log.e("TopicDetailFeedVM", "loadFeed failed", e)
                _liveData.postValue(TopicFeedState.ErrorState(e.message.toString()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore(pageLabel: String) {
        if (_isLoading.value == true || !hasMore) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newItems = fetchMoreFeed()
                val current = liveData.value.let {
                    when (it) {
                        is TopicFeedState.RefreshState -> it.items
                        is TopicFeedState.LoadingMoreState -> it.items
                        else -> emptyList()
                    }
                }
                _liveData.postValue(TopicFeedState.LoadingMoreState(current + newItems))
            } catch (e: Exception) {
                Log.e("TopicDetailFeedVM", "loadMore failed", e)
                _liveData.postValue(TopicFeedState.ErrorState(e.message.toString()))
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
        cardJSON = ""
        pageParams = ""
        cardIndex = ""
        materialIndex = ""
        hasMore = true
        loadFeed(pageLabel)
    }

    private suspend fun fetchFeed(pageLabel: String): List<TopicFeedItem> {
        return withContext(Dispatchers.IO) {
            val response = api.getPageRaw(pageLabel = pageLabel).execute()
            val rawBody = response.body()?.string() ?: ""
            val body = if (response.isSuccessful && rawBody.isNotEmpty()) {
                gson.fromJson(rawBody, GetPageResponse::class.java)
            } else null

            val items = mutableListOf<TopicFeedItem>()

            if (body?.code == 0 && body.result != null) {
                val result = body.result!!

                for (card in result.card_list) {
                    if (card.type == "call_card_list" || card.type == "call_metro_list") {
                        val params = card.card_data?.body?.api_request?.params
                        if (params != null) {
                            lastItemId = params["last_item_id"] ?: ""
                            materialJSON = params["material"] ?: ""
                            dataSource = params["data_source"] ?: ""
                            pageLabelParam = params["page_label"] ?: ""
                            cardJSON = params["card"] ?: ""
                            pageParams = params["page_params"] ?: ""
                            cardIndex = params["card_index"] ?: ""
                            materialIndex = params["material_index"] ?: ""
                        }
                    }

                    if (card.type != "set_metro_list") continue
                    val metroList = card.card_data?.body?.metro_list ?: continue

                    for (metro in metroList) {
                        if (metro.type != "item") continue
                        val data = metro.metro_data ?: continue

                        val itemId = data.item_id.toLongOrNull() ?: 0L
                        if (itemId == 0L) continue

                        val videoId = data.video?.video_id?.ifEmpty { data.video_id } ?: data.video_id
                        val resourceType = data.resource_type.ifEmpty { "pgc_video" }
                        val isVideo = videoId.isNotEmpty()
                        val coverUrl = if (isVideo) {
                            data.video?.cover?.url ?: ""
                        } else {
                            data.images?.firstOrNull()?.cover?.url ?: ""
                        }
                        val imageUrls = if (!isVideo) {
                            data.images?.mapNotNull { it.cover?.url } ?: emptyList()
                        } else {
                            emptyList()
                        }

                        items.add(
                            TopicFeedItem(
                                id = itemId,
                                videoId = videoId,
                                resourceType = resourceType,
                                text = data.text,
                                coverUrl = coverUrl,
                                imageUrls = imageUrls,
                                isVideo = isVideo,
                                authorName = data.author?.nick ?: "",
                                authorAvatar = data.author?.avatar?.url ?: "",
                                likeCount = data.consumption?.like_count ?: 0,
                                collectionCount = data.consumption?.collection_count ?: 0,
                                commentCount = data.consumption?.comment_count ?: 0,
                                publishTime = data.publish_time
                            )
                        )
                    }
                }
            }

            items
        }
    }

    private suspend fun fetchMoreFeed(): List<TopicFeedItem> {
        return withContext(Dispatchers.IO) {
            val response = api.loadMoreTopics(
                cardIndex = cardIndex,
                material = materialJSON,
                materialIndex = materialIndex,
                lastItemId = lastItemId,
                pageParams = pageParams,
                pageLabel = pageLabelParam,
                card = cardJSON,
                dataSource = dataSource
            ).execute()

            val rawBody = response.body()?.string() ?: ""
            val body = if (response.isSuccessful && rawBody.isNotEmpty()) {
                gson.fromJson(rawBody, CallMetroListResponse::class.java)
            } else null

            val items = mutableListOf<TopicFeedItem>()

            if (body?.code == 0 && body.result != null) {
                val result = body.result!!
                lastItemId = result.last_item_id

                if (lastItemId.isEmpty() || result.item_list.isEmpty()) {
                    hasMore = false
                }

                for (metro in result.item_list) {
                    if (metro.type != "item") continue
                    val data = metro.metro_data ?: continue

                    val itemId = data.item_id.toLongOrNull() ?: 0L
                    if (itemId == 0L) continue

                    val videoId = data.video?.video_id?.ifEmpty { data.video_id } ?: data.video_id
                    val resourceType = data.resource_type.ifEmpty { "pgc_video" }
                    val isVideo = videoId.isNotEmpty()
                    val coverUrl = if (isVideo) {
                        data.video?.cover?.url ?: ""
                    } else {
                        data.images?.firstOrNull()?.cover?.url ?: ""
                    }
                    val imageUrls = if (!isVideo) {
                        data.images?.mapNotNull { it.cover?.url } ?: emptyList()
                    } else {
                        emptyList()
                    }

                    items.add(
                        TopicFeedItem(
                            id = itemId,
                            videoId = videoId,
                            resourceType = resourceType,
                            text = data.text,
                            coverUrl = coverUrl,
                            imageUrls = imageUrls,
                            isVideo = isVideo,
                            authorName = data.author?.nick ?: "",
                            authorAvatar = data.author?.avatar?.url ?: "",
                            likeCount = data.consumption?.like_count ?: 0,
                            collectionCount = data.consumption?.collection_count ?: 0,
                            commentCount = data.consumption?.comment_count ?: 0,
                            publishTime = data.publish_time
                        )
                    )
                }
            } else {
                hasMore = false
            }

            items
        }
    }
}

sealed class TopicFeedState {
    data class RefreshState(val items: List<TopicFeedItem>) : TopicFeedState()
    data class LoadingMoreState(val items: List<TopicFeedItem>) : TopicFeedState()
    data class ErrorState(val errorMsg: String) : TopicFeedState()
}
