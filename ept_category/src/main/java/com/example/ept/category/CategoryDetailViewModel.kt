package com.example.ept.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.FollowCardData
import com.example.core.model.TextCardData
import com.example.core.model.VideoData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryDetailViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

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
            if (firstCover.isEmpty()) {
                firstCover = when (item.type) {
                    "followCard" -> {
                        (item.data as? FollowCardData)?.content?.data?.cover?.detail ?: ""
                    }
                    "videoSmallCard" -> {
                        (item.data as? VideoData)?.cover?.feed ?: ""
                    }
                    else -> ""
                }
            }

            if (desc.isEmpty()) {
                val tags = when (item.type) {
                    "followCard" -> {
                        val card = item.data as? FollowCardData ?: continue
                        card.content?.data?.tags ?: continue
                    }
                    "videoSmallCard" -> {
                        val v = item.data as? VideoData ?: continue
                        v.tags ?: continue
                    }
                    else -> continue
                }
                for (tag in tags) {
                    if (tag.name == categoryName) {
                        desc = tag.desc ?: ""
                        break
                    }
                }
            }

            if (firstCover.isNotEmpty() && desc.isNotEmpty()) break
        }

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
                    val card = item.data as? TextCardData ?: return@mapNotNull null
                    val text = card.text ?: return@mapNotNull null
                    CategoryItem.Header(text)
                }
                "followCard" -> {
                    val card = item.data as? FollowCardData ?: return@mapNotNull null
                    val v = card.content?.data ?: return@mapNotNull null
                    CategoryItem.Video(
                        videoId = v.id,
                        title = v.title,
                        coverUrl = v.cover?.detail ?: "",
                        duration = v.duration,
                        authorName = v.author?.name ?: "",
                        authorIcon = v.author?.icon ?: "",
                        description = v.description,
                        playUrl = v.playUrl,
                        category = v.category,
                        collectionCount = v.consumption?.collectionCount ?: 0,
                        shareCount = v.consumption?.shareCount ?: 0,
                        replyCount = v.consumption?.replyCount ?: 0,
                        webUrl = v.webUrl?.raw ?: ""
                    )
                }
                "videoSmallCard" -> {
                    val v = item.data as? VideoData ?: return@mapNotNull null
                    CategoryItem.Video(
                        videoId = v.id,
                        title = v.title,
                        coverUrl = v.cover?.feed ?: "",
                        duration = v.duration,
                        authorName = v.author?.name ?: "",
                        authorIcon = v.author?.icon ?: "",
                        description = v.description,
                        playUrl = v.playUrl,
                        category = v.category,
                        collectionCount = v.consumption?.collectionCount ?: 0,
                        shareCount = v.consumption?.shareCount ?: 0,
                        replyCount = v.consumption?.replyCount ?: 0,
                        webUrl = v.webUrl?.raw ?: ""
                    )
                }
                else -> null
            }
        }
    }
}
