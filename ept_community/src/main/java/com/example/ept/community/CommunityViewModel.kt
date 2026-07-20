package com.example.ept.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 社区页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/17  19:47
 */
class CommunityViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()
    private val gson = Gson()

    private val allItems = mutableListOf<CommunityItem>()

    private val _items = MutableLiveData<List<CommunityItem>>()
    val items: LiveData<List<CommunityItem>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var nextPageUrl: String? = null
    var hasNextPage = true
        private set
    var loaded = false
        private set

    fun refresh() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getCommunityRec().execute()
                }
                val body = response.body()
                allItems.clear()
                allItems.addAll(parseItems(body?.itemList ?: emptyList()))
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CommunityVM", "Loaded ${allItems.size} items, hasNext=$hasNextPage")
            } catch (e: Exception) {
                Log.e("CommunityVM", "refresh failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        val url = nextPageUrl ?: return
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getRankListByUrl(url).execute()
                }
                val body = response.body()
                val newItems = parseItems(body?.itemList ?: emptyList())
                allItems.addAll(newItems)
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CommunityVM", "Loaded ${newItems.size} more, total=${allItems.size}")
            } catch (e: Exception) {
                Log.e("CommunityVM", "loadNextPage failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseItems(rawItems: List<com.example.core.model.Item>): List<CommunityItem> {
        val result = mutableListOf<CommunityItem>()

        for (item in rawItems) {
            val data = item.data as? Map<*, *> ?: continue

            when (item.type) {
                // 入口卡片
                "horizontalScrollCard" -> {
                    val dataType = data["dataType"] as? String ?: ""
                    if (dataType == "ItemCollection") {
                        val innerItems = data["itemList"] as? List<*> ?: continue
                        for (inner in innerItems) {
                            val innerMap = inner as? Map<*, *> ?: continue
                            val innerData = innerMap["data"] as? Map<*, *> ?: continue
                            val title = innerData["title"] as? String ?: continue
                            val subTitle = innerData["subTitle"] as? String ?: ""
                            val bgPicture = innerData["bgPicture"] as? String ?: ""
                            result.add(CommunityItem.EntryCard(title, subTitle, bgPicture))
                        }
                    }
                }
                // 社区内容卡片
                "communityColumnsCard" -> {
                    val header = data["header"] as? Map<*, *> ?: continue
                    val content = data["content"] as? Map<*, *> ?: continue
                    val contentData = content["data"] as? Map<*, *> ?: continue
                    val contentType = content["type"] as? String ?: ""

                    val id = (contentData["id"] as? Double)?.toLong() ?: continue
                    val description = contentData["description"] as? String ?: ""
                    val cover = contentData["cover"] as? Map<*, *>
                    val coverUrl = cover?.get("feed") as? String ?: ""

                    val owner = contentData["owner"] as? Map<*, *>
                    val nickname = owner?.get("nickname") as? String ?: ""
                    val avatar = owner?.get("avatar") as? String ?: ""

                    val consumption = contentData["consumption"] as? Map<*, *>
                    val collectionCount = (consumption?.get("collectionCount") as? Double)?.toInt() ?: 0
                    val replyCount = (consumption?.get("replyCount") as? Double)?.toInt() ?: 0

                    // 判断是视频还是图片
                    val isVideo = contentType == "video"
                    val duration = if (isVideo) (contentData["duration"] as? Double)?.toLong() ?: 0 else 0
                    val playUrl = if (isVideo) contentData["playUrl"] as? String ?: "" else ""

                    // 图片可能有多张
                    val urls = contentData["urls"] as? List<*>
                    val imageUrls = if (!isVideo && urls != null && urls.isNotEmpty()) {
                        urls.mapNotNull { it as? String }
                    } else {
                        if (coverUrl.isNotEmpty()) listOf(coverUrl) else emptyList()
                    }

                    result.add(CommunityItem.ContentCard(
                        id = id,
                        nickname = nickname,
                        avatar = avatar,
                        coverUrl = coverUrl,
                        imageUrls = imageUrls,
                        description = description,
                        collectionCount = collectionCount,
                        replyCount = replyCount,
                        isVideo = isVideo,
                        duration = duration,
                        playUrl = playUrl
                    ))
                }
            }
        }
        return result
    }
}
