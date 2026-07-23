package com.example.ept.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
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

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<SpecficApi>()
    /** Gson 实例，用于 JSON 解析 */
    private val gson = Gson()

    /** 所有已加载的数据项，用于分页累加 */
    private val allItems = mutableListOf<CommunityItem>()

    /** 社区数据列表，供 Fragment 观察并展示 */
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
    /** 标记是否已加载过数据，防止返回时重复加载 */
    var loaded = false
        private set

    /**
     * 下拉刷新：重新加载社区推荐内容
     * 清空现有数据，从第一页开始加载
     */
    fun refresh() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 在 IO 线程执行网络请求
                val response = withContext(Dispatchers.IO) {
                    api.getCommunityRec().execute()
                }
                val body = response.body()
                // 清空并重新加载数据
                allItems.clear()
                allItems.addAll(parseItems(body?.itemList ?: emptyList()))
                // 更新分页信息
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

    /**
     * 加载下一页数据
     * 使用 nextPageUrl 请求更多内容并累加到列表
     */
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
                // 累加新数据到列表
                allItems.addAll(newItems)
                // 更新分页信息
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

    /**
     * 解析 API 返回的原始数据为 CommunityItem 列表
     * 支持两种卡片类型：入口卡片和社区内容卡片
     */
    private fun parseItems(rawItems: List<com.example.core.model.Item>): List<CommunityItem> {
        val result = mutableListOf<CommunityItem>()

        for (item in rawItems) {
            val data = item.data as? Map<*, *> ?: continue

            when (item.type) {
                // 入口卡片：横向滚动的推荐入口（如主题创作广场、话题讨论大厅）
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
                // 社区内容卡片：视频或图片帖子
                "communityColumnsCard" -> {
                    val header = data["header"] as? Map<*, *> ?: continue
                    val content = data["content"] as? Map<*, *> ?: continue
                    val contentData = content["data"] as? Map<*, *> ?: continue
                    val contentType = content["type"] as? String ?: ""

                    // 解析基础信息
                    val id = (contentData["id"] as? Double)?.toLong() ?: continue
                    val description = contentData["description"] as? String ?: ""
                    val cover = contentData["cover"] as? Map<*, *>
                    val coverUrl = cover?.get("feed") as? String ?: ""

                    // 解析作者信息
                    val owner = contentData["owner"] as? Map<*, *>
                    val nickname = owner?.get("nickname") as? String ?: ""
                    val avatar = owner?.get("avatar") as? String ?: ""

                    // 解析互动数据（收藏数、评论数）
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
