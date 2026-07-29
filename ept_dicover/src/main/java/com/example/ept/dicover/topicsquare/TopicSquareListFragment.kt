package com.example.ept.dicover.topicsquare

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
import com.example.ept.dicover.R
import com.example.ept.dicover.topicdetail.TopicDetailActivity

/**
 * description ： 话题广场 Tab 内容 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareListFragment : Fragment() {

    companion object {
        private const val ARG_PAGE_LABEL = "page_label"

        fun newInstance(pageLabel: String): TopicSquareListFragment {
            val fragment = TopicSquareListFragment()
            val args = Bundle()
            args.putString(ARG_PAGE_LABEL, pageLabel)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: TopicSquareListViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: TopicSquareListAdapter
    private var pageLabel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageLabel = arguments?.getString(ARG_PAGE_LABEL) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TopicSquareListViewModel::class.java]

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        val rvTopics = view.findViewById<RecyclerView>(R.id.rv_topics)

        adapter = TopicSquareListAdapter { item ->
            if (item.pageLabel.isNotEmpty()) {
                TopicDetailActivity.start(requireContext(), item.pageLabel, item.title)
            } else {
                Toast.makeText(requireContext(), "无法跳转: pageLabel为空", Toast.LENGTH_SHORT).show()
            }
        }
        rvTopics.layoutManager = LinearLayoutManager(requireContext())
        rvTopics.adapter = adapter

        rvTopics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy <= 0) return
                val lm = rv.layoutManager as LinearLayoutManager
                if (lm.findLastVisibleItemPosition() >= lm.itemCount - 5) {
                    viewModel.loadMore()
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh(pageLabel)
        }

        initObservers()
        viewModel.loadTopics(pageLabel)
    }

    private fun initObservers() {
        viewModel.liveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TopicSquareListState.RefreshState -> {
                    adapter.submitList(state.topics)
                    swipeRefresh.isRefreshing = false
                }
                is TopicSquareListState.LoadingMoreState -> {
                    adapter.submitList(state.topics)
                }
                is TopicSquareListState.ErrorState -> {
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
