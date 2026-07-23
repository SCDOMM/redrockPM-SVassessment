package com.example.ept.dicover.lightTopic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.FollowCardData
import com.example.core.model.Item
import com.example.core.model.LightTopicsResponse
import com.example.core.network.ItemDeserializer
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： lightTopics 接口 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicsViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    var loaded = false
        private set

    /** 头部图片 URL */
    private val _headerImage = MutableLiveData<String>()
    val headerImage: LiveData<String> = _headerImage

    /** 简介文本 */
    private val _brief = MutableLiveData<String>()
    val brief: LiveData<String> = _brief

    /** 详细描述文本 */
    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    /** 视频列表数据 */
    private val _items = MutableLiveData<List<TopicPlaylistVideo>>()
    val items: LiveData<List<TopicPlaylistVideo>> = _items

    /** 加载状态 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 加载轻话题详情数据 */
    fun loadDetail(topicId: Int) {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 先用 raw 接口获取原始响应，用于调试
                val rawResponse = withContext(Dispatchers.IO) {
                    api.getTopicDetailRaw(topicId).execute()
                }
                Log.d("LightTopicsVM", "HTTP status=${rawResponse.code()}, isSuccessful=${rawResponse.isSuccessful()}")
                val rawBody = rawResponse.body()?.string() ?: ""
                Log.d("LightTopicsVM", "RAW_LEN=${rawBody.length}")
                Log.d("LightTopicsVM", "RAWBody=${rawBody.take(2000)}")

                if (!rawResponse.isSuccessful()) {
                    _error.value = "HTTP错误: ${rawResponse.code()}"
                    return@launch
                }
                if (rawBody.isEmpty()) {
                    _error.value = "响应体为空"
                    return@launch
                }

                // 手动解析（注册 ItemDeserializer 处理 autoPlayFollowCard 类型）
                val gson = GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(
                        Item::class.java,
                        ItemDeserializer()
                    )
                    .create()
                val body = try {
                    gson.fromJson(rawBody, LightTopicsResponse::class.java)
                } catch (e: Exception) {
                    Log.e("LightTopicsVM", "JSON parse error", e)
                    _error.value = "JSON解析失败: ${e.message}"
                    return@launch
                }
                if (body == null) {
                    _error.value = "解析结果为null"
                    return@launch
                }

                _headerImage.value = body.headerImage
                _brief.value = body.brief
                _text.value = body.text

                val videoItems = mutableListOf<TopicPlaylistVideo>()
                Log.d("LightTopicsVM", "itemList.size=${body.itemList.size}")
                for ((index, item) in body.itemList.withIndex()) {
                    Log.d("LightTopicsVM", "item[$index] type=${item.type}, data class=${item.data?.javaClass?.simpleName}")
                    if (item.type != "autoPlayFollowCard") continue
                    val followCard = item.data as? FollowCardData
                    if (followCard == null) {
                        Log.w("LightTopicsVM", "item[$index] followCard is null, data=${item.data}")
                        continue
                    }
                    Log.d("LightTopicsVM", "item[$index] followCard.header=${followCard.header != null}, followCard.content=${followCard.content != null}")
                    val videoData = followCard.content?.data
                    if (videoData == null) {
                        Log.w("LightTopicsVM", "item[$index] videoData is null, content=${followCard.content}")
                        continue
                    }
                    Log.d("LightTopicsVM", "item[$index] title='${videoData.title}', id=${videoData.id}")
                    if (videoData.title.isEmpty()) continue

                    videoItems.add(
                        TopicPlaylistVideo(
                            id = videoData.id,
                            title = videoData.title,
                            coverUrl = videoData.cover?.feed ?: "",
                            duration = videoData.duration,
                            authorName = followCard.header?.issuerName ?: videoData.author?.name
                            ?: "",
                            authorIcon = followCard.header?.icon ?: videoData.author?.icon ?: "",
                            description = videoData.description,
                            playUrl = videoData.playUrl
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
