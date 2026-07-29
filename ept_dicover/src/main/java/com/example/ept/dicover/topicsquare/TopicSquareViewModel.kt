package com.example.ept.dicover.topicsquare

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.TabItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题广场列表页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareViewModel : ViewModel() {

    private val api = RetrofitClient.create<UniversalApi>()

    var loaded = false
        private set

    private var _liveData = MutableLiveData<TopicSquareState>()
    val liveData: LiveData<TopicSquareState> get() = _liveData

    fun loadTabs() {
        loaded = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getNav(tabLabel = "topic_square").execute()
                }
                val body = response.body()
                if (body?.code != 0) {
                    _liveData.postValue(TopicSquareState.ErrorState("加载失败: code=${body?.code}"))
                    return@launch
                }

                val navResult = body?.result
                if (navResult == null) {
                    _liveData.postValue(TopicSquareState.ErrorState("数据为空"))
                    return@launch
                }

                val tabItems = navResult.nav_list.map { nav ->
                    TabItem(
                        id = 0,
                        name = nav.title,
                        apiUrl = nav.page_label
                    )
                }

                _liveData.postValue(TopicSquareState.RefreshState(tabItems))
                Log.d("TopicSquareVM", "Loaded ${tabItems.size} tabs")
            } catch (e: Exception) {
                Log.e("TopicSquareVM", "loadTabs failed", e)
                _liveData.postValue(TopicSquareState.ErrorState(e.message.toString()))
            }
        }
    }
}

sealed class TopicSquareState {
    data class RefreshState(val tabs: List<TabItem>) : TopicSquareState()
    data class ErrorState(val errorMsg: String) : TopicSquareState()
}
