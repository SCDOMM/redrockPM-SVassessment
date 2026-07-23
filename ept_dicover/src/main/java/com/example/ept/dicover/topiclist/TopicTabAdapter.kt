package com.example.ept.dicover.topiclist

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.core.model.TabItem

/**
 * description ： 话题列表 ViewPager2 适配器
 * email : 3014386984@qq.com
 * date : 2026/7/18  19:38
 */
class TopicTabAdapter(
    activity: FragmentActivity,
    private val tabs: List<TabItem>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return TopicListFragment.newInstance(tabs[position].apiUrl)
    }
}
