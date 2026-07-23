package com.example.ept.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * description ： 分类详情页 Activity
 * email : 3014386984@qq.com
 * date : 2026/7/17
 */
class CategoryDetailActivity : AppCompatActivity() {

    companion object {
        /** 页面标签键 */
        const val EXTRA_PAGE_LABEL = "page_label"
        /** 分类名称键 */
        const val EXTRA_CATEGORY_NAME = "category_name"

        /** 启动分类详情页 */
        fun start(context: Context, pageLabel: String, categoryName: String) {
            val intent = Intent(context, CategoryDetailActivity::class.java).apply {
                putExtra(EXTRA_PAGE_LABEL, pageLabel)
                putExtra(EXTRA_CATEGORY_NAME, categoryName)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: CategoryDetailViewModel
    private var currentTabPosition = 0
    private val feedFragments = mutableMapOf<Int, CategoryFeedFragment>()

    /** 页面创建 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_category_detail)

        val root = findViewById<android.view.View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        viewModel = ViewModelProvider(this)[CategoryDetailViewModel::class.java]

        val swipeRefresh = findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipe_refresh)
        val ivHeader = findViewById<ImageView>(R.id.iv_category_header)
        val tvTitle = findViewById<AppCompatTextView>(R.id.actv_category_header_title)
        val tvDesc = findViewById<AppCompatTextView>(R.id.actv_category_header_desc)
        val tvStats = findViewById<AppCompatTextView>(R.id.tv_category_stats)
        val tabLayout = findViewById<TabLayout>(R.id.tab_category)
        val viewPager = findViewById<ViewPager2>(R.id.vp_category)

        val pageLabel = intent.getStringExtra(EXTRA_PAGE_LABEL) ?: ""
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: ""
        tvTitle.text = categoryName

        var feedAdapter: FragmentStateAdapter? = null

        // AppBarLayout 折叠时禁用 SwipeRefreshLayout，避免上滑触发刷新
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val isExpanded = verticalOffset == 0
            swipeRefresh.isEnabled = isExpanded
        }

        // ViewPager2 页面切换
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentTabPosition = position
            }
        })

        // 观察 header 信息
        viewModel.tagInfo.observe(this) { info ->
            if (info != null) {
                if (info.description.isNotEmpty()) {
                    tvDesc.text = info.description
                }
                if (info.stats.isNotEmpty()) {
                    tvStats.text = info.stats
                }
                if (info.headerImage.isNotEmpty()) {
                    Glide.with(this)
                        .load(info.headerImage)
                        .placeholder(android.R.color.darker_gray)
                        .into(ivHeader)
                }
                // 设置 ViewPager2
                if (info.feedPageLabels.isNotEmpty() && feedAdapter == null) {
                    val feedPageLabels = info.feedPageLabels.map { it.second }
                    val tabTitles = info.feedPageLabels.map { it.first }

                    feedAdapter = object : FragmentStateAdapter(this) {
                        override fun getItemCount() = feedPageLabels.size
                        override fun createFragment(position: Int): Fragment {
                            val fragment = CategoryFeedFragment.newInstance(feedPageLabels[position])
                            feedFragments[position] = fragment
                            return fragment
                        }
                    }
                    viewPager.adapter = feedAdapter

                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = tabTitles[position]
                    }.attach()
                }
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // SwipeRefreshLayout 刷新当前 tab
        swipeRefresh.setOnRefreshListener {
            val currentFragment = feedFragments[currentTabPosition]
            if (currentFragment != null) {
                currentFragment.refresh()
                currentFragment.viewModel.isLoading.observe(this) { loading ->
                    if (!loading) {
                        swipeRefresh.isRefreshing = false
                    }
                }
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        if (!viewModel.loaded) {
            viewModel.loadCategoryDetail(pageLabel, categoryName)
        }
    }
}
