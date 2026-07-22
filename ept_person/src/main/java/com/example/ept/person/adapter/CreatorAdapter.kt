package com.example.ept.person.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ept.person.CreatorActivity
import com.example.ept.person.fragment.FragmentInterface

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：CreatorAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 14:05
 *
 */
class CreatorAdapter(
    val fragmentList: MutableList<FragmentInterface>,
    creatorActivity: CreatorActivity
) : FragmentStateAdapter(creatorActivity) {
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].back()

    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}