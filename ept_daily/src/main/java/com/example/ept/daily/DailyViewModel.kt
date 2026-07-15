package com.example.ept.daily

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.videoEntity.VideoData
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.daily
 * 类名称：DailyViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 21:11
 *
 */
class DailyViewModel (application: Application) : AndroidViewModel(application)  {
    private var _liveData = MutableLiveData<VideoData>()
    val liveData: LiveData<VideoData> get() = _liveData
    private lateinit var dataList: MutableList<VideoData>
    init {
        refreshLiveData()
    }
    fun refreshLiveData(){
        viewModelScope.launch {


        }
    }

}