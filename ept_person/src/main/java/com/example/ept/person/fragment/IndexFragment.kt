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
import com.example.ept.person.adapter.content.IndexAdapter
import com.example.ept.person.viewmodel.CreatorViewModel
import com.example.ept.person.viewmodel.IndexState
import com.example.ept.person.viewmodel.IndexViewModel

class IndexFragment : Fragment() {
    private lateinit var view: View
    private lateinit var creatorViewModel: CreatorViewModel
    private lateinit var indexViewModel: IndexViewModel
    private lateinit var rvIndexDefault: RecyclerView
    private lateinit var adapter: IndexAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_index, container, false)
        creatorViewModel= ViewModelProvider(requireActivity())[CreatorViewModel::class.java]
        indexViewModel= ViewModelProvider(this)[IndexViewModel::class.java]
        rvIndexDefault=view.findViewById(R.id.rv_index_default)
        initEvent()
        return view
    }
    fun initEvent(){
        adapter= IndexAdapter()
        rvIndexDefault.layoutManager= LinearLayoutManager(view.context)
        rvIndexDefault.adapter=adapter
        creatorViewModel.indexLiveData.observe(viewLifecycleOwner){ tab ->
            indexViewModel.initLiveData(tab)
        }
        indexViewModel.liveData.observe(viewLifecycleOwner){ state ->
            when(state){
                is IndexState.FailedState -> Toast.makeText(view.context,state.msg,Toast.LENGTH_SHORT).show()
                is IndexState.InitState -> {
                    adapter.submitList(state.indexList)
                }
                is IndexState.RefreshState -> {

                }
            }
        }



    }
}