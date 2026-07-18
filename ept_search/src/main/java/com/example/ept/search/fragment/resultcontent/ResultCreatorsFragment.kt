package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultCreatorsAdapter
import com.example.ept.search.viewmodel.resultcontent.ResultCreatorsViewModel

class ResultCreatorsFragment : Fragment() {

    private lateinit var rvResultCreators: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultCreatorsAdapter
    private lateinit var viewModel: ResultCreatorsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_creators, container, false)
        rvResultCreators=view.findViewById(R.id.rv_result_creators)
        viewModel= ViewModelProvider(this)[ResultCreatorsViewModel::class.java]
        return view
    }
}