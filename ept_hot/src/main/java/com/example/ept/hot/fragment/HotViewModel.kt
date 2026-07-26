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
 * description �?热门排行�?ViewModel，管理视频列表数据和加载状�? * email : 3014386984@qq.com
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

    private var nextPageUrl=""

    fun loadHotVideosByUrl(apiUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getRankListByUrl(apiUrl).await()
                _hotList.value = HotState.RefreshState(response.itemList)
                nextPageUrl=response.nextPageUrl?:""
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadMore(){
        viewModelScope.launch {
         try {
             val response=api.getRankListByUrl(nextPageUrl).await()
             _hotList.value = HotState.LoadState(response.itemList)
             Log.d("测试",nextPageUrl)
             nextPageUrl=response.nextPageUrl?:""
             _error.value = null
         }   catch (e: Exception){
          _error.value=e.message
         }
        }
    }
}
sealed class HotState{
    data class RefreshState(val list:List<Item>): HotState()
    data class LoadState(val newList:List<Item>): HotState()
}
