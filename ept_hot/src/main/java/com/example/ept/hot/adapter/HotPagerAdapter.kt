package com.example.ept.hot.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.hot.fragment.HotFragment1

class HotPagerAdapter(
    fragment: Fragment,
    private val apiUrls: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = apiUrls.size

    override fun createFragment(position: Int): Fragment {
        return HotFragment1.newInstance(apiUrls[position])
    }
}
