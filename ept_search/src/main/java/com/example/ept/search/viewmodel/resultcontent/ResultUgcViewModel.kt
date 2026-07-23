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
 * 类名称：ResultUgcViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:57
 *
 */
class ResultUgcViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<UgcState>()
    val liveData: LiveData<UgcState> get() = _liveData
    private var lastItemId="2"
    private var allUgc: List<MetroData> =emptyList()
    private lateinit var query: String
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allUgc: MutableList<MetroData>, query: String) {
        this.query = query
        this.allUgc = allUgc
        _liveData.value = UgcState.InitState(allUgc)
    }
    fun loadMore() {
        viewModelScope.launch {
            try {
                val response=appService.searchLoad(query,"ugc",lastItemId,10).await()
                val resultData= parseSearchResponseV2(response)
                allUgc=allUgc+resultData.ugcList
                lastItemId=response.result?.lastItemId?:"0"
                _liveData.value= UgcState.LoadingMoreState(allUgc.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= UgcState.ErrorState(e.message.toString())
            }
        }
    }

}
sealed class UgcState{
    data class InitState(val ugcData: MutableList<MetroData>) : UgcState()
    data class RefreshState(val ugcList:MutableList<MetroData>): UgcState()
    data class LoadingMoreState( val newUgcList: MutableList<MetroData>): UgcState()
    data class ErrorState(val errorMsg: String): UgcState()
}