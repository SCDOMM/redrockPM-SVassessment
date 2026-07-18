package com.example.ept.dicover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ept.category.CategoryDetailActivity

/**
 * description ： 发现页 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/16 11:39
 */
class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by viewModels()

    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        // 分类网格
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_doscovery_category)
        rvCategory.layoutManager = GridLayoutManager(requireContext(), 3)

        val categoryAdapter = CategoryAdapter { category ->
            CategoryDetailActivity.start(requireContext(), category.apiUrl, category.name)
        }
        rvCategory.adapter = categoryAdapter

        // 更多话题按钮
        view.findViewById<android.widget.ImageView>(R.id.iv_doscovery_topicMore).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), TopicListActivity::class.java))
        }

        // 推荐主题横向列表
        val rvTopic = view.findViewById<RecyclerView>(R.id.rv_discovery_topic)
        rvTopic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val topicAdapter = TopicAdapter { topic ->
            TopicDetailActivity.start(requireContext(), topic.id.toInt(), topic.title)
        }
        rvTopic.adapter = topicAdapter

        // 推荐作者横向列表
        val rvAuthor = view.findViewById<RecyclerView>(R.id.rv_discovery_author)
        rvAuthor.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val authorAdapter = TopicAdapter()
        rvAuthor.adapter = authorAdapter

        viewModel.categories.observe(viewLifecycleOwner) { list ->
            categoryAdapter.submitList(list)
        }

        viewModel.topics.observe(viewLifecycleOwner) { list ->
            topicAdapter.submitList(list)
        }

        viewModel.authors.observe(viewLifecycleOwner) { list ->
            authorAdapter.submitList(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        if (!viewModel.loaded) {
            viewModel.refresh()
        }
    }
}
