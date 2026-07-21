package com.example.ept.notify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.official.NoticeItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**   
 * 包名称： com.example.ept.notify.viewmodel
 * 类名称：InfoViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 14:32
 *
 */
class InfoViewModel(application: Application): AndroidViewModel(application) {
    private var _liveData = MutableLiveData<InfoState>()
    val liveData: LiveData<InfoState> get() = _liveData
    private var lastItemId=2
    private var allMessages: List<NoticeItem> =emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    init {
        initViewModel()
    }
    fun apiTest(){
        viewModelScope.launch(IO) {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("http://baobab.kaiyanapp.com/api/v3/messages")
                .get()
                .addHeader("Cookie", "ky_udid=58d1cf919db5480fbf33d4e306642a4e;ky_auth=;PHPSESSID=596a26afb8cc1f7d006c9cb72c79ee63;APPID=ahpagrcrf2p7m6rg")
                .build()
            client.newCall(request).execute().use { response ->
                println("Response Code: ${response.code}")
                println("Headers:")
                response.headers.forEach { (name, value) ->
                    println("  $name: $value")
                }
                println("\nBody:")
                val body = response.body?.string()
                println(body)
            }
        }
    }
    fun initViewModel(){
        viewModelScope.launch {
            try {
                val response=appService.getPushList(1).await()
                val infoList=response.result?.itemList?:emptyList()
                allMessages=infoList
                _liveData.value= InfoState.InitState(infoList.toMutableList())
            }catch (e: Exception){
                e.printStackTrace()
                _liveData.value= InfoState.ErrorState(e.message.toString())
            }
        }
    }

    fun loadMore(){
        viewModelScope.launch {
            try {
                val response=appService.getPushList(lastItemId).await()
                val infoList=response.result?.itemList?:emptyList()
                lastItemId= response.result?.lastItemId ?:0
                allMessages=allMessages+infoList
                _liveData.value = InfoState.LoadingState(allMessages.toMutableList())
            }catch (e: Exception){
                e.printStackTrace()
                _liveData.value= InfoState.ErrorState(e.message.toString())
            }
        }
    }
}
sealed class InfoState{
    data class InitState(val infoList: MutableList<NoticeItem>): InfoState()
    data class RefreshState(val infoList: MutableList<NoticeItem>): InfoState ()
    data class LoadingState(val infoList: MutableList<NoticeItem>): InfoState()
    data class ErrorState(val msg: String): InfoState()
}
