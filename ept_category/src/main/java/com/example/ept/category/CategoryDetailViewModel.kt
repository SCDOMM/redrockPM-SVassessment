package com.example.ept.category

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

class CategoryDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()
    private val gson = Gson()

    data class TagInfo(val description: String, val headerImage: String, val fallbackCover: String)

    /** 分类名 → 头图 URL */
    private val categoryImageMap = mapOf(
        "生活" to "https://images.unsplash.com/photo-1583220113679-91e9833f1ff7?w=640&q=80&fm=jpg&crop=entropy",
        "动画" to "https://images.unsplash.com/photo-1706076463257-20b41d9519f0?w=640&q=80&fm=jpg&crop=entropy",
        "搞笑" to "https://images.unsplash.com/photo-1601236955994-f27f70bb7f6c?w=640&q=80&fm=jpg&crop=entropy",
        "开胃" to "https://images.unsplash.com/photo-1664232802830-592394491fd2?w=640&q=80&fm=jpg&crop=entropy",
        "创意" to "https://images.unsplash.com/photo-1508004680771-708b02aabdc0?w=640&q=80&fm=jpg&crop=entropy",
        "运动" to "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=640&q=80&fm=jpg&crop=entropy",
        "音乐" to "https://images.unsplash.com/photo-1507838153414-b4b713384a76?w=640&q=80&fm=jpg&crop=entropy",
        "萌宠" to "https://images.unsplash.com/photo-1642332280884-d5b0ef96ed2c?w=640&q=80&fm=jpg&crop=entropy",
        "剧情" to "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=640&q=80&fm=jpg&crop=entropy",
        "科技" to "https://images.unsplash.com/photo-1518770660439-4636190af475?w=640&q=80&fm=jpg&crop=entropy",
        "旅行" to "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=640&q=80&fm=jpg&crop=entropy",
        "影视" to "https://images.unsplash.com/photo-1570385404967-fe4e1b48454b?w=640&q=80&fm=jpg&crop=entropy",
        "记录" to "https://images.unsplash.com/photo-1759329234776-39f0f1d2d3ec?w=640&q=80&fm=jpg&crop=entropy",
        "游戏" to "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=640&q=80&fm=jpg&crop=entropy",
        "综艺" to "https://images.unsplash.com/photo-1781029711493-26385c558e91?w=640&q=80&fm=jpg&crop=entropy",
        "时尚" to "https://images.unsplash.com/photo-1572705824045-3dd0c9a47945?w=640&q=80&fm=jpg&crop=entropy",
        "集锦" to "https://images.unsplash.com/photo-1631159614227-c8d0dce25aee?w=640&q=80&fm=jpg&crop=entropy",
        "广告" to "https://images.unsplash.com/photo-1557858310-9052820906f7?w=640&q=80&fm=jpg&crop=entropy"
    )

    private val allItems = mutableListOf<CategoryItem>()

    /** 标记是否已加载过数据，防止配置变更后重复加载 */
    var loaded = false
        private set

    private val _items = MutableLiveData<List<CategoryItem>>()
    val items: LiveData<List<CategoryItem>> = _items

    private val _tagInfo = MutableLiveData<TagInfo?>()
    val tagInfo: LiveData<TagInfo?> = _tagInfo

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
    fun loadCategoryDetail(actionUrl: String, categoryName: String) {
        loaded = true
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
                val rawItems = body?.itemList ?: emptyList()
                allItems.clear()
                allItems.addAll(parseItems(rawItems))
                nextPageUrl = body?.nextPageUrl
                hasNextPage = nextPageUrl != null
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CategoryDetail", "Loaded ${allItems.size} items, hasNext=$hasNextPage")

                // 从视频的 tags 中查找匹配分类名的 tag，提取头图和描述
                findTagInfo(rawItems, categoryName)
            } catch (e: Exception) {
                Log.e("CategoryDetail", "loadCategoryDetail failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从视频 item 的 tags 数组中查找 name 匹配 categoryName 的 tag
     * 头图用 Unsplash 关键词图片，描述从 API tags 获取，视频封面作为 fallback
     */
    private fun findTagInfo(rawItems: List<com.example.core.model.Item>, categoryName: String) {
        var desc = ""
        var firstCover = ""

        for (item in rawItems) {
            val data = item.data as? Map<*, *> ?: continue

            // 提取第一个视频的封面作为 fallback
            if (firstCover.isEmpty()) {
                firstCover = when (item.type) {
                    "followCard" -> {
                        val content = data["content"] as? Map<*, *> ?: ""
                        val contentData = (content as? Map<*, *>)?.get("data") as? Map<*, *>
                        val cover = contentData?.get("cover") as? Map<*, *>
                        cover?.get("detail") as? String ?: ""
                    }
                    "videoSmallCard" -> {
                        val cover = data["cover"] as? Map<*, *>
                        cover?.get("feed") as? String ?: ""
                    }
                    else -> ""
                }
            }

            // 从 tags 中查找匹配分类名的描述
            if (desc.isEmpty()) {
                val tags = when (item.type) {
                    "followCard" -> {
                        val content = data["content"] as? Map<*, *> ?: continue
                        val contentData = content["data"] as? Map<*, *> ?: continue
                        contentData["tags"] as? List<*> ?: continue
                    }
                    "videoSmallCard" -> {
                        data["tags"] as? List<*> ?: continue
                    }
                    else -> continue
                }
                for (tag in tags) {
                    val tagMap = tag as? Map<*, *> ?: continue
                    if (tagMap["name"] as? String == categoryName) {
                        desc = tagMap["desc"] as? String ?: ""
                        break
                    }
                }
            }

            if (firstCover.isNotEmpty() && desc.isNotEmpty()) break
        }

        // 直接使用映射表中的头图 URL
        val headerImage = categoryImageMap[categoryName] ?: ""

        _tagInfo.value = TagInfo(desc, headerImage, firstCover)
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
                    // followCard.content 是 Item 包装器，不是 VideoData 需要手动解析
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
                    val webUrl = contentData["webUrl"] as? Map<*, *>
                    val webUrlRaw = webUrl?.get("raw") as? String ?: ""
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
                        replyCount = replyCount,
                        webUrl = webUrlRaw
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
                    val webUrl = data["webUrl"] as? Map<*, *>
                    val webUrlRaw = webUrl?.get("raw") as? String ?: ""
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
                        replyCount = replyCount,
                        webUrl = webUrlRaw
                    )
                }
                else -> null
            }
        }
    }
}
