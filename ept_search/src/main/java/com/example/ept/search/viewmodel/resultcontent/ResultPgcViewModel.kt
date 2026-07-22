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
 * 类名称：ResultPgcFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultPgcViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<PgcState>()
    val liveData: LiveData<PgcState> get() = _liveData

    private var lastItemId=2
    private var allPgc: List<MetroData> =emptyList()
    private var query=""
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    fun initLiveData(allPgc: MutableList<MetroData>, query: String){
        this.allPgc=allPgc
        this.query=query
        _liveData.value= PgcState.InitState(allPgc)
    }
    fun loadMore(){
        viewModelScope.launch {
            try {
                val response = appService.searchLoad(query, "pgc", lastItemId, 10).await()
                val loadResult = parseSearchResponseV2(response)
                lastItemId = response.result?.last_item_id ?: 0
                allPgc=allPgc+loadResult.pgcList
                _liveData.value= PgcState.LoadingMoreState(allPgc.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= PgcState.ErrorState(e.message.toString())
            }
        }
    }
}
sealed class PgcState{
    data class InitState(val pgcList: MutableList<MetroData>): PgcState()
    data class RefreshState(val pgcList:MutableList<MetroData>): PgcState()
    data class LoadingMoreState( val newPgcList: MutableList<MetroData>): PgcState()
    data class ErrorState(val errorMsg: String): PgcState()
}