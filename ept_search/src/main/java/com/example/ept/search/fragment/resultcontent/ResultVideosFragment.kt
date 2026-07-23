package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultVideosAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.ResultVideosViewModel
import com.example.ept.search.viewmodel.resultcontent.VideosState
import kotlin.jvm.java

class ResultVideosFragment : Fragment() {
    private lateinit var rvResultVideos: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultVideosAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var videosViewModel: ResultVideosViewModel
    private var isLoading=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_videos, container, false)
        rvResultVideos=view.findViewById(R.id.rv_videos_default)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        videosViewModel= ViewModelProvider(this)[ResultVideosViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= ResultVideosAdapter()
        rvResultVideos.layoutManager = LinearLayoutManager(view.context)
        rvResultVideos.adapter=adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            videosViewModel.initLiveData(resultData.videoList.toMutableList(),resultData.query)
        }
        videosViewModel.liveData.observe(viewLifecycleOwner){ data->
            when(data){
                is VideosState.ErrorState -> Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                is VideosState.InitState ->adapter.submitList(data.videoList)
                is VideosState.LoadingState ->{
                    isLoading=false
                    adapter.submitList(data.newVideoList)
                }
                is VideosState.RefreshState -> {}
            }
        }
        rvResultVideos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoading) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalCount = layoutManager.itemCount
                if (totalCount <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val preloadThreshold = 5
                val remainingItems = totalCount - lastVisible - 1
                if (remainingItems <= preloadThreshold) {
                    isLoading = true
                    videosViewModel.loadMore()
                }
            }
        })

    }
}