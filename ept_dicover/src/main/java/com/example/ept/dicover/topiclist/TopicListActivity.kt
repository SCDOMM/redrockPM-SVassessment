package com.example.ept.dicover.topiclist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.ept.dicover.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * description ： 话题列表页（TabLayout + ViewPager2）
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
/** 话题列表 Activity：TabLayout + ViewPager2 展示多个话题分类 */
class TopicListActivity : AppCompatActivity() {

    /** 话题列表 ViewModel */
    private lateinit var viewModel: TopicListViewModel

    /** 页面初始化：沉浸式状态栏、Toolbar 返回键、ViewPager + Tab 配置、数据观察 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_list)

        // AppBar 适配系统状态栏高度
        val appBar = findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.app_bar)
        ViewCompat.setOnApplyWindowInsetsListener(appBar) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, bars.top, 0, 0)
            insets
        }
        // Toolbar 导航按钮点击返回
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        // 初始化 ViewModel
        viewModel = ViewModelProvider(this)[TopicListViewModel::class.java]

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        // 观察 Tab 数据变化，设置 ViewPager 适配器并关联 TabLayout
        viewModel.tabs.observe(this) { tabs ->
            viewPager.adapter = TopicTabAdapter(this, tabs)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position].name
            }.attach()
        }

        // 观察错误信息，弹出 Toast 提示
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // 首次进入时加载 Tab 数据
        if (!viewModel.loaded) {
            viewModel.loadTabs()
        }
    }
}
