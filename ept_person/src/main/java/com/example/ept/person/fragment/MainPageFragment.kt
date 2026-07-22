package com.example.ept.person.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.person.R
import com.example.ept.person.adapter.MainPageAdapter
import com.example.ept.person.viewmodel.CreatorViewModel
import com.example.ept.person.viewmodel.MainPageState
import com.example.ept.person.viewmodel.MainPageViewModel

class MainPageFragment : Fragment() {
    private lateinit var view: View
    private lateinit var creatorViewModel: CreatorViewModel
    private lateinit var mainPageViewModel: MainPageViewModel
    private lateinit var rvMainPageDefault: RecyclerView
    private lateinit var adapter: MainPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_main_page, container, false)
        creatorViewModel= ViewModelProvider(requireActivity())[CreatorViewModel::class.java]
        mainPageViewModel= ViewModelProvider(this)[MainPageViewModel::class.java]
        rvMainPageDefault=view.findViewById(R.id.rv_main_page_default)
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= MainPageAdapter()
        rvMainPageDefault.layoutManager= LinearLayoutManager(view.context)
        rvMainPageDefault.adapter=adapter
        creatorViewModel.mainPageLiveData.observe(viewLifecycleOwner){tab ->
            mainPageViewModel.initLiveData(tab)
        }
        mainPageViewModel.liveData.observe(viewLifecycleOwner){state ->
            when(state){
                is MainPageState.FailedState -> Toast.makeText(view.context,state.msg,Toast.LENGTH_SHORT).show()
                is MainPageState.InitState -> {
                    adapter.submitList(state.indexList)
                }
                is MainPageState.RefreshState -> {

                }
            }
        }



    }
}