package com.example.ept.hot.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.hot.R
import com.example.ept.hot.adapter.HotPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 * description ： 热门排行榜 Tab 容器 Fragment，加载排行 Tab 并管理 ViewPager2
 * email : 3014386984@qq.com
 * date : 2026/7/15 15:28
 */
class HotFragment : Fragment() {

    private val api = RetrofitClient.create<KaiyanApi>()
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var tabTitles = mutableListOf<String>()
    private var tabApiUrls = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hot_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.mTabLayout)
        viewPager = view.findViewById(R.id.mViewPager)
        loadTabs()
    }

    //加载排行Tab
    private fun loadTabs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.getRankListTabs()
                val tabs = response.tabInfo.tabList

                tabTitles.clear()
                tabApiUrls.clear()

                tabs.forEach { tab ->
                    tabTitles.add(mapTabName(tab.name))
                    tabApiUrls.add(tab.apiUrl)
                }

                setupViewPager()
            } catch (e: Exception) {
                Log.e("HotFragment", "Failed to load tabs", e)
                tabTitles.addAll(listOf("月排行", "周排行", "总排行"))
                tabApiUrls.addAll(listOf(
                    "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=monthly",
                    "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=weekly",
                    "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=historical"
                ))
                setupViewPager()
            }
        }
    }

    //将英文转换为中文
    private fun mapTabName(apiName: String): String {
        return when (apiName.lowercase()) {
            "monthly" -> "月排行"
            "weekly" -> "周排行"
            "historical" -> "总排行"
            else -> apiName
        }
    }

    //VP2和TabLayout联动
    private fun setupViewPager() {
        val adapter = HotPagerAdapter(this, tabApiUrls)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
