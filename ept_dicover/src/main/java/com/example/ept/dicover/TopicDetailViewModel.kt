package com.example.ept.dicover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.community.CommunityItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题详情页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
class TopicDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    private val allItems = mutableListOf<CommunityItem>()

    var loaded = false
        private set

    private val _items = MutableLiveData<List<CommunityItem>>()
    val items: LiveData<List<CommunityItem>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var nextPageUrl: String? = null
    var hasNextPage = true
        private set

    fun loadDetail(tagId: Int, tagName: String) {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTagDynamics(tagId).execute()
                }
                val body = response.body()
                val rawItems = body?.itemList ?: emptyList()
                allItems.clear()

                // 从第一个 pictureFollowCard 提取 header 信息
                var headerDesc = ""
                var headerImage = ""
                for (item in rawItems) {
                    if (item.type == "pictureFollowCard") {
                        val data = item.data as? Map<*, *> ?: continue
                        val content = data["content"] as? Map<*, *> ?: continue
                        val contentData = content["data"] as? Map<*, *>
                        headerDesc = contentData?.get("description") as? String ?: ""
                        val cover = contentData?.get("cover") as? Map<*, *>
                        headerImage = cover?.get("feed") as? String ?: ""
                        break
                    }
                }
                allItems.add(CommunityItem.HeaderCard(tagName, headerDesc, headerImage))

                allItems.addAll(parseItems(rawItems))
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()

                _error.value = null
                Log.d("TopicDetailVM", "Loaded ${allItems.size} items for tag=$tagId")
            } catch (e: Exception) {
                Log.e("TopicDetailVM", "loadDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        val url = nextPageUrl ?: return
        if (_isLoading.value == true) return

        val fullUrl = if (url.contains("udid=")) url else {
            val separator = if (url.contains("?")) "&" else "?"
            "${url}${separator}udid=435865baacfc49499632ea13c5a78f944c2f28aa"
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTabDetailByUrl(fullUrl).execute()
                }
                val body = response.body()
                val newItems = parseItems(body?.itemList ?: emptyList())
                allItems.addAll(newItems)
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
            } catch (e: Exception) {
                Log.e("TopicDetailVM", "loadNextPage failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseItems(rawItems: List<com.example.core.model.Item>): List<CommunityItem> {
        return rawItems.mapNotNull { item ->
            when (item.type) {
                "pictureFollowCard" -> null
                "autoPlayFollowCard" -> {
                    parseUgcFollowCard(item.data as? Map<*, *>)
                }
                else -> null
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseUgcFollowCard(data: Map<*, *>?): CommunityItem.ContentCard? {
        data ?: return null
        val header = data["header"] as? Map<*, *> ?: return null
        val content = data["content"] as? Map<*, *> ?: return null
        val contentData = content["data"] as? Map<*, *> ?: return null

        val id = (contentData["id"] as? Double)?.toLong() ?: return null
        val description = contentData["description"] as? String ?: ""
        val cover = contentData["cover"] as? Map<*, *>
        val coverUrl = cover?.get("feed") as? String ?: ""

        val owner = contentData["owner"] as? Map<*, *>
        val nickname = header["issuerName"] as? String
            ?: owner?.get("nickname") as? String ?: ""
        val avatar = header["icon"] as? String
            ?: owner?.get("avatar") as? String ?: ""

        val consumption = contentData["consumption"] as? Map<*, *>
        val collectionCount = (consumption?.get("collectionCount") as? Double)?.toInt() ?: 0
        val replyCount = (consumption?.get("replyCount") as? Double)?.toInt() ?: 0

        val urls = contentData["urls"] as? List<*>
        val imageUrls = urls?.mapNotNull { it as? String }?.takeIf { it.isNotEmpty() }
            ?: if (coverUrl.isNotEmpty()) listOf(coverUrl) else emptyList()

        return CommunityItem.ContentCard(
            id = id,
            nickname = nickname,
            avatar = avatar,
            coverUrl = coverUrl,
            imageUrls = imageUrls,
            description = description,
            collectionCount = collectionCount,
            replyCount = replyCount,
            isVideo = false,
            duration = 0,
            playUrl = ""
        )
    }
}
