package com.example.ept.search.fragment.resultcontent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.ept.search.R
import com.example.ept.search.adapter.ResultAdapter
import com.example.ept.search.fragment.FragmentInterface
import com.example.ept.search.viewmodel.ResultViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ResultFragment : Fragment() {
    private lateinit var view: View
    private lateinit var vp2ResultDefault: ViewPager2
    private lateinit var tlResultDefault: TabLayout
    private lateinit var viewModel: ResultViewModel
    private var fragmentList = mutableListOf<FragmentInterface>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view=inflater.inflate(R.layout.fragment_result, container, false)
        vp2ResultDefault = view.findViewById(R.id.vp2_result_default)
        tlResultDefault = view.findViewById(R.id.tl_result_default)
         viewModel= ViewModelProvider(this)[ResultViewModel::class.java]
        initEvent()
        initRequest()
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
                return ResultCreatorsFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultArticlesFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultTopicsFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return ResultUsersFragment()
            }
        })
        val adapter = ResultAdapter(fragmentList, this)
        vp2ResultDefault.adapter = adapter
    }
    fun initRequest(){





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