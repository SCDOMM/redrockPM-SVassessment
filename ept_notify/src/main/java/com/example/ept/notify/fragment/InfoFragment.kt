package com.example.ept.notify.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ept.notify.R
import com.example.ept.notify.adapter.InfoAdapter
import com.example.ept.notify.viewmodel.InfoState
import com.example.ept.notify.viewmodel.InfoViewModel


class InfoFragment : Fragment() {
    private lateinit var view: View
    private lateinit var viewModel: InfoViewModel
    private lateinit var srlInfoDefault: SwipeRefreshLayout
    private lateinit var rvInfoDefault: RecyclerView
    private lateinit var adapter: InfoAdapter
    private var isLoading=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_info, container, false)
        viewModel= ViewModelProvider(this)[InfoViewModel::class.java]
        srlInfoDefault=view.findViewById(R.id.srl_info_default)
        rvInfoDefault=view.findViewById(R.id.rv_info_default)
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= InfoAdapter()
        rvInfoDefault.layoutManager= LinearLayoutManager(view.context)
        rvInfoDefault.adapter=adapter
        viewModel.liveData.observe(viewLifecycleOwner){data->
            when(data){
                is InfoState.ErrorState -> Toast.makeText(view.context,data.toString(),Toast.LENGTH_SHORT).show()
                is InfoState.InitState -> {
                    adapter.submitList(data.infoList)
                }
                is InfoState.LoadingState -> {
                    isLoading=false
                    adapter.submitList(data.infoList)
                }
                is InfoState.RefreshState -> {

                }
            }
        }


        rvInfoDefault.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoading) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalCount = layoutManager.itemCount
                if (totalCount <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val preloadThreshold = 6
                val remainingItems = totalCount - lastVisible - 1
                if (remainingItems <= preloadThreshold) {
                    isLoading = true
                    viewModel.loadMore()
                }
            }
        })
    }
}