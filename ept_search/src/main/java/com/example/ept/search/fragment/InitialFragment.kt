package com.example.ept.search.fragment

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ept.search.R
import com.example.ept.search.utils.addSearchHistory
import com.example.ept.search.utils.clearSearchHistory
import com.example.ept.search.utils.getHistoryFromPrefs
import com.example.ept.search.viewmodel.SearchViewModel
import com.google.android.flexbox.FlexboxLayout

class InitialFragment : Fragment() {
    private lateinit var view: View

    private lateinit var tvInitialDelete: TextView
    private lateinit var flexInitialHistory: FlexboxLayout
    private lateinit var flexInitialRecommend: FlexboxLayout
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var llInitialContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view=inflater.inflate(R.layout.fragment_initial, container, false)
        tvInitialDelete=view.findViewById(R.id.tv_initial_delete)
        flexInitialRecommend=view.findViewById(R.id.flex_initial_recommend)
        flexInitialHistory=view.findViewById(R.id.flex_initial_history)
        llInitialContainer=view.findViewById(R.id.ll_initial_container)
        searchViewModel= ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        initEvent()
        return view
    }
    fun initEvent(){
        val history=getHistoryFromPrefs(view.context)
        flexInitialHistory.removeAllViews()
        for (query in history){
            val tagView = createTagView(query)
            flexInitialHistory.addView(tagView)
        }
        tvInitialDelete.setOnClickListener {
            clearSearchHistory(view.context)
            flexInitialHistory.removeAllViews()
        }
        searchViewModel.historyLiveData.observe(viewLifecycleOwner){string ->
            addSearchHistory(view.context,string)
            val history=getHistoryFromPrefs(view.context)
            flexInitialHistory.removeAllViews()
            for (query in history){
                val tagView = createTagView(query)
                flexInitialHistory.addView(tagView)
            }
        }
        searchViewModel.recommendLiveData.observe(viewLifecycleOwner){data->
            flexInitialRecommend.removeAllViews()
            if (data.isEmpty()) {
                return@observe
            }
            for (query in data){
                val tagView = createTagView(query)
                flexInitialRecommend.addView(tagView)
            }
        }
        searchViewModel.rankingLiveData.observe(viewLifecycleOwner){ data ->
            for (i in 0 ..6){
                val child=llInitialContainer.getChildAt(i)
                child.visibility=View.VISIBLE
                val ivCover = child.findViewById<ImageView>(R.id.iv_ranking_cover_item)
                val tvTitle = child.findViewById<TextView>(R.id.tv_ranking_title_item)
                Glide.with(ivCover)
                    .load(data[i].cover?.url)
                    .placeholder(R.drawable.eyepetater)
                    .into(ivCover)
                tvTitle.text = data[i].title

            }
        }


    }
    private fun createTagView(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setPadding(
                dp2px(12),
                dp2px(6),
                dp2px(12),
                dp2px(6)
            )
            textSize = 14f
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,  // 宽度自适应
                dp2px(36)                                  // 固定高度
            ).apply {
                setMargins(0, 0, dp2px(8), dp2px(8))      // 标签间距
            }
        }
    }
    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

}