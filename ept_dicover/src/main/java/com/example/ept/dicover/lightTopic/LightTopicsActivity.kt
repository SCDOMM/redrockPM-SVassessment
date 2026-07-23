package com.example.ept.dicover.lightTopic

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.core.media.VideoPlayerActivity
import com.example.ept.dicover.R
import com.example.ept.dicover.adapter.VideoCardAdapter

/**
 * description ： lightTopics 详情页，使用 v3/lightTopics/internal 接口
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicsActivity : AppCompatActivity() {

    /** Intent 参数常量和页面启动入口 */
    companion object {
        const val EXTRA_TOPIC_ID = "topic_id"
        const val EXTRA_TITLE = "title"

        /** 启动轻话题详情页 */
        fun start(context: Context, topicId: Int, title: String) {
            val intent = Intent(context, LightTopicsActivity::class.java).apply {
                putExtra(EXTRA_TOPIC_ID, topicId)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: LightTopicsViewModel

    /** 页面初始化，设置布局、观察 LiveData 并加载数据 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_light_topics)

        val root = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        viewModel = ViewModelProvider(this)[LightTopicsViewModel::class.java]

        val topicId = intent.getIntExtra(EXTRA_TOPIC_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        title?.let { supportActionBar?.title = it }

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val ivHeader = findViewById<ImageView>(R.id.iv_header)
        val tvBrief = findViewById<TextView>(R.id.tv_brief)
        val tvText = findViewById<TextView>(R.id.tv_text)
        val rvVideos = findViewById<RecyclerView>(R.id.rv_videos)

        rvVideos.layoutManager = LinearLayoutManager(this)
        val adapter = VideoCardAdapter { video ->
            VideoPlayerActivity.start(this, video.id.toString())
        }
        rvVideos.adapter = adapter

        viewModel.headerImage.observe(this) { url ->
            if (url.isNotEmpty()) {
                Glide.with(this)
                    .load(url)
                    .transform(CenterCrop())
                    .into(ivHeader)
            }
        }

        viewModel.brief.observe(this) { brief ->
            tvBrief.text = brief
        }

        viewModel.text.observe(this) { text ->
            tvText.text = text
        }

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
            viewModel.loadDetail(topicId)
        }

        if (!viewModel.loaded) {
            viewModel.loadDetail(topicId)
        }
    }
}
