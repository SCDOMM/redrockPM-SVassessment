package com.example.ept.search.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.WeeklyRankMetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.search.utils.SearchResultData
import com.example.ept.search.utils.parseSearchResponse
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：SearchViewModel
 * 类描述：搜索Activity的ViewModel，负责对所有的子Fragment和三大状态协调分配
 * 创建人：韦西波
 * 创建时间：2026-07-18 19:07
 *
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<SearchState>()
    val liveData: LiveData<SearchState> get() = _liveData
    //视频周榜
    private var _rankingLiveData = MutableLiveData<List<WeeklyRankMetroData>>()
    val rankingLiveData: LiveData<List<WeeklyRankMetroData>> get() = _rankingLiveData
    //搜索历史
    private var _historyLiveData = MutableLiveData<String>()
    val historyLiveData: LiveData<String> get() = _historyLiveData
    //推荐搜索
    private var _recommendLiveData = MutableLiveData<List<String>>()
    val recommendLiveData: LiveData<List<String>> get() = _recommendLiveData
    //搜索提示词
    private val _preSearchLiveData = MutableLiveData<List<String>>()
    val preSearchLiveData: LiveData<List<String>> get() = _preSearchLiveData
    //搜索结果
    private val _resultLiveData = MutableLiveData<SearchResultData>()
    val resultLiveData: LiveData<SearchResultData> get() = _resultLiveData

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    init {
        fetchRecommend()
        fetchWeeklyRank()
    }

    fun search(query: String) {
        _historyLiveData.value=query
        viewModelScope.launch {
            try {
                val response = appService.search(query, 0, 10).await()
                val result = parseSearchResponse(response, query)
                _resultLiveData.value = result
                _liveData.value = SearchState.Result
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(SearchState.Failed(e.message.toString()))
            }
        }
    }
    //获取推荐词
    fun fetchRecommend(){
        viewModelScope.launch {
            try {
                val response = appService.getHotQueries().await()
                val result=response.result
                _recommendLiveData.value = result?.item_list
                _liveData.value= SearchState.Recommend
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = SearchState.Failed(e.message.toString())
            }
        }
    }
    //获取视频周榜
    fun fetchWeeklyRank() {
        viewModelScope.launch {
            try {
                val response = appService.getWeeklyRank().await()
                val videoList = response.result?.card_list
                    ?.firstOrNull()
                    ?.cardData
                    ?.body
                    ?.metroList
                    ?.mapNotNull { it.metroData }
                    ?: emptyList()
                _rankingLiveData.value = videoList
                _liveData.value= SearchState.Ranking
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = SearchState.Failed(e.message.toString())
            }
        }
    }
    //获取搜索提示词
    fun fetchPreSearch(query: String) {
        viewModelScope.launch {
            try {
                val response = appService.getPreSearch(query).await()
                val preSearchResult = response.result
                _liveData.value = SearchState.PreSearch
                _preSearchLiveData.value = preSearchResult?.item_list

            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = SearchState.Failed(e.message.toString())
            }
        }
    }
}
//搜索状态
sealed class SearchState {
    object Ranking : SearchState()
    object Recommend: SearchState()
    object PreSearch : SearchState()
    object Result : SearchState()
    data class Failed(val message: String) : SearchState()
}