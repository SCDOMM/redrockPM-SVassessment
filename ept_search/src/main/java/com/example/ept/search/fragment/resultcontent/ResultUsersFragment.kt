package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.adapter.resultcontent.ResultUsersAdapter
import com.example.ept.search.viewmodel.resultcontent.ResultUsersViewModel

class ResultUsersFragment : Fragment() {
    private lateinit var rvResultUsers: RecyclerView
    private lateinit var view: View
    private lateinit var adapter: ResultUsersAdapter
    private lateinit var viewModel: ResultUsersViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_users, container, false)
        rvResultUsers=view.findViewById(R.id.rv_result_users)
        viewModel= ViewModelProvider(this)[ResultUsersViewModel::class.java]
        return view
    }


}