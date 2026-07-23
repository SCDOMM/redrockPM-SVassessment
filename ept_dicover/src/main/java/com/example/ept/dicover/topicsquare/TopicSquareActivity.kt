package com.example.ept.dicover.topicsquare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
 * description ： 话题广场列表页，带 TabLayout + ViewPager2
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareActivity : AppCompatActivity() {

    /** 启动话题广场页面 */
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TopicSquareActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: TopicSquareViewModel

    /** 初始化页面：沉浸式状态栏、Toolbar、TabLayout + ViewPager2 联动 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_list)

        val appBar = findViewById<android.view.View>(R.id.app_bar)
        ViewCompat.setOnApplyWindowInsetsListener(appBar) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.title = "话题广场"

        viewModel = ViewModelProvider(this)[TopicSquareViewModel::class.java]

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        viewModel.tabs.observe(this) { tabs ->
            val adapter = TopicSquareTabAdapter(this, tabs)
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position].name
            }.attach()
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        if (!viewModel.loaded) {
            viewModel.loadTabs()
        }
    }
}
