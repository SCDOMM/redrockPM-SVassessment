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

    private val _feedItems = MutableLiveData<List<TopicFeedItem>>()
    val feedItems: LiveData<List<TopicFeedItem>> = _feedItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _hasMore = MutableLiveData(true)
    val hasMore: LiveData<Boolean> = _hasMore

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
                _feedItems.value = items
                _error.value = null
            } catch (e: Exception) {
                Log.e("TopicDetailFeedVM", "loadFeed failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore(pageLabel: String) {
        if (_isLoading.value == true || _hasMore.value == false) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newItems = fetchMoreFeed()
                val current = _feedItems.value.orEmpty()
                _feedItems.value = current + newItems
                _error.value = null
            } catch (e: Exception) {
                Log.e("TopicDetailFeedVM", "loadMore failed", e)
                _error.value = e.message
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
        _hasMore.value = true
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
                    _hasMore.postValue(false)
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
                _hasMore.postValue(false)
            }

            items
        }
    }
}
