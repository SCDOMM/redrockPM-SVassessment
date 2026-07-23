package com.example.ept.person.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.AlbumData
import com.example.core.model.NavTab
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import com.example.core.network.await
import com.example.core.common.parseAlbumCards
import kotlinx.coroutines.launch

/**
 * 包名称： com.example.ept.person.viewmodel
 * 类名称：AlbumViewModel
 * 类描述：专辑的VM
 * 创建人：韦西波
 * 创建时间：2026-07-21 16:24
 *
 */
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<AlbumState>()
    val liveData: LiveData<AlbumState> get() = _liveData

    private var allAlbums: List<AlbumData> = emptyList()
    private var lastItemId= "0"
    private var cardJSON: String = ""
    private var pageLabel: String = ""
    private val appService: UniversalApi by lazy {
        RetrofitClient.create()
    }
    fun initLiveData(albumTab: NavTab) {
        viewModelScope.launch {
            pageLabel = albumTab.pageLabel
            try {
                val pageLabel = albumTab.pageLabel
                val pageType = albumTab.pageType
                val response = appService.getPage(pageLabel, pageType).await()
                val cardList = response.result?.cardList!!
                allAlbums = parseAlbumCards(cardList)
                val callCard = cardList.firstOrNull { it.type == "call_card_list" }
                val params = callCard?.cardData?.body?.apiRequest?.params
                lastItemId = (params?.get("last_item_id")?: "0").toString()
                cardJSON = (params?.get("card_list") as? String) ?: ""
                _liveData.value = AlbumState.InitState(allAlbums.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = AlbumState.FailedState(e.message.toString())
            }
        }
    }
    fun loadMore() {
        viewModelScope.launch {
            try {
                val cardListJson = cardJSON
                val response = appService.loadMoreAlbum(
                    lastItemId = lastItemId,
                    cardList = cardListJson,
                    pageLabel = pageLabel,
                    version = 1
                ).await()
                val newCards = response.result?.itemList ?: emptyList()
                if (lastItemId=="0"){
                    _liveData.value = AlbumState.LoadingState(allAlbums.toMutableList())
                    return@launch
                }
                lastItemId = response.result?.lastItemId ?: "0"

                val newAlbums = parseAlbumCards(newCards)
                allAlbums = allAlbums + newAlbums
                _liveData.value = AlbumState.LoadingState(allAlbums.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = AlbumState.FailedState(e.message.toString())
            }
        }
    }
}

sealed class AlbumState {
    data class InitState(val albumList: MutableList<AlbumData>) : AlbumState()
    data class RefreshState(val albumData: MutableList<AlbumData>) : AlbumState()
    data class LoadingState(val newAlbumData: MutableList<AlbumData>) : AlbumState()
    data class FailedState(val msg: String) : AlbumState()
}