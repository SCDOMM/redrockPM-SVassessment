package com.example.ept.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.media.VideoPlayerActivity

/**
 * description ： 分类详情页视频列表 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
class CategoryFeedFragment : Fragment() {

    companion object {
        /** 页面标签参数键 */
        private const val ARG_PAGE_LABEL = "page_label"

        /** 创建新的 Fragment 实例 */
        fun newInstance(pageLabel: String): CategoryFeedFragment {
            return CategoryFeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_LABEL, pageLabel)
                }
            }
        }
    }

    /** 页面视图模型 */
    val viewModel: CategoryFeedViewModel by viewModels()
    private lateinit var adapter: CategoryDetailAdapter
    private var pageLabel = ""

    /** 创建视图 */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_feed, container, false)
    }

    /** 视图创建完成 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageLabel = arguments?.getString(ARG_PAGE_LABEL) ?: ""

        val rvFeed = view.findViewById<RecyclerView>(R.id.rv_feed)
        rvFeed.layoutManager = LinearLayoutManager(requireContext())

        adapter = CategoryDetailAdapter(
            onVideoClick = { video ->
                val videoId = video.videoId.toString()
                if (videoId != "0") {
                    VideoPlayerActivity.start(requireContext(), videoId)
                }
            },
            onShareClick = { video ->
                if (video.webUrl.isNotEmpty()) {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, video.webUrl)
                    }
                    startActivity(android.content.Intent.createChooser(shareIntent, "分享视频"))
                }
            }
        )
        rvFeed.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // 上拉加载更多
        rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val total = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (total - lastVisible <= 6 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                    viewModel.loadNextPage()
                }
            }
        })

        if (viewModel.items.value.isNullOrEmpty()) {
            viewModel.loadFeed(pageLabel)
        }
    }

    /**
     * 供 Activity 调用：刷新当前 tab 的数据
     */
    fun refresh() {
        viewModel.loadFeed(pageLabel)
    }
}
