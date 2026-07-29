package com.example.ept.hot.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.media.VideoPlayerActivity
import com.example.ept.hot.R
import com.example.ept.hot.adapter.HotVideoAdapter

/**
 * description 热门排行榜页面容器 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/15 11:23
 */
class HotListFragment : Fragment() {

    private val viewModel: HotViewModel by viewModels()
    private lateinit var adapter: HotVideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var isLoading = false

    private var strategy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            strategy = it.getString(ARG_STRATEGY, "")
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

    private fun setupRecyclerView() {
        adapter = HotVideoAdapter { videoItem ->
            if (!isAdded) return@HotVideoAdapter
            VideoPlayerActivity.start(requireContext(), videoItem.id.toString())
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HotListFragment.adapter
        }
    }

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

    private fun loadVideos() {
        viewModel.loadHotVideos(strategy)
    }

    private fun observeData() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoading) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalCount = layoutManager.itemCount
                if (totalCount <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val preloadThreshold = 5
                val remainingItems = totalCount - lastVisible - 1
                if (remainingItems <= preloadThreshold) {
                    isLoading = true
                    viewModel.loadMore()
                }
            }
        })
        viewModel.hotList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HotState.RefreshState -> {
                    adapter.submitList(adapter.parseItems(state.list))
                }
                is HotState.LoadingMoreState -> {
                    isLoading = false
                    val merged = adapter.currentList.toMutableList()
                    merged.addAll(adapter.parseItems(state.newList))
                    adapter.submitList(merged)
                }
                is HotState.ErrorState -> {
                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), state.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    companion object {
        private const val ARG_STRATEGY = "strategy"

        @JvmStatic
        fun newInstance(strategy: String) =
            HotListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STRATEGY, strategy)
                }
            }
    }
}
