package com.example.ept.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.core.common.FragmentInterface
import com.example.ept.search.R
import com.example.ept.search.adapter.ResultAdapter
import com.example.ept.search.fragment.resultcontent.ResultGraphicsFragment
import com.example.ept.search.fragment.resultcontent.ResultPgcFragment
import com.example.ept.search.fragment.resultcontent.ResultTopicsFragment
import com.example.ept.search.fragment.resultcontent.ResultUgcFragment
import com.example.ept.search.fragment.resultcontent.ResultVideosFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ResultFragment : Fragment() {
    private lateinit var view: View
    private lateinit var vp2ResultDefault: ViewPager2
    private lateinit var tlResultDefault: TabLayout
    private var fragmentList = mutableListOf<FragmentInterface>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_result, container, false)
        vp2ResultDefault = view.findViewById(R.id.vp2_result_default)
        tlResultDefault = view.findViewById(R.id.tl_result_default)
        initEvent()
        initTabs()
        return view
    }

    fun initEvent() {
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultVideosFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultPgcFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultGraphicsFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultTopicsFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultUgcFragment()
            }
        })
        val adapter = ResultAdapter(fragmentList, this)
        vp2ResultDefault.adapter = adapter
    }

    fun initTabs() {
        TabLayoutMediator(tlResultDefault, vp2ResultDefault) { p0, p1 ->
            when (p1) {
                0 -> {
                    p0.text = "视频"
                }

                1 -> {
                    p0.text = "作者"
                }

                2 -> {
                    p0.text = "图文"
                }

                3 -> {
                    p0.text = "话题"
                }

                4 -> {
                    p0.text = "用户"
                }
            }
        }.attach()
    }

}