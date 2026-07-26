package com.example.ept.hot.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.hot.fragment.HotListFragment

/**
 * description �?排行�?ViewPager2 适配器，管理各排�?Tab �?Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/15 11:23
 */
class HotPagerAdapter : FragmentStateAdapter {

    private val apiUrls: List<String>

    constructor(fragment: Fragment, apiUrls: List<String>) : super(fragment) {
        this.apiUrls = apiUrls
    }

    constructor(fragmentActivity: FragmentActivity, apiUrls: List<String>) : super(fragmentActivity) {
        this.apiUrls = apiUrls
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, apiUrls: List<String>) : super(fragmentManager, lifecycle) {
        this.apiUrls = apiUrls
    }

    override fun getItemCount(): Int = apiUrls.size

    override fun createFragment(position: Int): Fragment {
        return HotListFragment.newInstance(apiUrls[position])
    }
}
