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
import com.example.ept.search.adapter.resultcontent.ResultUgcAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.ResultUgcViewModel
import com.example.ept.search.viewmodel.resultcontent.UgcState

class ResultUgcFragment : Fragment() {
    private lateinit var rvResultUgc: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultUgcAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var ugcViewModel: ResultUgcViewModel
    private var isLoading=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_ugc, container, false)
        rvResultUgc=view.findViewById(R.id.rv_ugc_default)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        ugcViewModel= ViewModelProvider(this)[ResultUgcViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= ResultUgcAdapter()
        rvResultUgc.layoutManager= LinearLayoutManager(view.context)
        rvResultUgc.adapter=adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            ugcViewModel.initLiveData(resultData.ugcList.toMutableList(),resultData.query)
        }
        ugcViewModel.liveData.observe(viewLifecycleOwner){ data->
            when(data){
                is UgcState.ErrorState -> Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                is UgcState.InitState ->adapter.submitList(data.ugcData)
                is UgcState.LoadingMoreState ->{
                    isLoading=false
                    adapter.submitList(data.newUgcList)
                }
                is UgcState.RefreshState -> {}
            }
        }
        rvResultUgc.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    ugcViewModel.loadMore()
                }
            }
        })

    }

}