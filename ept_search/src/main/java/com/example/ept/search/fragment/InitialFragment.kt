package com.example.ept.search.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ept.search.R
import com.google.android.flexbox.FlexboxLayout

class InitialFragment : Fragment() {
    private lateinit var view: View

    private lateinit var tvInitialDelete: TextView
    private lateinit var flexInitialHistory: FlexboxLayout
    private lateinit var flexInitialRecommend: FlexboxLayout

    private lateinit var llInitial2: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view=inflater.inflate(R.layout.fragment_initial, container, false)
        tvInitialDelete=view.findViewById(R.id.tv_initial_delete)
        flexInitialRecommend=view.findViewById(R.id.flex_initial_recommend)
        flexInitialHistory=view.findViewById(R.id.flex_initial_history)
        return view
    }
}