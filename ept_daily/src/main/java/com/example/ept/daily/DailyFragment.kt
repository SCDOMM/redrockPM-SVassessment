package com.example.ept.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class DailyFragment : Fragment() {
    lateinit var view: View
    private lateinit var ivDailyNotify: ImageView
    private lateinit var ivDailySearch: ImageView
    private lateinit var rvDailyDefault: RecyclerView
    private lateinit var viewModel: DailyViewModel
    private lateinit var adapter: DailyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_daily, container, false)
        ivDailyNotify=view.findViewById(R.id.iv_daily_notify)
        ivDailySearch=view.findViewById(R.id.iv_daily_search)
        rvDailyDefault=view.findViewById(R.id.rv_daily_default)
        viewModel = ViewModelProvider(this)[DailyViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
     adapter= DailyAdapter()
        rvDailyDefault.adapter=adapter
        adapter.submitList()
        ivDailyNotify.setOnClickListener {
            initNotify()
        }
        ivDailySearch.setOnClickListener {
            initSearch()
        }
    }
    fun initNotify(){


    }
    fun initSearch(){


    }
}