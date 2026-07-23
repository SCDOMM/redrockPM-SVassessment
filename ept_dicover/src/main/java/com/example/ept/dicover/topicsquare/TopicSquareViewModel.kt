package com.example.ept.dicover.topicsquare

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.TabItem
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题广场列表页 ViewModel
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareViewModel : ViewModel() {

    private val api = RetrofitClient.create<KaiyanApi>()

    /** 是否已加载过 Tab 数据 */
    var loaded = false
        private set

    /** Tab 列表数据（对外只读） */
    private val _tabs = MutableLiveData<List<TabItem>>()
    val tabs: LiveData<List<TabItem>> = _tabs

    /** 错误信息（对外只读） */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 从 API 加载话题广场的 Tab 导航数据 */
    fun loadTabs() {
        loaded = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getNav(tabLabel = "topic_square").execute()
                }
                val body = response.body()
                if (body?.code != 0) {
                    _error.value = "加载失败: code=${body?.code}"
                    return@launch
                }

                val navResult = body?.result
                if (navResult == null) {
                    _error.value = "数据为空"
                    return@launch
                }

                val tabItems = navResult.nav_list.map { nav ->
                    TabItem(
                        id = 0,
                        name = nav.title,
                        apiUrl = nav.page_label
                    )
                }

                _tabs.value = tabItems
                _error.value = null
                Log.d("TopicSquareVM", "Loaded ${tabItems.size} tabs")
            } catch (e: Exception) {
                Log.e("TopicSquareVM", "loadTabs failed", e)
                _error.value = e.message
            }
        }
    }
}
