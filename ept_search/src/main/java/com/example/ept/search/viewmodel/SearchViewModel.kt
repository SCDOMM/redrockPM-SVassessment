package com.example.ept.search.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.search.utils.SearchResultData
import com.example.ept.search.utils.parseSearchResponse
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：SearchViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 19:07
 *
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<SearchState>()
    val liveData: LiveData<SearchState> get() = _liveData
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    private val _resultLiveData = MutableLiveData<SearchResultData>()
    val resultLiveData: LiveData<SearchResultData> get() = _resultLiveData

    fun search(query: String, start: Int = 0, num: Int = 20) {
        viewModelScope.launch {
            try {
                val response = appService.search(query, start, num).await()
                val result = parseSearchResponse(response)
                _resultLiveData.value = result
                 _liveData.value = SearchState.Result

            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(SearchState.Failed(e.message.toString()))
            }
        }
    }

}

sealed class SearchState {
    object Initial : SearchState()
    object Recommend : SearchState()
    object Result : SearchState()
    object Searching : SearchState()
    data class Failed(val message: String) : SearchState()
}