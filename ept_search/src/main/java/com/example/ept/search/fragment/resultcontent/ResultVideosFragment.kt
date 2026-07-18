package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultVideosAdapter
import com.example.ept.search.viewmodel.resultcontent.ResultVideosViewModel

class ResultVideosFragment : Fragment() {
    private lateinit var rvResultVideos: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultVideosAdapter
    private lateinit var viewModel: ResultVideosViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_videos, container, false)
        rvResultVideos=view.findViewById(R.id.rv_result_videos)
        viewModel= ViewModelProvider(this)[ResultVideosViewModel::class.java]
        return view
    }
}