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
 * 类名称：ResultVideosFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 22:07
 *
 */
class ResultVideosViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<VideosState>()
    val liveData: LiveData<VideosState> get() = _liveData
    private var lastItemId = 2
    private lateinit var query: String
    private var allVideos: List<MetroData> = emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allVideos: MutableList<MetroData>, query: String) {
        this.query = query
        this.allVideos = allVideos
        _liveData.value = VideosState.InitState(allVideos)
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                val response = appService.searchLoad(query, "video", lastItemId, 10).await()
                val resultData = parseSearchResponseV2(response)
                allVideos = allVideos + resultData.videoList
                lastItemId = response.result?.last_item_id ?: 0
                _liveData.value = VideosState.LoadingMoreState(allVideos.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = VideosState.ErrorState(e.message.toString())
            }
        }
    }


}

sealed class VideosState {
    data class InitState(val videoList: MutableList<MetroData>) : VideosState()
    data class RefreshState(val videoList: MutableList<MetroData>) : VideosState()
    data class LoadingMoreState(val newVideoList: MutableList<MetroData>) : VideosState()
    data class ErrorState(val errorMsg: String) : VideosState()
}