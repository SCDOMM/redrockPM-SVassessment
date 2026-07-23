package com.example.ept.dicover.topicdetail

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * description ： 话题详情页 Tab 适配器
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailTabAdapter(
    private val activity: AppCompatActivity,
    private val tabs: List<Pair<String, String>>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        val fragment = TopicDetailFeedFragment.newInstance(tabs[position].second)
        // 注册到 Activity 的 map 中以便刷新
        if (activity is TopicDetailActivity2) {
            activity.registerFeedFragment(position, fragment)
        }
        return fragment
    }
}
