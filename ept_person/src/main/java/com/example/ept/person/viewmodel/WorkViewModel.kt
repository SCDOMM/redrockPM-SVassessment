package com.example.ept.person.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.MetroData
import com.example.core.model.official.NavTab
import com.example.core.model.official.WorkMetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.person.utils.parseWorkDataList
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.person.pgc.viewmodel
 * 类名称：WorkViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 21:34
 *
 */
class WorkViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<WorkState>()
    val liveData: LiveData<WorkState> get() = _liveData
    private lateinit var cardJSON: String
    private lateinit var materialJson: String
    private var lastItemId=""
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    private var allWorks:List<WorkMetroData> = emptyList()

    fun initLiveData(workTab: NavTab) {
        viewModelScope.launch {
            try {
                val response = appService.getWorkPage(workTab.page_label, workTab.page_type).await()
                val metroList = parseWorkDataList(response)
                allWorks = metroList
                val firstCard = response.result?.cardList?.firstOrNull { it.type == "call_metro_list" }
                val params = firstCard?.cardData?.body?.apiRequest?.params

                lastItemId = params?.get("last_item_id") as? String ?: ""
                Log.d("测试1",lastItemId)
                materialJson = params?.get("material") as? String ?: ""
                cardJSON = Gson().toJson(firstCard)
                _liveData.value = WorkState.InitState(metroList.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = WorkState.FailedState(e.message.toString())
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val response=appService.loadMoreWork(
                cardIndex = 2,
                material = materialJson,
                materialIndex = 12,
                lastItemId = lastItemId,
                pageLabel = "user_center_work",
                card = cardJSON
            ).await()

            val newItems = response.result?.itemList
            if (lastItemId==""){
                _liveData.value = WorkState.LoadingState(allWorks.toMutableList())
                return@launch
            }
            lastItemId = response.result?.lastItemId?:""

            Log.d("测试",lastItemId)

            val workDataList = newItems?.mapNotNull { it.metroData}
            if (!workDataList.isNullOrEmpty()) allWorks=allWorks+workDataList
            _liveData.value = WorkState.LoadingState(allWorks.toMutableList())
        }
    }


}

sealed class WorkState{
    data class InitState(val workList: MutableList<WorkMetroData>) : WorkState()
    data class RefreshState(val workList: MutableList<WorkMetroData>) : WorkState()
    data class LoadingState(val newWorkList: MutableList<WorkMetroData>) : WorkState()
    data class FailedState(val msg: String) : WorkState()
}
