package com.example.ept.search.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroData
import com.example.core.model.SearchResult
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 18:53
 *
 */
class ResultViewModel(application: Application) : AndroidViewModel(application){
    private var _liveData = MutableLiveData<ResultState>()
    val liveData: LiveData<ResultState> get() = _liveData
    private var nextPageUrl: String? = null
    private var allResult: List<MetroData> =emptyList()

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    fun search(query:String,start:Int=0,num:Int=20){
        viewModelScope.launch {
            try {
                val response=appService.search(query,start,num).await()
                if (response.result!=null) _liveData.postValue(ResultState.SuccessState(response.result!!))
            }catch (e: Exception){
                e.printStackTrace()
                _liveData.postValue(ResultState.FailedState(e.message.toString()))
            }
        }
    }
}
sealed class ResultState(){
    data class SuccessState(val searchResult: SearchResult): ResultState()
    data class FailedState(val errorMsg: String): ResultState()
}