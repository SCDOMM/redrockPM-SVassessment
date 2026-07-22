package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.NavTab
import com.example.core.model.official.UserInfo
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.person.pgc.viewmodel
 * 类名称：CreatorViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 14:52
 *
 */
class CreatorViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<CreatorState>()
    val liveData: LiveData<CreatorState> get() = _liveData

    private var _mainPageLiveData = MutableLiveData<NavTab>()
    val mainPageLiveData: LiveData<NavTab> get() = _mainPageLiveData

    private var _workLiveData = MutableLiveData<NavTab>()
    val workLiveData: LiveData<NavTab> get() = _workLiveData

    private var _albumLiveData = MutableLiveData<NavTab>()
    val albumLiveData: LiveData<NavTab> get() = _albumLiveData


    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initCreator(uid: Long) {
        viewModelScope.launch {
            try {
                val response = appService.getUserInfo(uid).await()
                val result = response.result
                _liveData.value = result?.let { CreatorState.InitState(it,
                    result.nav_tabs?.nav_list?.size ?: 0
                ) }

                val mainPageTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "index"
                }
                if (mainPageTab != null) {
                    _mainPageLiveData.value = mainPageTab
                }

                val workTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "work"
                }
                if (workTab != null) {
                    _workLiveData.value = workTab
                }

                val albumTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "album"
                }
                if (albumTab!=null) {
                    _albumLiveData.value = albumTab
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = CreatorState.FailedState(e.message.toString())
            }
        }
    }
    fun initRefresh(uid: Long) {
        viewModelScope.launch {
            try {
                val response = appService.getUserInfo(uid).await()
                val result = response.result
                _liveData.value = result?.let { CreatorState.RefreshState(it) }

                val mainPageTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "index"
                }
                if (mainPageTab != null) {
                    _mainPageLiveData.value = mainPageTab
                }

                val workTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "work"
                }
                if (workTab != null) {
                    _workLiveData.value = workTab
                }

                val albumTab = result?.nav_tabs?.nav_list?.firstOrNull {
                    it.icon_type == "album"
                }
                if (albumTab!=null) {
                    _albumLiveData.value = albumTab
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = CreatorState.FailedState(e.message.toString())
            }
        }
    }

}

sealed class CreatorState {
    data class InitState(val userInfo: UserInfo,val length: Int) : CreatorState()
    data class RefreshState(val userInfo: UserInfo) : CreatorState()
    data class FailedState(val msg: String) : CreatorState()
}
