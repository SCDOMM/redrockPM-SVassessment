package com.example.ept.hot.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.media.VideoPlayerActivity
import com.example.ept.hot.R
import com.example.ept.hot.adapter.HotVideoAdapter
import kotlinx.coroutines.launch
/**
 * description ： 热门排行榜页面容器 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/15 11:23
 */
class HotListFragment : Fragment() {

    private val viewModel: HotViewModel by viewModels()
    private lateinit var adapter: HotVideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var apiUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apiUrl = it.getString(ARG_API_URL, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.mRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout)
        setupRecyclerView()
        setupSwipeRefresh()
        observeData()
        loadVideos()
    }

    //初始化RV和视频列表适配器，设置点击跳转播放页
    private fun setupRecyclerView() {
        adapter = HotVideoAdapter { videoItem ->
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, videoItem.id)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, videoItem.playUrl)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, videoItem.title)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_COVER, videoItem.coverUrl)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_NAME, videoItem.authorName)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_ICON, videoItem.authorIcon)
                putExtra(VideoPlayerActivity.EXTRA_CATEGORY, videoItem.category)
                putExtra(VideoPlayerActivity.EXTRA_DESCRIPTION, videoItem.description)
            }
            startActivity(intent)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HotListFragment.adapter
        }
    }

    //配置下拉刷新样式和刷新监听
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            loadVideos()
        }
    }

    //触发加载排行榜视频数据
    private fun loadVideos() {
        viewModel.loadHotVideosByUrl(apiUrl)
    }

    //观察 ViewModel 数据变化，更新列表、刷新状态和错误提示
    private fun observeData() {
        viewModel.hotList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(adapter.parseItems(list))
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_API_URL = "api_url"

        //创建Fragment实例并传入APIURL
        @JvmStatic
        fun newInstance(apiUrl: String) =
            HotListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_API_URL, apiUrl)
                }
            }
    }
}
