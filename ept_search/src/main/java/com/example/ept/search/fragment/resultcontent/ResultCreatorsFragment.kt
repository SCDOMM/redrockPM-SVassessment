package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultCreatorsAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.CreatorsState
import com.example.ept.search.viewmodel.resultcontent.ResultCreatorsViewModel

class ResultCreatorsFragment : Fragment() {

    private lateinit var rvResultCreators: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultCreatorsAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var viewModel: ResultCreatorsViewModel
    private var isLoading = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_creators, container, false)
        rvResultCreators = view.findViewById(R.id.rv_result_creators)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel = ViewModelProvider(this)[ResultCreatorsViewModel::class.java]
        initEvent()
        return view
    }

    fun initEvent() {
        adapter = ResultCreatorsAdapter()
        rvResultCreators.layoutManager = LinearLayoutManager(view.context)
        rvResultCreators.adapter = adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner) { resultData ->
            viewModel.initLiveData(resultData.creatorList.toMutableList(), resultData.query)
        }
        viewModel.liveData.observe(viewLifecycleOwner) { data ->
            when (data) {
                is CreatorsState.InitState -> {
                    adapter.submitList(data.creatorList)
                }

                is CreatorsState.LoadingMoreState -> {
                    isLoading = false
                    adapter.submitList(data.newCreatorList)
                }

                is CreatorsState.RefreshState -> {

                }

                is CreatorsState.ErrorState -> {
                    Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        rvResultCreators.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    viewModel.loadMore()
                }
            }
        })


    }


}