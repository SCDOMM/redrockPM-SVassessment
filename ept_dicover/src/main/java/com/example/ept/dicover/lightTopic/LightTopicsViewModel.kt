package com.example.ept.dicover.lightTopic

import com.example.core.model.FollowCardData
import com.example.core.model.LightTopicPlaylistVideo
import com.example.core.model.LightTopicsResponse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 主题播单详情页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicsViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()

    var loaded = false
        private set

    private val _headerImage = MutableLiveData<String>()
    val headerImage: LiveData<String> = _headerImage

    private val _brief = MutableLiveData<String>()
    val brief: LiveData<String> = _brief

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _items = MutableLiveData<List<LightTopicPlaylistVideo>>()
    val items: LiveData<List<LightTopicPlaylistVideo>> = _items

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

                if (!rawResponse.isSuccessful()) {
                    _error.value = "HTTP错误: ${rawResponse.code()}"
                    return@launch
                }
                val rawBody = rawResponse.body()?.string() ?: ""

                if (rawBody.isEmpty()) {
                    _error.value = "响应体为空"
                    return@launch
                }

                val response = try {
                    RetrofitClient.gson.fromJson(rawBody, LightTopicsResponse::class.java)
                } catch (e: Exception) {
                    Log.e("LightTopicsVM", "JSON parse error", e)
                    _error.value = "JSON解析失败: ${e.message}"
                    return@launch
                }

                if (response == null) {
                    _error.value = "解析结果为null"
                    return@launch
                }

                _headerImage.value = response.headerImage ?: ""
                _brief.value = response.brief ?: ""
                _text.value = response.text ?: ""

                val videoItems = response.itemList
                    .filter { it.type == "autoPlayFollowCard" }
                    .mapNotNull { item ->
                        val data = try {
                            RetrofitClient.gson.fromJson(
                                RetrofitClient.gson.toJsonTree(item.data),
                                FollowCardData::class.java
                            )
                        } catch (e: Exception) { null }
                            ?: return@mapNotNull null
                        val videoData = data.getVideoData() ?: return@mapNotNull null
                        val header = data.header

                        LightTopicPlaylistVideo(
                            id = videoData.id,
                            title = videoData.title,
                            coverUrl = videoData.cover?.feed ?: "",
                            duration = videoData.getDurationLong(),
                            authorName = header?.issuerName ?: videoData.author?.name ?: "",
                            authorIcon = header?.icon ?: videoData.author?.icon ?: "",
                            description = videoData.description,
                            playUrl = videoData.playUrl
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
