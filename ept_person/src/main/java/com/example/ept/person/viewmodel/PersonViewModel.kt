package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.NavTab
import com.example.core.model.UserInfo
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import com.example.core.network.await
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.person.viewmodel
 * 类名称：CreatorViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 14:52
 *
 */
class CreatorViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<CreatorState>()
    val liveData: LiveData<CreatorState> get() = _liveData

    private var _indexLiveData = MutableLiveData<NavTab>()
    val indexLiveData: LiveData<NavTab> get() = _indexLiveData

    private var _workLiveData = MutableLiveData<NavTab>()
    val workLiveData: LiveData<NavTab> get() = _workLiveData

    private var _albumLiveData = MutableLiveData<NavTab>()
    val albumLiveData: LiveData<NavTab> get() = _albumLiveData


    private val appService: UniversalApi by lazy {
        RetrofitClient.create()
    }

    fun initCreator(uid: String) {
        viewModelScope.launch {
            try {
                val response = appService.getUserInfo(uid).await()
                val result = response.result
                _liveData.value = result?.let { CreatorState.InitState(it,
                    result.navTabs?.navList?.size ?: 0
                ) }

                val indexTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "index"
                }
                if (indexTab != null) {
                    _indexLiveData.value = indexTab
                }

                val workTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "work"
                }
                if (workTab != null) {
                    _workLiveData.value = workTab
                }

                val albumTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "album"
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
    fun initRefresh(uid: String) {
        viewModelScope.launch {
            try {
                val response = appService.getUserInfo(uid).await()
                val result = response.result
                _liveData.value = result?.let { CreatorState.RefreshState(it) }

                val indexTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "index"
                }
                if (indexTab != null) {
                    _indexLiveData.value = indexTab
                }

                val workTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "work"
                }
                if (workTab != null) {
                    _workLiveData.value = workTab
                }

                val albumTab = result?.navTabs?.navList?.firstOrNull {
                    it.iconType == "album"
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
