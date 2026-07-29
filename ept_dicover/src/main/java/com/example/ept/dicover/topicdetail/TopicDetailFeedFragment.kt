package com.example.ept.dicover.topicdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.media.VideoPlayerActivity
import com.example.ept.dicover.R

/**
 * description ： 话题详情页 Feed Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailFeedFragment : Fragment() {

    companion object {
        private const val ARG_PAGE_LABEL = "page_label"

        fun newInstance(pageLabel: String): TopicDetailFeedFragment {
            val fragment = TopicDetailFeedFragment()
            val args = Bundle()
            args.putString(ARG_PAGE_LABEL, pageLabel)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: TopicDetailFeedViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: TopicDetailFeedAdapter
    private var pageLabel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageLabel = arguments?.getString(ARG_PAGE_LABEL) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_detail_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TopicDetailFeedViewModel::class.java]

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        val rvFeed = view.findViewById<RecyclerView>(R.id.rv_feed)

        adapter = TopicDetailFeedAdapter { item ->
            if (item.isVideo && item.videoId.isNotEmpty()) {
                VideoPlayerActivity.start(requireContext(), item.videoId, item.resourceType)
            }
        }
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        rvFeed.adapter = adapter

        rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy <= 0) return
                val lm = rv.layoutManager as LinearLayoutManager
                if (lm.findLastVisibleItemPosition() >= lm.itemCount - 5) {
                    viewModel.loadMore(pageLabel)
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh(pageLabel)
        }

        initObservers()
        viewModel.loadFeed(pageLabel)
    }

    fun refresh() {
        viewModel.refresh(pageLabel)
    }

    private fun initObservers() {
        viewModel.liveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TopicFeedState.RefreshState -> {
                    adapter.submitList(state.items)
                    swipeRefresh.isRefreshing = false
                }
                is TopicFeedState.LoadingMoreState -> {
                    adapter.submitList(state.items)
                }
                is TopicFeedState.ErrorState -> {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), state.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }
    }
}
