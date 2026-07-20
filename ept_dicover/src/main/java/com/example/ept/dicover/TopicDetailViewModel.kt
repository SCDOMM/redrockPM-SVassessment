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

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<KaiyanApi>()

    /** 所有已加载的数据项，用于分页累加 */
    private val allItems = mutableListOf<CommunityItem>()

    /** 标记是否已加载过数据，防止返回时重复加载 */
    var loaded = false
        private set

    /** 话题动态列表，供 Activity 观察并展示 */
    private val _items = MutableLiveData<List<CommunityItem>>()
    val items: LiveData<List<CommunityItem>> = _items

    /** 加载状态，控制下拉刷新动画显示 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息，显示网络请求失败时的错误提示 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 下一页的 URL 地址，用于分页加载 */
    private var nextPageUrl: String? = null
    /** 是否有下一页数据 */
    var hasNextPage = true
        private set

    /**
     * 加载话题详情数据
     * @param tagId 话题 ID
     * @param tagName 话题名称
     */
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

                // 从第一个 pictureFollowCard 提取 header 信息（话题头图和描述）
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
                // 添加话题标题卡片
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

    /**
     * 加载下一页数据
     * 使用 nextPageUrl 请求更多内容并累加到列表
     */
    fun loadNextPage() {
        val url = nextPageUrl ?: return
        if (_isLoading.value == true) return

        // 确保 URL 包含 udid 参数
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
                // 累加新数据到列表
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

    /**
     * 解析 API 返回的原始数据为 CommunityItem 列表
     * 只解析 autoPlayFollowCard 类型（视频/图片帖子）
     */
    private fun parseItems(rawItems: List<com.example.core.model.Item>): List<CommunityItem> {
        return rawItems.mapNotNull { item ->
            when (item.type) {
                "pictureFollowCard" -> null  // 跳过图片关注卡片（已在 header 中处理）
                "autoPlayFollowCard" -> {
                    parseUgcFollowCard(item.data as? Map<*, *>)
                }
                else -> null
            }
        }
    }

    /**
     * 解析 autoPlayFollowCard 数据为 ContentCard
     * @param data 原始数据 Map
     * @return ContentCard 或 null（解析失败时）
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseUgcFollowCard(data: Map<*, *>?): CommunityItem.ContentCard? {
        data ?: return null
        val header = data["header"] as? Map<*, *> ?: return null
        val content = data["content"] as? Map<*, *> ?: return null
        val contentData = content["data"] as? Map<*, *> ?: return null

        // 解析基础信息
        val id = (contentData["id"] as? Double)?.toLong() ?: return null
        val description = contentData["description"] as? String ?: ""
        val cover = contentData["cover"] as? Map<*, *>
        val coverUrl = cover?.get("feed") as? String ?: ""

        // 解析作者信息（优先从 header 获取）
        val owner = contentData["owner"] as? Map<*, *>
        val nickname = header["issuerName"] as? String
            ?: owner?.get("nickname") as? String ?: ""
        val avatar = header["icon"] as? String
            ?: owner?.get("avatar") as? String ?: ""

        // 解析互动数据（收藏数、评论数）
        val consumption = contentData["consumption"] as? Map<*, *>
        val collectionCount = (consumption?.get("collectionCount") as? Double)?.toInt() ?: 0
        val replyCount = (consumption?.get("replyCount") as? Double)?.toInt() ?: 0

        // 解析图片列表
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
