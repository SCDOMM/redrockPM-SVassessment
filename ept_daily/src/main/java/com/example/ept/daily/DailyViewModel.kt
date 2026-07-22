package com.example.ept.daily

import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.daily
 * 类名称：DailyViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 21:11
 *
 */
class DailyViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<DailyState>()
    val liveData: LiveData<DailyState> get() = _liveData
    private var lastItemId = 1
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
                val response = appService.getPage("daily_issue", "card").await()
                allVideos = parseDailyVideos(response)
                val callMetroCard = response.result?.cardList?.find { it.type == "call_metro_list" }
                val params = callMetroCard?.cardData?.body?.apiRequest?.params
                if (params != null) {
                    materialJSON = params.safeString("material")
                    lastItemId = params.safeInt("last_item_id")
                }
                Log.d("DailyVM", "material=$materialJSON, lastItemId=$lastItemId")
                _liveData.postValue(DailyState.RefreshState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(DailyState.ErrorState(e.message.toString()))
            }
        }
    }

    fun loadingMore() {
        viewModelScope.launch {
            try {
                val response = appService.getMoreDailyPage(
                    "history_issue_feed",
                    materialJSON,
                    lastItemId
                ).await()
                lastItemId = response.result?.last_item_id ?: 0

                val newVideos: List<MetroData> = response.result?.item_list
                    ?.mapNotNull { it.metroData }
                    ?: emptyList()
                allVideos = allVideos + newVideos

                _liveData.postValue(DailyState.LoadingMoreState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(DailyState.ErrorState(e.message.toString()))
            }
        }
    }

}

sealed class DailyState {
    data class RefreshState(val videoList: MutableList<MetroData>) : DailyState()
    data class LoadingMoreState(val videoList: MutableList<MetroData>) : DailyState()
    data class ErrorState(val errorMsg: String) : DailyState()
}