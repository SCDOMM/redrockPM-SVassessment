package com.example.ept.search.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.PreSearchAdapter
import com.example.ept.search.viewmodel.SearchViewModel

class PreSearchFragment : Fragment() {

    private lateinit var rvPreSearchDefault: RecyclerView
    private lateinit var view: View
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var adapter: PreSearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_presearch, container, false)
        rvPreSearchDefault = view.findViewById(R.id.rv_presearch_default)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        initEvent()
        return view
    }

    fun initEvent() {
        adapter= PreSearchAdapter()
        rvPreSearchDefault.adapter=adapter
        rvPreSearchDefault.layoutManager= LinearLayoutManager(view.context)
        searchViewModel.preSearchLiveData.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.toMutableList())
        }
    }
}