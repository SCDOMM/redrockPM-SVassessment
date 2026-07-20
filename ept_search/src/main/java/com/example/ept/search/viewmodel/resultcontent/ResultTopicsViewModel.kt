package com.example.ept.search.viewmodel.resultcontent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.search.utils.parseSearchResponseV2
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultTopicsFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultTopicsViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<TopicsState>()
    val liveData: LiveData<TopicsState> get() = _liveData
    private var lastItemId = 2
    private var allTopics: List<MetroData> = emptyList()
    private lateinit var query: String
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allTopics: MutableList<MetroData>, query: String) {
        this.query = query
        this.allTopics = allTopics
        _liveData.value = TopicsState.InitState(allTopics)
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                val response=appService.searchLoad(query,"topic",lastItemId,10).await()
                val resultData= parseSearchResponseV2(response)
                allTopics=allTopics+resultData.topicList
                lastItemId=response.result?.last_item_id?:0
                _liveData.value= TopicsState.LoadingMoreState(allTopics.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= TopicsState.ErrorState(e.message.toString())
            }
        }
    }
}

sealed class TopicsState {
    data class InitState(val topicData: MutableList<MetroData>) : TopicsState()
    data class RefreshState(val topicList: MutableList<MetroData>) : TopicsState()
    data class LoadingMoreState(val newTopicList: MutableList<MetroData>) : TopicsState()
    data class ErrorState(val errorMsg: String) : TopicsState()
}