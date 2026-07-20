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
 * 类名称：ResultUsersViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:57
 *
 */
class ResultUsersViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<UsersState>()
    val liveData: LiveData<UsersState> get() = _liveData
    private var lastItemId=2
    private var allUsers: List<MetroData> =emptyList()
    private lateinit var query: String
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allUsers: MutableList<MetroData>, query: String) {
        this.query = query
        this.allUsers = allUsers
        _liveData.value = UsersState.InitState(allUsers)
    }
    fun loadMore() {
        viewModelScope.launch {
            try {
                val response=appService.searchLoad(query,"ugc",lastItemId,10).await()
                val resultData= parseSearchResponseV2(response)
                allUsers=allUsers+resultData.userList
                lastItemId=response.result?.last_item_id?:0
                _liveData.value= UsersState.LoadingMoreState(allUsers.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= UsersState.ErrorState(e.message.toString())
            }
        }
    }

}
sealed class UsersState{
    data class InitState(val userData: MutableList<MetroData>) : UsersState()
    data class RefreshState(val userList:MutableList<MetroData>): UsersState()
    data class LoadingMoreState( val newUserList: MutableList<MetroData>): UsersState()
    data class ErrorState(val errorMsg: String): UsersState()
}