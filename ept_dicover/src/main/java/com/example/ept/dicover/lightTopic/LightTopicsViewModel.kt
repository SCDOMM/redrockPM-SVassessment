package com.example.ept.dicover.lightTopic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LightTopicsResponse
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： lightTopics 接口 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicsViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()
    private val gson = Gson()

    var loaded = false
        private set

    private val _headerImage = MutableLiveData<String>()
    val headerImage: LiveData<String> = _headerImage

    private val _brief = MutableLiveData<String>()
    val brief: LiveData<String> = _brief

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _items = MutableLiveData<List<TopicPlaylistVideo>>()
    val items: LiveData<List<TopicPlaylistVideo>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDetail(topicId: Int) {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val rawResponse = withContext(Dispatchers.IO) {
                    api.getTopicDetailRaw(topicId).execute()
                }
                Log.d("LightTopicsVM", "HTTP status=${rawResponse.code()}")

                if (!rawResponse.isSuccessful()) {
                    _error.value = "HTTP错误: ${rawResponse.code()}"
                    return@launch
                }
                val rawBody = rawResponse.body()?.string() ?: ""
                Log.d("LightTopicsVM", "RAW_LEN=${rawBody.length}")

                if (rawBody.isEmpty()) {
                    _error.value = "响应体为空"
                    return@launch
                }

                // 先解析顶层结构
                val topLevel = try {
                    gson.fromJson(rawBody, Map::class.java) as? Map<String, Any>
                } catch (e: Exception) {
                    Log.e("LightTopicsVM", "JSON parse error", e)
                    _error.value = "JSON解析失败: ${e.message}"
                    return@launch
                }

                if (topLevel == null) {
                    _error.value = "解析结果为null"
                    return@launch
                }

                _headerImage.value = topLevel["headerImage"] as? String ?: ""
                _brief.value = topLevel["brief"] as? String ?: ""
                _text.value = topLevel["text"] as? String ?: ""

                @Suppress("UNCHECKED_CAST")
                val itemList = topLevel["itemList"] as? List<Map<String, Any>> ?: emptyList()
                Log.d("LightTopicsVM", "itemList.size=${itemList.size}")

                val videoItems = mutableListOf<TopicPlaylistVideo>()

                for ((index, itemMap) in itemList.withIndex()) {
                    val type = itemMap["type"] as? String ?: ""
                    Log.d("LightTopicsVM", "item[$index] type=$type")
                    if (type != "autoPlayFollowCard") continue

                    @Suppress("UNCHECKED_CAST")
                    val dataMap = itemMap["data"] as? Map<String, Any>
                    if (dataMap == null) {
                        Log.w("LightTopicsVM", "item[$index] data is null")
                        continue
                    }

                    // 提取 header
                    @Suppress("UNCHECKED_CAST")
                    val headerMap = dataMap["header"] as? Map<String, Any>
                    val issuerName = headerMap?.get("issuerName") as? String
                    val headerIcon = headerMap?.get("icon") as? String

                    // 提取 content -> data（视频信息）
                    @Suppress("UNCHECKED_CAST")
                    val contentMap = dataMap["content"] as? Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val videoMap = (contentMap?.get("data") as? Map<String, Any>) ?: contentMap

                    if (videoMap == null) {
                        Log.w("LightTopicsVM", "item[$index] videoMap is null")
                        continue
                    }

                    val title = videoMap["title"] as? String ?: ""
                    if (title.isEmpty()) continue

                    val id = when (val v = videoMap["id"]) {
                        is Number -> v.toLong()
                        is String -> v.toLongOrNull() ?: 0L
                        else -> 0L
                    }

                    @Suppress("UNCHECKED_CAST")
                    val coverMap = videoMap["cover"] as? Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val authorMap = videoMap["author"] as? Map<String, Any>

                    val duration = videoMap["duration"]
                    val durationLong = when (duration) {
                        is Number -> duration.toLong()
                        is Map<*, *> -> (duration["value"] as? Number)?.toLong() ?: 0L
                        else -> 0L
                    }

                    Log.d("LightTopicsVM", "item[$index] title='$title', id=$id")

                    videoItems.add(
                        TopicPlaylistVideo(
                            id = id,
                            title = title,
                            coverUrl = coverMap?.get("feed") as? String ?: "",
                            duration = durationLong,
                            authorName = issuerName ?: authorMap?.get("name") as? String ?: "",
                            authorIcon = headerIcon ?: authorMap?.get("icon") as? String ?: "",
                            description = videoMap["description"] as? String ?: "",
                            playUrl = videoMap["play_url"] as? String ?: videoMap["playUrl"] as? String ?: ""
                        )
                    )
                }

                _items.value = videoItems
                _error.value = null
                Log.d("LightTopicsVM", "Loaded ${videoItems.size} videos for topic $topicId")
            } catch (e: Exception) {
                Log.e("LightTopicsVM", "loadDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
