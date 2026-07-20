package com.example.ept.dicover

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * description ： 话题列表页（TabLayout + ViewPager2）
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
class TopicListActivity : AppCompatActivity() {

    private lateinit var viewModel: TopicListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_list)

        val appBar = findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.app_bar)
        ViewCompat.setOnApplyWindowInsetsListener(appBar) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, bars.top, 0, 0)
            insets
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        viewModel = ViewModelProvider(this)[TopicListViewModel::class.java]

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        viewModel.tabs.observe(this) { tabs ->
            viewPager.adapter = TopicTabAdapter(this, tabs)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position].name
            }.attach()
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        if (!viewModel.loaded) {
            viewModel.loadTabs()
        }
    }
}
