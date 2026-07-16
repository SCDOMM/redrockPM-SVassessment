package com.example.ept.dicover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 发现页分类 ViewModel，加载分类列表
 * email : 3014386984@qq.com
 * date : 2026/7/16 11:39
 */
class DiscoveryViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * 加载分类列表
     */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTabList().execute()
                }
                val body = response.body()
                val list = body?.tabInfo?.tabList
                    ?.filter { it.id > 0 }
                    ?.map { CategoryItem(it.name, it.apiUrl, CategoryAdapter.getIconForCategory(it.name)) }
                    ?: emptyList()
                Log.d("DiscoveryViewModel", "Loaded ${list.size} categories")
                _categories.value = list
                _error.value = null
            } catch (e: Exception) {
                Log.e("DiscoveryViewModel", "loadCategories failed", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
