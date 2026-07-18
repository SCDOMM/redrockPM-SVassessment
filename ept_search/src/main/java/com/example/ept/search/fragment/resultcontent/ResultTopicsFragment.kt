package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultTopicsAdapter
import com.example.ept.search.viewmodel.resultcontent.ResultTopicsViewModel


class ResultTopicsFragment : Fragment() {
    private lateinit var rvResultTopics: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultTopicsAdapter
    private lateinit var viewModel: ResultTopicsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view= inflater.inflate(R.layout.fragment_topics, container, false)
        rvResultTopics=view.findViewById(R.id.rv_result_topics)
        viewModel= ViewModelProvider(this)[ResultTopicsViewModel::class.java]
        return view
    }

}