package com.example.ept.notify.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.notify.activity.NotifyActivity
import com.example.ept.notify.fragment.FragmentInterface

/**   
 * 包名称： com.example.ept.home.adapter
 * 类名称：NotifyAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 17:05
 *
 */
class NotifyAdapter(
    val fragmentList: MutableList<FragmentInterface>,
    notifyActivity: NotifyActivity
) : FragmentStateAdapter(notifyActivity) {
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].back()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

}