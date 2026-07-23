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
import com.example.ept.search.adapter.resultcontent.ResultGraphicsAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.GraphicsState
import com.example.ept.search.viewmodel.resultcontent.ResultGraphicsViewModel

class ResultGraphicsFragment : Fragment() {
    private lateinit var rvGraphicsDefault: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultGraphicsAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var graphicsViewModel: ResultGraphicsViewModel
    private var isLoading=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_graphics, container, false)
        rvGraphicsDefault=view.findViewById(R.id.rv_graphics_default)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        graphicsViewModel= ViewModelProvider(this)[ResultGraphicsViewModel::class.java]
        initEvent()
        initAdd()
        return view
    }
    fun initEvent(){
        adapter= ResultGraphicsAdapter()
        rvGraphicsDefault.layoutManager = LinearLayoutManager(requireContext())
        rvGraphicsDefault.adapter=adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            graphicsViewModel.initLiveData(resultData.graphicList.toMutableList(),resultData.query)
        }
        graphicsViewModel.liveData.observe(viewLifecycleOwner){ data ->
           when(data){
               is GraphicsState.InitState ->{
                   adapter.submitList(data.graphicList)
               }
               is GraphicsState.LoadingState->{
                   isLoading=false
                   adapter.submitList(data.newGraphicList)
               }
               is GraphicsState.RefreshState->{

               }
               is GraphicsState.ErrorState->{
                   Toast.makeText(
                       requireContext(),
                       "错误！" + data.errorMsg,
                       Toast.LENGTH_SHORT
                   ).show()
               }
           }
        }

    }
    fun initAdd(){
        rvGraphicsDefault.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    graphicsViewModel.loadMore()
                }
            }
        })
    }
}