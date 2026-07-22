package com.example.ept.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroData
import com.example.core.model.utils.safeInt
import com.example.core.model.utils.safeString
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.home.utils.parseHomeVideos
import kotlinx.coroutines.launch

/**
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<HomeState>()
    val liveData: LiveData<HomeState> get() = _liveData
    private var lastItemId = 1
    private var cardJSON = ""
    private var materialJSON = ""
    private var allVideos: List<MetroData> = emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    init {
        refreshLiveData()
    }

    fun refreshLiveData() {
        viewModelScope.launch {
            try {
                val response = appService.getPage("recommend", "card").await()
                allVideos = parseHomeVideos(response)
                val callMetroCard = response.result?.cardList?.find { it.type == "call_metro_list" }
                val params = callMetroCard?.cardData?.body?.apiRequest?.params
                if (params != null) {
                    materialJSON = params.safeString("material")
                    cardJSON = params.safeString("card")
                    lastItemId = params.safeInt("last_item_id")
                }
                _liveData.postValue(HomeState.RefreshState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(HomeState.ErrorState(e.message.toString()))
            }
        }
    }

    fun loadingMore() {
        viewModelScope.launch {
            try {
                val response = appService.getMoreHomePage(
                    "recommend",
                    "recommend_feed",
                    materialJSON,
                    cardJSON,
                    lastItemId
                ).await()
                lastItemId = response.result?.last_item_id ?: 0
                response.result?.item_list ?: emptyList()

                val newVideos: List<MetroData> = response.result?.item_list
                    ?.mapNotNull { it.metroData }   // 只保留非 null 的 metroData
                    ?: emptyList()
                allVideos = allVideos + newVideos

                _liveData.postValue(HomeState.LoadingMoreState(allVideos.toMutableList()))

            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(HomeState.ErrorState(e.message.toString()))
            }
        }
    }
}

sealed class HomeState {
    data class RefreshState(val videoList: MutableList<MetroData>) : HomeState()
    data class LoadingMoreState(val newVideoList: MutableList<MetroData>) : HomeState()
    data class ErrorState(val errorMsg: String) : HomeState()
}