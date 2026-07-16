package com.example.ept.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.videoEntity.VideoData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

/**
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeViewModel (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<VideoData>()
    val liveData: LiveData<VideoData> get() = _liveData
    private lateinit var dataList: MutableList<VideoData>
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    init {
        refreshLiveData()
    }
    fun refreshLiveData(){
        viewModelScope.launch {


        }
    }
    suspend fun request(): String{
        return suspendCoroutine {continuation ->
            appService.getTabList().


        }
    }







}