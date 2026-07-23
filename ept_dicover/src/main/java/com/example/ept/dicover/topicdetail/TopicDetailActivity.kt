package com.example.ept.dicover.topicdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.ept.dicover.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * description ： 话题详情页，类似分类详情页布局
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailActivity : AppCompatActivity() {

    companion object {
        /** 页面标签键 */
        const val EXTRA_PAGE_LABEL = "page_label"
        /** 标题键 */
        const val EXTRA_TITLE = "title"

        /** 启动话题详情页 */
        fun start(context: Context, pageLabel: String, title: String) {
            val intent = Intent(context, TopicDetailActivity::class.java).apply {
                putExtra(EXTRA_PAGE_LABEL, pageLabel)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: TopicDetailViewModel
    private var feedFragments = mutableMapOf<Int, TopicDetailFeedFragment>()
    private var feedAdapter: TopicDetailTabAdapter? = null

    /** 注册 Feed Fragment 到 map 中，便于下拉刷新 */
    fun registerFeedFragment(position: Int, fragment: TopicDetailFeedFragment) {
        feedFragments[position] = fragment
    }

    /** 页面初始化，设置布局、观察数据、配置下拉刷新 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_detail_2)

        val rootLayout = findViewById<android.view.View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        val pageLabel = intent.getStringExtra(EXTRA_PAGE_LABEL) ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        viewModel = ViewModelProvider(this)[TopicDetailViewModel::class.java]

        val swipeRefresh = findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipe_refresh)
        val appBar = findViewById<AppBarLayout>(R.id.appbar)
        val tabLayout = findViewById<TabLayout>(R.id.tab_topic)
        val viewPager = findViewById<ViewPager2>(R.id.vp_topic)
        val ivHeader = findViewById<ImageView>(R.id.iv_topic_header)
        val tvTitle = findViewById<TextView>(R.id.tv_topic_title)
        val tvDescription = findViewById<TextView>(R.id.tv_topic_desc)
        val tvStats = findViewById<TextView>(R.id.tv_topic_stats)

        // AppBarLayout 折叠时禁用下拉刷新
        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            swipeRefresh.isEnabled = verticalOffset == 0
        }

        viewModel.tagInfo.observe(this) { info ->
            tvTitle.text = info.title
            tvDescription.text = info.description
            tvStats.text = info.stats

            if (info.headerImage.isNotEmpty()) {
                Glide.with(this)
                    .load(info.headerImage)
                    .transform(CenterCrop())
                    .into(ivHeader)
            }

            // 创建 Tab + ViewPager
            if (feedAdapter == null && info.feedPageLabels.isNotEmpty()) {
                feedAdapter = TopicDetailTabAdapter(this, info.feedPageLabels)
                viewPager.adapter = feedAdapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = info.feedPageLabels[position].first
                }.attach()
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        // 下拉刷新
        swipeRefresh.setOnRefreshListener {
            val currentFragment = feedFragments[viewPager.currentItem]
            currentFragment?.refresh()
        }

        if (!viewModel.loaded) {
            viewModel.loadDetail(pageLabel)
        }
    }
}
