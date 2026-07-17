package com.example.ept.search.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultTopicsFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultTopicsViewModel  (application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<TopicsState>()
    val liveData: LiveData<TopicsState> get() = _liveData
    private var nextPageUrl: String? = null
    private var allTopics: List<> =emptyList()
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }




}
sealed class TopicsState{
    data class RefreshState(val videoList:MutableList<>): TopicsState()
    data class LoadingMoreState( val newVideoList: MutableList<>): TopicsState()
    data class ErrorState(val errorMsg: String): TopicsState()
}