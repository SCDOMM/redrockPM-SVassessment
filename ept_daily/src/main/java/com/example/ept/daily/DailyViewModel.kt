package com.example.ept.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.model.EyepetizerResponse
import com.example.core.model.FollowCardData
import com.example.core.model.SquareCardCollectionData
import com.example.core.model.VideoData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.core.network.await
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**   
 * 包名称： com.example.ept.daily
 * 类名称：DailyViewModel
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 21:11
 *
 */
class DailyViewModel(application: Application) : AndroidViewModel(application) {
    private var _liveData = MutableLiveData<DailyState>()
    val liveData: LiveData<DailyState> get() = _liveData

    private var allVideos: List<VideoData> = emptyList()

    private val appService: KaiyanApi by lazy {
        RetrofitClient.create()
    }
    private var nextPageUrl: String? = null

    init {
        refreshLiveData()
//        viewModelScope.launch(IO) {
//            try {
//                val client = OkHttpClient.Builder()
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//
//                val formBody = FormBody.Builder()
//                    .add("query", "艺术")
//                    .add("start","0")
//                    .add("num","20")
//                    .build()
//
//                val request = Request.Builder()
//                    .url("https://api.eyepetizer.net/v1/search/search/get_search_result_v2")
//                    .post(formBody)
//                    .addHeader("Cookie", "ky_udid=e53bcd5aa95847c0b0bd31c80b7fc102;ky_auth=APPIDahpagrcrf2p7m6rg;P95E551D-bdf22c23d2df47687d7bceef5c8bd00")
//                    .build()
//
//                val response = client.newCall(request).execute()
//                val body = response.body?.string()
//                Log.d("SEARCH_POST", "Response code: ${response.code}")
//                Log.d("SEARCH_POST", "Body: $body")
//            } catch (e: Exception) {
//                Log.e("SEARCH_POST", "Error", e)
//            }
//        }
    }

    fun refreshLiveData() {
        viewModelScope.launch {
            try {
                val response = appService.getTabDetail("feed", 0).await()
                nextPageUrl = response.nextPageUrl
                allVideos = parseVideoList(response)

                _liveData.postValue(DailyState.RefreshState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(DailyState.ErrorState(e.message.toString()))
            }
        }
    }

    fun loadingMore() {
        if (nextPageUrl == null) return
        viewModelScope.launch {
            try {
                val response = appService.getTabDetailByUrl(nextPageUrl!!).await()
                nextPageUrl = response.nextPageUrl
                val newVideos = parseVideoList(response)

                allVideos = allVideos + newVideos
                _liveData.postValue(DailyState.LoadingMoreState(allVideos.toMutableList()))
            } catch (e: Exception) {
                e.printStackTrace()
                _liveData.postValue(DailyState.ErrorState(e.message.toString()))
            }
        }
    }

    private fun parseVideoList(response: EyepetizerResponse): List<VideoData> {
        return response.itemList.flatMap { item ->
            when (item.type) {
                "video", "videoSmallCard" -> {
                    listOfNotNull(item.data as? VideoData)
                }
                "followCard" -> {
                    val fc = item.data as? FollowCardData
                    listOfNotNull(fc?.content?.data)
                }
                "squareCardCollection" -> {
                    val col = item.data as? SquareCardCollectionData
                    col?.itemList?.mapNotNull { child ->
                        if (child.type == "followCard") {
                            (child.data as? FollowCardData)?.content?.data
                        } else null
                    } ?: emptyList()
                }
                else -> emptyList()
            }
        }
    }
}

sealed class DailyState {
    data class RefreshState(val videoList: MutableList<VideoData>) : DailyState()
    data class LoadingMoreState(val videoList: MutableList<VideoData>) : DailyState()
    data class ErrorState(val errorMsg: String) : DailyState()
}