package com.example.ept.hot.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Item
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.launch

class HotViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    private val _hotList = MutableLiveData<List<Item>>()
    val hotList: LiveData<List<Item>> = _hotList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadHotVideosByUrl(apiUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getRankListByUrl(apiUrl)
                _hotList.value = response.itemList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
