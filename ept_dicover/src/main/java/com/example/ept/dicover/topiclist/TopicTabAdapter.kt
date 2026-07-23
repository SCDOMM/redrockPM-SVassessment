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
/** 话题列表 ViewPager2 页面适配器，每个 Tab 对应一个 TopicListFragment */
class TopicTabAdapter(
    activity: FragmentActivity,
    private val tabs: List<TabItem>
) : FragmentStateAdapter(activity) {

    /** 返回 Tab 总数 */
    override fun getItemCount(): Int = tabs.size

    /** 根据位置创建对应的 Fragment，传入 apiUrl 加载不同话题数据 */
    override fun createFragment(position: Int): Fragment {
        return TopicListFragment.newInstance(tabs[position].apiUrl)
    }
}
