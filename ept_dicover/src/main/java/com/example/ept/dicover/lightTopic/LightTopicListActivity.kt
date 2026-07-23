package com.example.ept.dicover.lightTopic

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
import com.example.core.media.VideoPlayerActivity
import com.example.ept.dicover.R

/**
 * description ： 主题播单列表页
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicListActivity : AppCompatActivity() {

    /** 页面启动入口 */
    companion object {
        /** 启动主题播单列表页 */
        fun start(context: Context) {
            val intent = Intent(context, LightTopicListActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: LightTopicListViewModel

    /** 页面初始化，设置布局、观察 LiveData 并加载数据 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_light_topic_list)

        val root = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        supportActionBar?.title = "主题播单"

        viewModel = ViewModelProvider(this)[LightTopicListViewModel::class.java]

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rvTopics = findViewById<RecyclerView>(R.id.rv_topics)

        rvTopics.layoutManager = LinearLayoutManager(this)
        val adapter = LightTopicListAdapter(
            onTopicClick = { topic ->
                // 点击整个卡片跳转详情
                if (topic.topicId > 0) {
                    LightTopicsActivity.start(this, topic.topicId, topic.title)
                }
            },
            onVideoClick = { video ->
                // 点击视频跳转播放
                VideoPlayerActivity.start(this, video.id.toString())
            }
        )
        rvTopics.adapter = adapter

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
            viewModel.loadTopics()
        }

        if (!viewModel.loaded) {
            viewModel.loadTopics()
        }
    }
}
