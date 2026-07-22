package com.example.ept.person.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.AlbumData
import com.example.core.model.official.NavTab
import com.example.core.model.official.SearchCard
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

    private var allAlbums: List<AlbumData> = emptyList()
    private var lastItemId: Int = 0
    private var cardJSON: String = ""
    private var pageLabel: String = ""
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    fun initLiveData(albumTab: NavTab) {
        viewModelScope.launch {
            pageLabel = albumTab.page_label
            try {
                val pageLabel = albumTab.page_label
                val pageType = albumTab.page_type
                val response = appService.getPage(pageLabel, pageType).await()
                val cardList = response.result?.card_list!!
                allAlbums = parseAlbumCards(cardList)
                val callCard = cardList.firstOrNull { it.type == "call_card_list" }
                val params = callCard?.card_data?.body?.api_request?.params
                lastItemId = (params?.get("last_item_id") as? Number)?.toInt() ?: 0
                // 提取固定卡片模板字符串
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
                // 直接使用固定的模板字符串，不再序列化 currentAlbumCards
                val cardListJson = cardJSON
                val response = appService.loadMoreAlbum(
                    lastItemId = lastItemId,
                    cardList = cardListJson,
                    pageLabel = pageLabel,
                    version = 1
                ).await()
                val newCards = response.result?.itemList ?: emptyList()
                if (lastItemId==0){
                    _liveData.value = AlbumState.LoadingState(allAlbums.toMutableList())
                    return@launch
                }
                lastItemId = response.result?.lastItemId ?: 0

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

sealed class AlbumState() {
    data class InitState(val albumList: MutableList<AlbumData>) : AlbumState()
    data class RefreshState(val albumData: MutableList<AlbumData>) : AlbumState()
    data class LoadingState(val newAlbumData: MutableList<AlbumData>) : AlbumState()
    data class FailedState(val msg: String) : AlbumState()
}