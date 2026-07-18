package com.example.ept.search.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**   
 * 包名称： com.example.ept.search.viewModel
 * 类名称：SearchViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 19:07
 *
 */
class SearchViewModel(application: Application): AndroidViewModel(application) {
    private var _liveData= MutableLiveData<SearchState>()
    val liveData: LiveData<SearchState> get() =_liveData




}
sealed class SearchState(){

}