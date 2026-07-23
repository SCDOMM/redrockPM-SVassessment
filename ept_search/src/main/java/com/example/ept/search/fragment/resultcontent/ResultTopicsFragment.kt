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
import com.example.ept.search.adapter.resultcontent.ResultTopicsAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.ResultTopicsViewModel
import com.example.ept.search.viewmodel.resultcontent.TopicsState


class ResultTopicsFragment : Fragment() {
    private lateinit var rvResultTopics: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultTopicsAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var topicsViewModel: ResultTopicsViewModel
    private var isLoading=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view= inflater.inflate(R.layout.fragment_topics, container, false)
        rvResultTopics=view.findViewById(R.id.rv_topics_default)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        topicsViewModel= ViewModelProvider(this)[ResultTopicsViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= ResultTopicsAdapter()
        rvResultTopics.layoutManager= LinearLayoutManager(view.context)
        rvResultTopics.adapter=adapter

        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            topicsViewModel.initLiveData(resultData.topicList.toMutableList(),resultData.query)
        }
        topicsViewModel.liveData.observe(viewLifecycleOwner){ data->
            when(data){
                is TopicsState.ErrorState -> Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                is TopicsState.InitState ->adapter.submitList(data.topicData)
                is TopicsState.LoadingState ->{
                    isLoading=false
                    adapter.submitList(data.newTopicList)
                }
                is TopicsState.RefreshState -> {}
            }
        }
        rvResultTopics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    topicsViewModel.loadMore()
                }
            }
        })

    }

}