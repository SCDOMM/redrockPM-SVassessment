package com.example.ept.dicover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.TabItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题列表页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
class TopicListViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    private val _tabs = MutableLiveData<List<TabItem>>()
    val tabs: LiveData<List<TabItem>> = _tabs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    var loaded = false
        private set

    fun loadTabs() {
        loaded = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTagTabList().execute()
                }
                val tabList = response.body()?.tabInfo?.tabList ?: emptyList()
                _tabs.value = tabList
                Log.d("TopicListVM", "Loaded ${tabList.size} tabs")
                _error.value = null
            } catch (e: Exception) {
                Log.e("TopicListVM", "loadTabs failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
