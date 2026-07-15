package com.example.ept.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.home.adapter.HomeAdapter
import com.example.ept.home.viewmodel.HomeViewModel
import com.example.ept.home.R

class HomeFragment : Fragment() {
    private lateinit var tbHomeTop: Toolbar
    private lateinit var ivHomeNotify: ImageView
    private lateinit var rvHomeDefault: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_home, container, false)
        tbHomeTop=view.findViewById(R.id.tb_home_top)
        ivHomeNotify=view.findViewById(R.id.iv_home_notify)
        rvHomeDefault=view.findViewById(R.id.rv_home_default)
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