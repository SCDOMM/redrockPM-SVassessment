package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.NavTab
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.person.utils.UserHomeItem
import com.example.ept.person.utils.parseCardForAdapter
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.person.pgc.viewmodel
 * 类名称：MainPageViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-22 11:57
 *
 */
class MainPageViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<MainPageState>()
    val liveData: LiveData<MainPageState> get() = _liveData

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(indexTab: NavTab) {
        viewModelScope.launch {
            try {
                val response=appService.getWorkPage(indexTab.page_label,indexTab.page_type).await()
                val indexList= parseCardForAdapter(response)
                _liveData.value= MainPageState.InitState(indexList.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value= MainPageState.FailedState(e.message.toString())
            }

        }
    }


}

sealed class MainPageState() {
    data class InitState(val indexList: MutableList<UserHomeItem> ) : MainPageState()
    data class RefreshState(val indexList: MutableList<UserHomeItem> ) : MainPageState()
    data class FailedState(val msg: String) : MainPageState()
}