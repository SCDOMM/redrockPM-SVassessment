package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.AlbumData
import com.example.core.model.official.NavTab
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.person.utils.parseAlbumCards
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.person.pgc.viewmodel
 * 类名称：AlbumViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 16:24
 *
 */
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<AlbumState>()
    val liveData: LiveData<AlbumState> get() = _liveData

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(albumTab: NavTab) {
        viewModelScope.launch {
            try {
                val pageLabel = albumTab.page_label
                val pageType = albumTab.page_type
                val response = appService.getPage(pageLabel, pageType).await()
                val cardList = response.result?.card_list!!
                val albumList = parseAlbumCards(cardList)
                _liveData.value = AlbumState.InitState(albumList.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = AlbumState.FailedState(e.message.toString())
            }
        }
    }
    fun loadMore(){




    }
}


sealed class AlbumState() {
    data class InitState(val albumList: MutableList<AlbumData>) : AlbumState()
    data class RefreshState(val albumData: MutableList<AlbumData>) : AlbumState()
    data class LoadingState(val newAlbumData: MutableList<AlbumData>) : AlbumState()
    data class FailedState(val msg: String) : AlbumState()
}