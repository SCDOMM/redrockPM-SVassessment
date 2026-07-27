package com.example.ept.hot.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Item
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import com.example.core.network.await
import kotlinx.coroutines.launch

/**
 * description ：热门排行榜 ViewModel，管理视频列表数据和加载状态
 * email : 3014386984@qq.com
 * date : 2026/7/15 15:46
 */
class HotViewModel : ViewModel() {

    private val api = RetrofitClient.create<UniversalApi>()

    private val _hotList = MutableLiveData<HotState>()
    val hotList: LiveData<HotState> = _hotList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var strategy = ""
    private var currentStart = 0
    private var hasMore = true

    private fun buildUrl(strategy: String, start: Int, num: Int = 10): String {
        return "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=$strategy&start=$start&num=$num"
    }

    fun loadHotVideos(strategy: String) {
        this.strategy = strategy
        this.currentStart = 0
        this.hasMore = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getRankList(buildUrl(strategy, 0)).await()
                _hotList.value = HotState.RefreshState(response.itemList)
                currentStart = 10
                hasMore = !response.itemList.isNullOrEmpty()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (!hasMore || strategy.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = api.getRankList(buildUrl(strategy, currentStart)).await()
                val list = response.itemList.orEmpty()
                _hotList.value = HotState.LoadState(list)
                currentStart += 10
                hasMore = list.isNotEmpty()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

sealed class HotState {
    data class RefreshState(val list: List<Item>) : HotState()
    data class LoadState(val newList: List<Item>) : HotState()
}
