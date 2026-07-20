package com.example.ept.search.viewmodel.resultcontent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.MetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import com.example.ept.search.utils.parseSearchResponseV2
import kotlinx.coroutines.launch

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：ResultArticlesViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-17 21:56
 *
 */
class ResultArticlesViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<ArticlesState>()
    val liveData: LiveData<ArticlesState> get() = _liveData
    private var allArticles: List<MetroData> = emptyList()
    private var lastItemId = 2
    private var query = ""
    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }

    fun initLiveData(allArticles: MutableList<MetroData>, query: String) {
        this.allArticles = allArticles
        this.query = query
        _liveData.value = ArticlesState.InitState(allArticles)
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                val response = appService.searchLoad(query, "graphic", lastItemId, 10).await()
                val loadResult = parseSearchResponseV2(response)
                lastItemId = response.result?.last_item_id ?: 0
                allArticles = allArticles + loadResult.articleList
                _liveData.value = ArticlesState.LoadingMoreState(allArticles.toMutableList())
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.value = ArticlesState.ErrorState(e.message.toString())
            }
        }
    }
}

sealed class ArticlesState {
    data class InitState(val articleList: MutableList<MetroData>) : ArticlesState()
    data class RefreshState(val articleList: MutableList<MetroData>) : ArticlesState()
    data class LoadingMoreState(val newArticleList: MutableList<MetroData>) : ArticlesState()
    data class ErrorState(val errorMsg: String) : ArticlesState()
}