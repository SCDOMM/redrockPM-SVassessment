package com.example.ept.person.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.person.R
import com.example.ept.person.adapter.content.WorkAdapter
import com.example.ept.person.viewmodel.CreatorViewModel
import com.example.ept.person.viewmodel.WorkState
import com.example.ept.person.viewmodel.WorkViewModel

class WorkFragment : Fragment() {
    private lateinit var view: View
    private lateinit var rvWorkDefault: RecyclerView
    private lateinit var tvWorkTitle: TextView
    private lateinit var workViewModel: WorkViewModel
    private lateinit var creatorViewModel: CreatorViewModel
    private lateinit var adapter: WorkAdapter
    private var isLoading=false

    // ★ 新增：供 Activity 获取 RecyclerView
    fun getRecyclerView(): RecyclerView = rvWorkDefault

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_work, container, false)
        workViewModel= ViewModelProvider(this)[WorkViewModel::class.java]
        creatorViewModel= ViewModelProvider(requireActivity())[CreatorViewModel::class.java]
        rvWorkDefault=view.findViewById(R.id.rv_work_default)
        tvWorkTitle=view.findViewById(R.id.tv_work_title)
        initEvent()
        initAdd()
        return view
    }
    fun initEvent(){
        adapter= WorkAdapter()
        rvWorkDefault.layoutManager= LinearLayoutManager(view.context)
        rvWorkDefault.adapter=adapter
        creatorViewModel.workLiveData.observe(viewLifecycleOwner){tab->
            workViewModel.initLiveData(tab)
        }
        workViewModel.liveData.observe(viewLifecycleOwner){state ->
            when(state){
                is WorkState.FailedState -> Toast.makeText(view.context,state.msg,Toast.LENGTH_SHORT).show()
                is WorkState.InitState ->{
                    tvWorkTitle.text=state.title
                    if (state.title=="null"){
                        tvWorkTitle.text="暂无作品"
                    }
                    adapter.submitList(state.workList)}
                is WorkState.LoadingState -> {
                    isLoading=false
                    adapter.submitList(state.newWorkList)
                }
                is WorkState.RefreshState -> {


                }
            }
        }
    }
    fun initAdd(){
        rvWorkDefault.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    workViewModel.loadMore()
                }
            }
        })
    }
}