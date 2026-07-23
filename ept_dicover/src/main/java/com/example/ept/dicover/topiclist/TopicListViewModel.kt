package com.example.ept.dicover.topiclist

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
/**
 * 话题列表页 ViewModel
 * 加载话题分类 Tab 列表
 */
class TopicListViewModel : ViewModel() {

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<KaiyanApi>()

    /** Tab 列表，展示话题分类 */
    private val _tabs = MutableLiveData<List<TabItem>>()
    val tabs: LiveData<List<TabItem>> = _tabs

    /** 加载状态 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 标记是否已加载过数据，防止返回时重复加载 */
    var loaded = false
        private set

    /**
     * 加载 Tab 列表
     * 从 API 获取话题分类标签
     */
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
