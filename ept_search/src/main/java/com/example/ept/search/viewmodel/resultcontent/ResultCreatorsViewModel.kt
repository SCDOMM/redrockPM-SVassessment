package com.example.ept.search.viewmodel.resultcontent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.MetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.search.utils.parseSearchResponseV2
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultCreatorsFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultCreatorsViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<CreatorsState>()
    val liveData: LiveData<CreatorsState> get() = _liveData

    private var lastItemId=2
    private var allCreators: List<MetroData> =emptyList()
    private var query=""
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    fun initLiveData(allCreators: MutableList<MetroData>,query: String){
        this.allCreators=allCreators
        this.query=query
        _liveData.value= CreatorsState.InitState(allCreators)
    }
    fun loadMore(){
        viewModelScope.launch {
            try {
                val response = appService.searchLoad(query, "pgc", lastItemId, 10).await()
                val loadResult = parseSearchResponseV2(response)
                lastItemId = response.result?.last_item_id ?: 0
                allCreators=allCreators+loadResult.creatorList
                _liveData.value= CreatorsState.LoadingMoreState(allCreators.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= CreatorsState.ErrorState(e.message.toString())
            }
        }
    }
}
sealed class CreatorsState{
    data class InitState(val creatorList: MutableList<MetroData>): CreatorsState()
    data class RefreshState(val creatorList:MutableList<MetroData>): CreatorsState()
    data class LoadingMoreState( val newCreatorList: MutableList<MetroData>): CreatorsState()
    data class ErrorState(val errorMsg: String): CreatorsState()
}