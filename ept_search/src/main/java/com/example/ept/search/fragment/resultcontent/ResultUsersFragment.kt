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
import com.example.ept.search.adapter.resultcontent.ResultUsersAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.ResultUsersViewModel
import com.example.ept.search.viewmodel.resultcontent.UsersState

class ResultUsersFragment : Fragment() {
    private lateinit var rvResultUsers: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultUsersAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var viewModel: ResultUsersViewModel
    private var isLoading=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_users, container, false)
        rvResultUsers=view.findViewById(R.id.rv_result_users)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel= ViewModelProvider(this)[ResultUsersViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= ResultUsersAdapter()
        rvResultUsers.layoutManager= LinearLayoutManager(view.context)
        rvResultUsers.adapter=adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            viewModel.initLiveData(resultData.userList.toMutableList(),resultData.query)
        }
        viewModel.liveData.observe(viewLifecycleOwner){data->
            when(data){
                is UsersState.ErrorState -> Toast.makeText(view.context,data.errorMsg, Toast.LENGTH_SHORT).show()
                is UsersState.InitState ->adapter.submitList(data.userData)
                is UsersState.LoadingMoreState ->{
                    isLoading=false
                    adapter.submitList(data.newUserList)
                }
                is UsersState.RefreshState -> {}
            }
        }
        rvResultUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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