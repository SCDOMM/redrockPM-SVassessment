package com.example.ept.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
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
    private val api = RetrofitClient.create<UniversalApi>()

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
                allItems.addAll(parseItems(body?.result?.itemList ?: emptyList()))
                // 更新分页信息
                nextPageUrl = null
                hasNextPage = false
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
                val newItems = parseItems(body?.result?.itemList ?: emptyList())
                // 累加新数据到列表
                allItems.addAll(newItems)
                // 更新分页信息
                nextPageUrl = null
                hasNextPage = false
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
    private fun parseItems(rawItems: List<MetroItem>): List<CommunityItem> {
        val result = mutableListOf<CommunityItem>()

        for (item in rawItems) {
            val metroData = item.metroData ?: continue

            when (item.type) {
                // 入口卡片：横向滚动的推荐入口（如主题创作广场、话题讨论大厅）
                "horizontalScrollCard" -> {
                    // MetroItem 不支持 horizontalScrollCard，跳过
                    continue
                }
                // 社区内容卡片：视频或图片帖子
                "communityColumnsCard" -> {
                    // 使用 MetroData 中的字段解析
                    val id = metroData.videoId?.toLongOrNull() ?: metroData.itemId?.toLongOrNull() ?: continue
                    val description = metroData.description ?: metroData.text ?: ""
                    val coverUrl = metroData.cover?.url ?: ""

                    // 解析作者信息
                    val nickname = metroData.author?.nick ?: metroData.nick ?: ""
                    val avatar = metroData.author?.avatar?.url ?: metroData.avatar?.url ?: ""

                    // 解析互动数据（收藏数、评论数）
                    val collectionCount = metroData.consumption?.collectionCount ?: 0
                    val replyCount = metroData.consumption?.commentCount ?: 0

                    // 判断是视频还是图片
                    val isVideo = metroData.videoId != null
                    val duration = metroData.duration?.value?.toLong() ?: 0
                    val playUrl = metroData.playUrl ?: ""

                    // 图片可能有多张
                    val imagesList = metroData.images
                    val imageUrls = if (!isVideo && !imagesList.isNullOrEmpty()) {
                        imagesList.mapNotNull { it.cover?.url }
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
