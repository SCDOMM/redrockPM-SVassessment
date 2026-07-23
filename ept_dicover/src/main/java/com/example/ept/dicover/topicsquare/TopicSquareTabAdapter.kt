package com.example.ept.dicover.topicsquare

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.core.model.TabItem

/**
 * description ： 话题广场 Tab 适配器
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareTabAdapter(
    activity: AppCompatActivity,
    private val tabs: List<TabItem>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        return TopicSquareFragment.newInstance(tabs[position].apiUrl)
    }
}
