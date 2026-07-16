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

    fun loadCategoryDetail(apiUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getRankListByUrl(apiUrl).execute()
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
                    val followCard = gson.fromJson(gson.toJson(item.data), FollowCardData::class.java)
                    val video = followCard.content ?: return@mapNotNull null

                    CategoryItem.Video(
                        videoId = video.id,
                        title = video.title,
                        coverUrl = video.cover.detail,
                        duration = video.duration,
                        authorName = video.author?.name ?: "",
                        authorIcon = video.author?.icon ?: "",
                        description = video.description
                    )
                }
                else -> null
            }
        }
    }
}
