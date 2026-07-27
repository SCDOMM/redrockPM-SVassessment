package com.example.ept.hot.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.core.network.RetrofitClient
import com.example.core.network.api.UniversalApi
import com.example.core.network.await
import com.example.ept.hot.R
import com.example.ept.hot.adapter.HotPagerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 * description ： 热门排行榜 Activity，加载排行 Tab 并管理 ViewPager2
 * email : 3014386984@qq.com
 * date : 2026/7/26
 */
class HotActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HotActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val api = RetrofitClient.create<UniversalApi>()
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var tabTitles = mutableListOf<String>()
    private var strategies = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_hot)

        val root = findViewById<android.view.View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, bars.top, 0, 0)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "热门"
        toolbar.setNavigationOnClickListener { finish() }

        tabLayout = findViewById(R.id.mTabLayout)
        viewPager = findViewById(R.id.mViewPager)
        loadTabs()
    }

    private fun loadTabs() {
        lifecycleScope.launch {
            try {
                val response = api.getRankListTabs().await()
                val tabs = response.tabInfo?.tabList ?: emptyList()

                tabTitles.clear()
                strategies.clear()

                tabs.forEach { tab ->
                    tabTitles.add(mapTabName(tab.name))
                    strategies.add(tab.name)
                }

                setupViewPager()
            } catch (e: Exception) {
                tabTitles.addAll(listOf("月排行", "周排行", "总排行"))
                strategies.addAll(listOf("monthly", "weekly", "historical"))
                setupViewPager()
            }
        }
    }

    private fun mapTabName(apiName: String): String {
        return when (apiName.lowercase()) {
            "monthly" -> "月排行"
            "weekly" -> "周排行"
            "historical" -> "总排行"
            else -> apiName
        }
    }

    private fun setupViewPager() {
        val adapter = HotPagerAdapter(supportFragmentManager, lifecycle, strategies)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
