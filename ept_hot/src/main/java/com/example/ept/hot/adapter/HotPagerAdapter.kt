package com.example.ept.hot.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.hot.fragment.HotListFragment

/**
 * description ： 排行榜 ViewPager2 适配器，管理各排行 Tab 的 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/15 11:23
 */
class HotPagerAdapter(
    fragment: Fragment,
    private val apiUrls: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = apiUrls.size

    override fun createFragment(position: Int): Fragment {
        return HotListFragment.newInstance(apiUrls[position])
    }
}
