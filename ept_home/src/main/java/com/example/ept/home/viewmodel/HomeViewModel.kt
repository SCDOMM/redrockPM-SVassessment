package com.example.ept.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.EyepetizerResponse
import com.example.core.model.FollowCardData
import com.example.core.model.SquareCardCollectionData
import com.example.core.model.VideoData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import kotlinx.coroutines.launch

/**
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeViewModel (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<HomeState>()
    val liveData: LiveData<HomeState> get() = _liveData
    private var nextPageUrl: String? = null
    private var allVideos: List<VideoData> =emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    init {
        refreshLiveData()
    }
    fun refreshLiveData() {
        viewModelScope.launch {
            try {
                val response = appService.getTabDetail("allRec", 0).await()
                nextPageUrl=response.nextPageUrl
                allVideos =parseVideoList(response)
               _liveData.postValue(HomeState.RefreshState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(HomeState.ErrorState(e.message.toString()))
            }
        }
    }
    fun loadingMore(){
        viewModelScope.launch {
            try {
                val response=appService.getTabDetailByUrl(nextPageUrl!!).await()
                nextPageUrl=response.nextPageUrl
                val newVideos = parseVideoList(response)
                allVideos = allVideos + newVideos
                _liveData.postValue(HomeState.LoadingMoreState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(HomeState.ErrorState(e.message.toString()))
            }
        }
    }
    private fun parseVideoList(response: EyepetizerResponse): List<VideoData> {
        return response.itemList.flatMap { item ->
            when (item.type) {
                "video", "videoSmallCard" -> {
                    listOfNotNull(item.data as? VideoData)
                }
                "followCard" -> {
                    val fc = item.data as? FollowCardData
                    listOfNotNull(fc?.content?.data)
                }
                "squareCardCollection" -> {
                    val col = item.data as? SquareCardCollectionData
                    col?.itemList?.mapNotNull { child ->
                        if (child.type == "followCard") {
                            (child.data as? FollowCardData)?.content?.data
                        } else null
                    } ?: emptyList()
                }
                else -> emptyList()
            }
        }
    }
}
sealed class HomeState{
    data class RefreshState(val videoList:MutableList<VideoData>): HomeState()
    data class LoadingMoreState( val newVideoList: MutableList<VideoData>): HomeState()
    data class ErrorState(val errorMsg: String): HomeState()
}