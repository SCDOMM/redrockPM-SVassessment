package com.example.ept.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R
import com.example.ept.search.viewmodel.OverlayViewModel

class SuggestionFragment : Fragment() {

    private lateinit var viewModel: OverlayViewModel
    private lateinit var rvOverlayDefault: RecyclerView
    private lateinit var view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view= inflater.inflate(R.layout.fragment_overlay, container, false)
        viewModel= ViewModelProvider(this)[OverlayViewModel::class.java]
        rvOverlayDefault=view.findViewById(R.id.rv_suggestion_default)

        return view
    }
}