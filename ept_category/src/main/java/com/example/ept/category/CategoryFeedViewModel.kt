package com.example.ept.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.GetPageCard
import com.example.core.model.GetPageMetroItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 分类详情页视频列表 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
class CategoryFeedViewModel : ViewModel() {

    private val api = RetrofitClient.create<SpecficApi>()
    private val gson = Gson()

    private val allItems = mutableListOf<CategoryItem>()

    private val _items = MutableLiveData<List<CategoryItem>>()
    val items: LiveData<List<CategoryItem>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    var hasNextPage = true
        private set

    // 分页所需数据
    private var currentPageLabel = ""
    private var lastCardJson: String? = null
    private var lastMaterialJson: String? = null
    private var lastItemId: String = ""
    private var lastPageParams: String = ""
    private var lastDataSource: String = ""
    private var lastCardIndex: Int = 0

    /**
     * 加载视频列表
     */
    fun loadFeed(pageLabel: String) {
        currentPageLabel = pageLabel
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPageRaw(pageLabel = pageLabel).execute()
                }
                val rawBody = response.body()?.string() ?: ""
                val body = gson.fromJson(rawBody, GetPageResponse::class.java)

                if (body?.code != 0) {
                    _error.value = "加载失败: code=${body?.code}"
                    return@launch
                }

                val result = body.result
                if (result == null) {
                    _error.value = "数据为空"
                    return@launch
                }

                val cardList = result.card_list
                allItems.clear()

                // 解析视频
                for (card in cardList) {
                    allItems.addAll(parseVideoCard(card))
                }

                // 从 call_metro_list card 中提取分页数据
                extractPaginationData(cardList)

                hasNextPage = lastItemId.isNotEmpty()
                _items.value = allItems.toList()
                _error.value = null
                Log.d("CategoryFeed", "Loaded ${allItems.size} items, hasNext=$hasNextPage, page=$pageLabel")
            } catch (e: Exception) {
                Log.e("CategoryFeed", "loadFeed failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 card_list 中提取分页所需数据
     * call_metro_list 类型的 card 包含分页参数
     */
    private fun extractPaginationData(cardList: List<GetPageCard>) {
        for (card in cardList) {
            if (card.type == "call_metro_list") {
                // call_metro_list card 包含 api_request，从中提取参数
                val body = card.card_data?.body ?: continue
                val apiRequest = body.api_request ?: continue
                val params = apiRequest.params ?: continue

                lastCardJson = params["card"] ?: ""
                lastMaterialJson = params["material"] ?: ""
                lastItemId = params["last_item_id"] ?: ""
                lastPageParams = params["page_params"] ?: ""
                lastDataSource = params["data_source"] ?: ""
                currentPageLabel = params["page_label"] ?: ""

                // card_index 从 card 中提取
                val cardIndexStr = params["card_index"]
                lastCardIndex = cardIndexStr?.toIntOrNull() ?: card.card_id

                Log.d("CategoryFeed", "Extracted pagination: dataSource=$lastDataSource, pageLabel=$currentPageLabel, lastItemId=$lastItemId")
                return
            }
        }
        // 如果没有 call_metro_list card，没有更多数据
        hasNextPage = false
    }

    /**
     * 加载下一页 - 使用 call_metro_list_v2 接口
     */
    fun loadNextPage() {
        if (_isLoading.value == true) return
        if (lastItemId.isEmpty() || lastCardJson.isNullOrEmpty()) {
            hasNextPage = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.callMetroListV2(
                        cardIndex = lastCardIndex,
                        material = lastMaterialJson ?: "",
                        lastItemId = lastItemId,
                        pageParams = lastPageParams,
                        pageLabel = lastDataSource,
                        card = lastCardJson ?: "",
                        dataSource = lastDataSource
                    ).execute()
                }
                val body = response.body()

                if (body?.code != 0) {
                    hasNextPage = false
                    return@launch
                }

                val result = body?.result
                if (result == null) {
                    hasNextPage = false
                    return@launch
                }

                val metroItems = result.item_list
                val newItems = mutableListOf<CategoryItem>()
                for (metro in metroItems) {
                    parseMetroItem(metro)?.let { newItems.add(it) }
                }

                // 更新 lastItemId 用于下次分页
                if (result.last_item_id.isNotEmpty()) {
                    lastItemId = result.last_item_id
                }

                if (newItems.isEmpty()) {
                    hasNextPage = false
                } else {
                    allItems.addAll(newItems)
                    hasNextPage = lastItemId.isNotEmpty()
                }

                if (newItems.isEmpty()) {
                    hasNextPage = false
                } else {
                    allItems.addAll(newItems)
                    hasNextPage = lastItemId.isNotEmpty()
                }

                _items.value = allItems.toList()
                _error.value = null
                Log.d("CategoryFeed", "Loaded ${newItems.size} more, total=${allItems.size}, hasNext=$hasNextPage")
            } catch (e: Exception) {
                Log.e("CategoryFeed", "loadNextPage failed", e)
                hasNextPage = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 从 card_list 中解析视频项
     */
    private fun parseVideoCard(card: GetPageCard): List<CategoryItem> {
        if (card.type != "set_metro_list" && card.type != "call_metro_list") return emptyList()

        val metroList = card.card_data?.body?.metro_list ?: return emptyList()
        val result = mutableListOf<CategoryItem>()

        for (metro in metroList) {
            parseMetroItem(metro)?.let { result.add(it) }
        }
        return result
    }

    /**
     * 解析单个 metro item 为 CategoryItem
     */
    private fun parseMetroItem(metro: GetPageMetroItem): CategoryItem? {
        val data = metro.metro_data ?: return null
        if (metro.type == "nav") return null

        val authorName = data.author?.nick ?: ""
        val authorIcon = data.author?.avatar?.url ?: ""
        val consumption = data.consumption

        // 判断是视频还是图片
        val video = data.video
        val isVideo = video?.video_id?.isNotEmpty() == true

        if (isVideo) {
            // 视频帖子
            val title = video?.title ?: ""
            if (title.isEmpty()) return null

            return CategoryItem.Video(
                videoId = video?.video_id?.toLongOrNull() ?: 0L,
                title = title,
                coverUrl = video?.cover?.url ?: "",
                duration = video?.duration?.value ?: 0L,
                authorName = authorName,
                authorIcon = authorIcon,
                description = data.text ?: "",
                playUrl = video?.play_url ?: "",
                category = "",
                collectionCount = consumption?.collection_count ?: 0,
                shareCount = consumption?.share_count ?: 0,
                replyCount = consumption?.comment_count ?: 0,
                webUrl = ""
            )
        } else {
            // 图片帖子
            val images = data.images
            if (images.isNullOrEmpty()) return null

            val imageUrls = images.mapNotNull { it.cover?.url }
            if (imageUrls.isEmpty()) return null

            val itemId = data.item_id.toLongOrNull() ?: 0L

            return CategoryItem.Image(
                id = itemId,
                title = data.text ?: "",
                imageUrls = imageUrls,
                authorName = authorName,
                authorIcon = authorIcon,
                description = data.text ?: "",
                likeCount = consumption?.like_count ?: 0,
                commentCount = consumption?.comment_count ?: 0,
                collectionCount = consumption?.collection_count ?: 0,
                shareCount = consumption?.share_count ?: 0
            )
        }
    }
}
