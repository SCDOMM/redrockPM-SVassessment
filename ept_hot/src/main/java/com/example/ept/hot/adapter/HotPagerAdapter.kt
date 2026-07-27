package com.example.ept.hot.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.hot.fragment.HotListFragment

/**
 * description 排行榜ViewPager2 适配器
 * email : 3014386984@qq.com
 * date : 2026/7/15 11:23
 */
class HotPagerAdapter : FragmentStateAdapter {

    private val strategies: List<String>

    constructor(fragment: Fragment, strategies: List<String>) : super(fragment) {
        this.strategies = strategies
    }

    constructor(fragmentActivity: FragmentActivity, strategies: List<String>) : super(fragmentActivity) {
        this.strategies = strategies
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, strategies: List<String>) : super(fragmentManager, lifecycle) {
        this.strategies = strategies
    }

    override fun getItemCount(): Int = strategies.size

    override fun createFragment(position: Int): Fragment {
        return HotListFragment.newInstance(strategies[position])
    }
}
