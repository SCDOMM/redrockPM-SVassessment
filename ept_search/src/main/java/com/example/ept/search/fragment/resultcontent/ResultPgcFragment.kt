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
import com.example.ept.search.adapter.resultcontent.ResultPgcAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.PgcState
import com.example.ept.search.viewmodel.resultcontent.ResultPgcViewModel

class ResultPgcFragment : Fragment() {

    private lateinit var rvPgcDefault: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultPgcAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var pgcViewModel: ResultPgcViewModel
    private var isLoading = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_pgc, container, false)
        rvPgcDefault = view.findViewById(R.id.rv_pgc_default)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        pgcViewModel = ViewModelProvider(this)[ResultPgcViewModel::class.java]
        initEvent()
        return view
    }

    fun initEvent() {
        adapter = ResultPgcAdapter()
        rvPgcDefault.layoutManager = LinearLayoutManager(view.context)
        rvPgcDefault.adapter = adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner) { resultData ->
            pgcViewModel.initLiveData(resultData.pgcList.toMutableList(), resultData.query)
        }
        pgcViewModel.liveData.observe(viewLifecycleOwner) { data ->
            when (data) {
                is PgcState.InitState -> {
                    adapter.submitList(data.pgcList)
                }

                is PgcState.LoadingMoreState -> {
                    isLoading = false
                    adapter.submitList(data.newPgcList)
                }

                is PgcState.RefreshState -> {

                }

                is PgcState.ErrorState -> {
                    Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        rvPgcDefault.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    pgcViewModel.loadMore()
                }
            }
        })


    }


}