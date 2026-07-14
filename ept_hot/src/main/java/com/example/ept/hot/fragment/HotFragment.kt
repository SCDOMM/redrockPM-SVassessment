package com.example.ept.hot.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.hot.adapter.HotPagerAdapter
import com.example.ept.hot.databinding.HotFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class HotFragment : Fragment() {

    private var _binding: HotFragmentBinding? = null
    private val binding get() = _binding!!

    private val api = RetrofitClient.create<KaiyanApi>()

    private var tabTitles = mutableListOf<String>()
    private var tabApiUrls = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HotFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTabs()
    }

    private fun loadTabs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.getRankListTabs()
                val tabs = response.tabInfo.tabList

                tabTitles.clear()
                tabApiUrls.clear()

                tabs.forEach { tab ->
                    tabTitles.add(tab.name)
                    tabApiUrls.add(tab.apiUrl)
                }

                setupViewPager()
            } catch (e: Exception) {
                Log.e("HotFragment", "Failed to load tabs", e)
                // 使用默认 Tab
                tabTitles.addAll(listOf("周排行", "月排行"))
                tabApiUrls.addAll(listOf(
                    "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=weekly",
                    "http://baobab.kaiyanapp.com/api/v4/rankList/videos?strategy=monthly"
                ))
                setupViewPager()
            }
        }
    }

    private fun setupViewPager() {
        val adapter = HotPagerAdapter(this, tabApiUrls)
        binding.mViewPager.adapter = adapter

        TabLayoutMediator(binding.mTabLayout, binding.mViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
