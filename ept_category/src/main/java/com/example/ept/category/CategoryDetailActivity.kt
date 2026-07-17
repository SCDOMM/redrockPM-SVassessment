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
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide

/**
 * description ： 分类详情页 Activity
 * email : 3014386984@qq.com
 * date : 2026/7/17
 */
class CategoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_API_URL = "api_url"
        const val EXTRA_CATEGORY_NAME = "category_name"

        fun start(context: Context, apiUrl: String, categoryName: String) {
            val intent = Intent(context, CategoryDetailActivity::class.java).apply {
                putExtra(EXTRA_API_URL, apiUrl)
                putExtra(EXTRA_CATEGORY_NAME, categoryName)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: CategoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_category_detail)

        // 状态栏 insets 只应用于内容，不额外加 padding
        val root = findViewById<android.view.View>(R.id.nsv_category_detail)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, 0, bars.right, 0)
            insets
        }

        viewModel = ViewModelProvider(this)[CategoryDetailViewModel::class.java]

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val ivHeader = findViewById<ImageView>(R.id.iv_category_header)
        val tvTitle = findViewById<AppCompatTextView>(R.id.actv_category_header_title)
        val tvDesc = findViewById<AppCompatTextView>(R.id.actv_category_header_desc)
        val rvCategory = findViewById<RecyclerView>(R.id.rv_category_detail)

        val apiUrl = intent.getStringExtra(EXTRA_API_URL) ?: ""
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: ""
        tvTitle.text = categoryName

        val layoutManager = LinearLayoutManager(this)
        rvCategory.layoutManager = layoutManager

        val adapter = CategoryDetailAdapter(
            onVideoClick = { video ->
                val videoIntent = Intent(this, com.example.core.media.VideoPlayerActivity::class.java).apply {
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_VIDEO_ID, video.videoId)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_VIDEO_URL, video.playUrl)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.title)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_VIDEO_COVER, video.coverUrl)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_AUTHOR_NAME, video.authorName)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_AUTHOR_ICON, video.authorIcon)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_CATEGORY, video.category)
                    putExtra(com.example.core.media.VideoPlayerActivity.EXTRA_DESCRIPTION, video.description)
                }
                startActivity(videoIntent)
            },
            onShareClick = { video ->
                if (video.webUrl.isNotEmpty()) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, video.webUrl)
                    }
                    startActivity(Intent.createChooser(shareIntent, "分享视频"))
                }
            }
        )
        rvCategory.adapter = adapter

        viewModel.tagInfo.observe(this) { info ->
            if (info != null) {
                tvDesc.text = info.description
                Glide.with(this)
                    .load(info.headerImage)
                    .placeholder(android.R.color.darker_gray)
                    .error(
                        Glide.with(this)
                            .load(info.fallbackCover)
                            .placeholder(android.R.color.darker_gray)
                    )
                    .into(ivHeader)
            }
        }

        viewModel.items.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(this) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.loadCategoryDetail(apiUrl, categoryName)
        }

        val nestedScrollView = findViewById<NestedScrollView>(R.id.nsv_category_detail)
        nestedScrollView?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val child = nestedScrollView.getChildAt(0)
            if (child != null) {
                val diff = child.bottom - (nestedScrollView.height + scrollY)
                if (diff < 500 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                    viewModel.loadNextPage()
                }
            }
        }

        if (!viewModel.loaded) {
            viewModel.loadCategoryDetail(apiUrl, categoryName)
        }
    }
}
