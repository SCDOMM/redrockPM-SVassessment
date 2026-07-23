package com.example.ept.search.viewmodel.resultcontent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SearchApi
import com.example.core.network.await
import com.example.core.common.parseLoadSearch
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultGraphicsViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultGraphicsViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<GraphicsState>()
    val liveData: LiveData<GraphicsState> get() = _liveData
    private var allGraphics: List<MetroData> = emptyList()
    private var lastItemId = "2"
    private var query = ""
    private val appService: SearchApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allGraphics: MutableList<MetroData>, query: String) {
        this.allGraphics = allGraphics
        this.query = query
        _liveData.value = GraphicsState.InitState(allGraphics)
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                val response = appService.searchLoad(query, "graphic", lastItemId, 10).await()
                val loadResult = parseLoadSearch(response)
                lastItemId = response.result?.lastItemId ?: "0"
                allGraphics = allGraphics + loadResult.graphicList
                _liveData.value = GraphicsState.LoadingMoreState(allGraphics.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = GraphicsState.ErrorState(e.message.toString())
            }
        }
    }
}

sealed class GraphicsState {
    data class InitState(val graphicList: MutableList<MetroData>) : GraphicsState()
    data class RefreshState(val graphicList: MutableList<MetroData>) : GraphicsState()
    data class LoadingMoreState(val newGraphicList: MutableList<MetroData>) : GraphicsState()
    data class ErrorState(val errorMsg: String) : GraphicsState()
}