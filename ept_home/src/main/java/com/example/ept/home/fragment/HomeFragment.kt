package com.example.ept.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.home.adapter.HomeAdapter
import com.example.ept.home.viewmodel.HomeViewModel
import com.example.ept.home.R

class HomeFragment : Fragment() {
    private lateinit var ivHomeNotify: ImageView
    private lateinit var rvHomeDefault: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private lateinit var view: View
    private lateinit var adapter: HomeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_home, container, false)
        ivHomeNotify=view.findViewById(R.id.iv_home_notify)
        rvHomeDefault=view.findViewById(R.id.rv_home_default)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initEvent()
        return  view
    }
    fun initEvent(){
        adapter= HomeAdapter()
        rvHomeDefault.adapter=adapter
        adapter.submitList()
        ivHomeNotify.setOnClickListener {
            initNotify()
        }
    }
    fun initNotify(){



    }



}