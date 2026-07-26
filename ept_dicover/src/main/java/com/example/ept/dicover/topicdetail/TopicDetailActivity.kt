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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
const val EXTRA_PAGE_LABEL = "page_label"
/** 标题键 */
const val EXTRA_TITLE = "title"
class TopicDetailActivity : AppCompatActivity() {
    companion object {
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
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var appBar: AppBarLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var ivHeader: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvStats: TextView
    private lateinit var pageLabel: String
    private lateinit var title: String
    fun registerFeedFragment(position: Int, fragment: TopicDetailFeedFragment) {
        feedFragments[position] = fragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_detail)
        val rootLayout = findViewById<android.view.View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }
        swipeRefresh = findViewById(R.id.swipe_refresh)
        appBar = findViewById(R.id.appbar)
        tabLayout = findViewById(R.id.tab_topic)
        viewPager = findViewById(R.id.vp_topic)
        ivHeader = findViewById(R.id.iv_topic_header)
        tvTitle = findViewById(R.id.tv_topic_title)
        tvDescription = findViewById(R.id.tv_topic_desc)
        tvStats = findViewById(R.id.tv_topic_stats)
        viewModel = ViewModelProvider(this)[TopicDetailViewModel::class.java]
        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            swipeRefresh.isEnabled = verticalOffset == 0
        }
        pageLabel = intent.getStringExtra(EXTRA_PAGE_LABEL) ?: ""
        title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        initEvent()
    }
    fun initEvent(){
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
