package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultArticlesAdapter
import com.example.ept.search.viewmodel.SearchViewModel

class ResultArticlesFragment : Fragment() {
    private lateinit var rvResultArticles: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultArticlesAdapter
    private lateinit var viewModel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_articles, container, false)
        rvResultArticles=view.findViewById(R.id.rv_result_articles)
        viewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= ResultArticlesAdapter()
        rvResultArticles.layoutManager = LinearLayoutManager(requireContext())
        rvResultArticles.adapter=adapter


    }
}