package com.example.ept.dicover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ept.community.CommunityAdapter

/**
 * description ： 话题详情页 Activity
 * email : 3014386984@qq.com
 * date : 2026/7/18  14:32
 */
/**
 * 话题详情页 Activity
 * 展示指定话题下的动态列表（图片/视频帖子）
 */
class TopicDetailActivity : AppCompatActivity() {

    companion object {
        /** 话题 ID Intent extra 键 */
        const val EXTRA_TAG_ID = "tag_id"
        /** 话题名称 Intent extra 键 */
        const val EXTRA_TAG_NAME = "tag_name"

        /**
         * 启动话题详情页
         * @param context 上下文
         * @param tagId 话题 ID
         * @param tagName 话题名称
         */
        fun start(context: Context, tagId: Int, tagName: String) {
            val intent = Intent(context, TopicDetailActivity::class.java).apply {
                putExtra(EXTRA_TAG_ID, tagId)
                putExtra(EXTRA_TAG_NAME, tagName)
            }
            context.startActivity(intent)
        }
    }

    /** 话题详情页 ViewModel */
    private lateinit var viewModel: TopicDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用沉浸式状态栏
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_detail)

        /** 根布局，用于设置系统窗口边距 */
        val root = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        viewModel = ViewModelProvider(this)[TopicDetailViewModel::class.java]

        /** 从 Intent 中获取话题 ID 和名称 */
        val tagId = intent.getIntExtra(EXTRA_TAG_ID, 0)
        val tagName = intent.getStringExtra(EXTRA_TAG_NAME) ?: ""

        /** 下拉刷新布局 */
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        /** 话题动态列表 */
        val rvDetail = findViewById<RecyclerView>(R.id.rv_topic_detail)

        /** 线性布局管理器 */
        val layoutManager = LinearLayoutManager(this)
        rvDetail.layoutManager = layoutManager

        /** 社区适配器，复用社区模块的适配器 */
        val adapter = CommunityAdapter()
        rvDetail.adapter = adapter

        /** 观察数据变化，更新列表显示 */
        viewModel.items.observe(this) { list ->
            adapter.submitList(list)
        }

        /** 观察加载状态，控制下拉刷新动画 */
        viewModel.isLoading.observe(this) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        /** 观察错误信息，显示 Toast 提示 */
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        /** 下拉刷新监听器，触发数据重新加载 */
        swipeRefresh.setOnRefreshListener {
            viewModel.loadDetail(tagId, tagName)
        }

        /** 上拉加载更多监听器，滚动到底部时自动加载下一页 */
        rvDetail.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 向下滚动时检测是否需要加载更多
                if (dy > 0) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val totalItems = layoutManager.itemCount
                    // 距离底部还有3个item时触发加载，且有下一页且不在加载中
                    if (lastVisible >= totalItems - 3 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        /** 首次加载数据，防止返回时重复加载 */
        if (!viewModel.loaded) {
            viewModel.loadDetail(tagId, tagName)
        }
    }
}
