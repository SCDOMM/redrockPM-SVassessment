package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.NavTab
import com.example.core.model.official.WorkMetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.person.utils.parseWorkDataList
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

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(workTab: NavTab) {
        viewModelScope.launch {
            try {
                val response = appService.getWorkPage(workTab.page_label, workTab.page_type).await()
                val metroList = parseWorkDataList(response)
                _liveData.value = WorkState.InitState(metroList.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = WorkState.FailedState(e.message.toString())
            }
        }
    }

    fun loadMore() {


    }


}

sealed class WorkState() {
    data class InitState(val workList: MutableList<WorkMetroData>) : WorkState()
    data class RefreshState(val workList: MutableList<WorkMetroData>) : WorkState()
    data class LoadingState(val newWorkList: MutableList<WorkMetroData>) : WorkState()
    data class FailedState(val msg: String) : WorkState()
}