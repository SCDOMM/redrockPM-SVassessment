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
 * date : 2026/7/18
 */
class TopicDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TAG_ID = "tag_id"
        const val EXTRA_TAG_NAME = "tag_name"

        fun start(context: Context, tagId: Int, tagName: String) {
            val intent = Intent(context, TopicDetailActivity::class.java).apply {
                putExtra(EXTRA_TAG_ID, tagId)
                putExtra(EXTRA_TAG_NAME, tagName)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: TopicDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_topic_detail)

        val root = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        viewModel = ViewModelProvider(this)[TopicDetailViewModel::class.java]

        val tagId = intent.getIntExtra(EXTRA_TAG_ID, 0)
        val tagName = intent.getStringExtra(EXTRA_TAG_NAME) ?: ""

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rvDetail = findViewById<RecyclerView>(R.id.rv_topic_detail)

        val layoutManager = LinearLayoutManager(this)
        rvDetail.layoutManager = layoutManager

        val adapter = CommunityAdapter()
        rvDetail.adapter = adapter

        viewModel.items.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(this) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.loadDetail(tagId, tagName)
        }

        // 上拉加载更多（与社区页相同模式）
        rvDetail.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val totalItems = layoutManager.itemCount
                    if (lastVisible >= totalItems - 3 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        if (!viewModel.loaded) {
            viewModel.loadDetail(tagId, tagName)
        }
    }
}
