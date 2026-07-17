package com.example.ept.search.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.core.model.VideoData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultCreatorsFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultCreatorsViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<CreatorsState>()
    val liveData: LiveData<CreatorsState> get() = _liveData
    private var nextPageUrl: String? = null
    private var allCreators: List<> =emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }



}
sealed class CreatorsState{
    data class RefreshState(val videoList:MutableList<VideoData>): CreatorsState()
    data class LoadingMoreState( val newVideoList: MutableList<VideoData>): CreatorsState()
    data class ErrorState(val errorMsg: String): CreatorsState()
}