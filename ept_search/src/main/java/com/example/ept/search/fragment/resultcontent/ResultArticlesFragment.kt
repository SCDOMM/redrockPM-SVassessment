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
import com.example.ept.search.adapter.resultcontent.ResultArticlesAdapter
import com.example.ept.search.viewmodel.SearchViewModel
import com.example.ept.search.viewmodel.resultcontent.ArticlesState
import com.example.ept.search.viewmodel.resultcontent.ResultArticlesViewModel

class ResultArticlesFragment : Fragment() {
    private lateinit var rvResultArticles: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultArticlesAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var articlesViewModel: ResultArticlesViewModel
    private var isLoading=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_articles, container, false)
        rvResultArticles=view.findViewById(R.id.rv_result_articles)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        articlesViewModel= ViewModelProvider(this)[ResultArticlesViewModel::class.java]
        initEvent()
        initAdd()
        return view
    }
    fun initEvent(){
        adapter= ResultArticlesAdapter()
        rvResultArticles.layoutManager = LinearLayoutManager(requireContext())
        rvResultArticles.adapter=adapter
        searchViewModel.resultLiveData.observe(viewLifecycleOwner){ resultData ->
            articlesViewModel.initLiveData(resultData.articleList.toMutableList(),resultData.query)
        }
        articlesViewModel.liveData.observe(viewLifecycleOwner){data ->
           when(data){
               is ArticlesState.InitState ->{
                   adapter.submitList(data.articleList)
               }
               is ArticlesState.LoadingMoreState->{
                   isLoading=false
                   adapter.submitList(data.newArticleList)
               }
               is ArticlesState.RefreshState->{

               }
               is ArticlesState.ErrorState->{
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
        rvResultArticles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    articlesViewModel.loadMore()
                }
            }
        })
    }
}