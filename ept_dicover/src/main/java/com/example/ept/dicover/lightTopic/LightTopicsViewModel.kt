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

    private var _liveData = MutableLiveData<LightTopicsState>()
    val liveData: LiveData<LightTopicsState> get() = _liveData

    fun loadDetail(topicId: Int) {
        loaded = true
        viewModelScope.launch {
            try {
                val rawResponse = withContext(Dispatchers.IO) {
                    api.getTopicDetailRaw(topicId).execute()
                }

                if (!rawResponse.isSuccessful()) {
                    _liveData.postValue(LightTopicsState.ErrorState("HTTP错误: ${rawResponse.code()}"))
                    return@launch
                }
                val rawBody = rawResponse.body()?.string() ?: ""

                if (rawBody.isEmpty()) {
                    _liveData.postValue(LightTopicsState.ErrorState("响应体为空"))
                    return@launch
                }

                val response = try {
                    RetrofitClient.gson.fromJson(rawBody, LightTopicsResponse::class.java)
                } catch (e: Exception) {
                    Log.e("LightTopicsVM", "JSON parse error", e)
                    _liveData.postValue(LightTopicsState.ErrorState("JSON解析失败: ${e.message}"))
                    return@launch
                }

                if (response == null) {
                    _liveData.postValue(LightTopicsState.ErrorState("解析结果为null"))
                    return@launch
                }

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

                _liveData.postValue(LightTopicsState.RefreshState(
                    headerImage = response.headerImage ?: "",
                    brief = response.brief ?: "",
                    text = response.text ?: "",
                    items = videoItems
                ))
                Log.d("LightTopicsVM", "Loaded ${videoItems.size} videos for topic $topicId")
            } catch (e: Exception) {
                Log.e("LightTopicsVM", "loadDetail failed", e)
                _liveData.postValue(LightTopicsState.ErrorState(e.message.toString()))
            }
        }
    }
}

sealed class LightTopicsState {
    data class RefreshState(
        val headerImage: String,
        val brief: String,
        val text: String,
        val items: List<LightTopicPlaylistVideo>
    ) : LightTopicsState()
    data class ErrorState(val errorMsg: String) : LightTopicsState()
}
