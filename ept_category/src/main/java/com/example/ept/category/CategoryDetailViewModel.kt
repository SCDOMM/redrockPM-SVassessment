package com.example.ept.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.card.FollowCardData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()
    private val gson = Gson()

    private val allItems = mutableListOf<CategoryItem>()

    private val _items = MutableLiveData<List<CategoryItem>>()
    val items: LiveData<List<CategoryItem>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var nextPageUrl: String? = null
    var hasNextPage = true
        private set

    /**
     * 加载分类详情，从 actionUrl 中解析 categoryId
     */
    fun loadCategoryDetail(actionUrl: String) {
        // 从 eyepetizer://category/36/?title=xxx 中解析 categoryId
        val categoryId = Regex("category/(\\d+)").find(actionUrl)?.groupValues?.get(1)?.toIntOrNull()
        if (categoryId == null) {
            _error.value = "无效的分类URL"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getCategoryDetail(categoryId).execute()
                }
                val body = response.body()
                allItems.clear()
                allItems.addAll(parseItems(body?.itemList ?: emptyList()))
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CategoryDetail", "Loaded ${allItems.size} items, hasNext=$hasNextPage")
            } catch (e: Exception) {
                Log.e("CategoryDetail", "loadCategoryDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        val url = nextPageUrl ?: return
        if (_isLoading.value == true) return

        // 拼接 udid 参数，nextPageUrl 原始返回不包含此参数
        val fullUrl = if (url.contains("udid=")) url else {
            val separator = if (url.contains("?")) "&" else "?"
            "${url}${separator}udid=435865baacfc49499632ea13c5a78f944c2f28aa"
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getRankListByUrl(fullUrl).execute()
                }
                val body = response.body()
                val newItems = parseItems(body?.itemList ?: emptyList())
                allItems.addAll(newItems)
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CategoryDetail", "Loaded ${newItems.size} more items, total=${allItems.size}, hasNext=$hasNextPage")
            } catch (e: Exception) {
                Log.e("CategoryDetail", "loadNextPage failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseItems(rawItems: List<com.example.core.model.Item>): List<CategoryItem> {
        return rawItems.mapNotNull { item ->
            when (item.type) {
                "textCard" -> {
                    val data = item.data as? Map<*, *>
                    val text = data?.get("text") as? String ?: return@mapNotNull null
                    CategoryItem.Header(text)
                }
                "followCard" -> {
                    // followCard.content 是 Item 包装器，不是 VideoData — 手动解析
                    val data = item.data as? Map<*, *> ?: return@mapNotNull null
                    val content = data["content"] as? Map<*, *> ?: return@mapNotNull null
                    val contentData = content["data"] as? Map<*, *> ?: return@mapNotNull null
                    val id = (contentData["id"] as? Double)?.toLong() ?: return@mapNotNull null
                    val title = contentData["title"] as? String ?: ""
                    val cover = contentData["cover"] as? Map<*, *>
                    val coverUrl = cover?.get("detail") as? String ?: ""
                    val duration = (contentData["duration"] as? Double)?.toLong() ?: 0
                    val author = contentData["author"] as? Map<*, *>
                    val authorName = author?.get("name") as? String ?: ""
                    val authorIcon = author?.get("icon") as? String ?: ""
                    val description = contentData["description"] as? String ?: ""
                    val playUrl = contentData["playUrl"] as? String ?: ""
                    val category = contentData["category"] as? String ?: ""
                    val consumption = contentData["consumption"] as? Map<*, *>
                    val collectionCount = (consumption?.get("collectionCount") as? Double)?.toInt() ?: 0
                    val shareCount = (consumption?.get("shareCount") as? Double)?.toInt() ?: 0
                    val replyCount = (consumption?.get("replyCount") as? Double)?.toInt() ?: 0
                    CategoryItem.Video(
                        videoId = id,
                        title = title,
                        coverUrl = coverUrl,
                        duration = duration,
                        authorName = authorName,
                        authorIcon = authorIcon,
                        description = description,
                        playUrl = playUrl,
                        category = category,
                        collectionCount = collectionCount,
                        shareCount = shareCount,
                        replyCount = replyCount
                    )
                }
                "videoSmallCard" -> {
                    val data = item.data as? Map<*, *> ?: return@mapNotNull null
                    val id = (data["id"] as? Double)?.toLong() ?: return@mapNotNull null
                    val title = data["title"] as? String ?: ""
                    val cover = data["cover"] as? Map<*, *>
                    val coverUrl = cover?.get("feed") as? String ?: ""
                    val duration = (data["duration"] as? Double)?.toLong() ?: 0
                    val author = data["author"] as? Map<*, *>
                    val authorName = author?.get("name") as? String ?: ""
                    val authorIcon = author?.get("icon") as? String ?: ""
                    val description = data["description"] as? String ?: ""
                    val playUrl = data["playUrl"] as? String ?: ""
                    val category = data["category"] as? String ?: ""
                    val consumption = data["consumption"] as? Map<*, *>
                    val collectionCount = (consumption?.get("collectionCount") as? Double)?.toInt() ?: 0
                    val shareCount = (consumption?.get("shareCount") as? Double)?.toInt() ?: 0
                    val replyCount = (consumption?.get("replyCount") as? Double)?.toInt() ?: 0
                    CategoryItem.Video(
                        videoId = id,
                        title = title,
                        coverUrl = coverUrl,
                        duration = duration,
                        authorName = authorName,
                        authorIcon = authorIcon,
                        description = description,
                        playUrl = playUrl,
                        category = category,
                        collectionCount = collectionCount,
                        shareCount = shareCount,
                        replyCount = replyCount
                    )
                }
                else -> null
            }
        }
    }
}
