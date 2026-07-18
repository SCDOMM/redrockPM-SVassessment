package com.example.ept.search.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.search.fragment.FragmentInterface
import com.example.ept.search.fragment.ResultFragment

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-18 15:38
 *
 */
class ResultAdapter(
    val fragmentList: MutableList<FragmentInterface>,
    resultFragment: ResultFragment
) : FragmentStateAdapter(resultFragment) {
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].back()
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }
}