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

    /** 发现页 ViewModel，管理分类和推荐数据的加载 */
    private val viewModel: DiscoveryViewModel by viewModels()

    /** 下拉刷新布局，用于触发刷新操作和显示加载状态 */
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

        // 分类网格：3列布局，展示视频分类入口
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_doscovery_category)
        rvCategory.layoutManager = GridLayoutManager(requireContext(), 3)

        /** 分类适配器，点击分类跳转到分类详情页 */
        val categoryAdapter = CategoryAdapter { category ->
            CategoryDetailActivity.start(requireContext(), category.apiUrl, category.name)
        }
        rvCategory.adapter = categoryAdapter

        // 更多话题按钮：跳转到话题列表页
        view.findViewById<android.widget.ImageView>(R.id.iv_doscovery_topicMore).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), TopicListActivity::class.java))
        }

        // 推荐主题横向列表：水平滚动展示推荐话题
        val rvTopic = view.findViewById<RecyclerView>(R.id.rv_discovery_topic)
        rvTopic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        /** 话题适配器，点击话题跳转到话题详情页 */
        val topicAdapter = TopicAdapter { topic ->
            TopicDetailActivity.start(requireContext(), topic.id.toInt(), topic.title)
        }
        rvTopic.adapter = topicAdapter

        // 推荐作者横向列表：水平滚动展示推荐作者
        val rvAuthor = view.findViewById<RecyclerView>(R.id.rv_discovery_author)
        rvAuthor.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val authorAdapter = TopicAdapter()
        rvAuthor.adapter = authorAdapter

        /** 观察分类数据变化 */
        viewModel.categories.observe(viewLifecycleOwner) { list ->
            categoryAdapter.submitList(list)
        }

        /** 观察话题数据变化 */
        viewModel.topics.observe(viewLifecycleOwner) { list ->
            topicAdapter.submitList(list)
        }

        /** 观察作者数据变化 */
        viewModel.authors.observe(viewLifecycleOwner) { list ->
            authorAdapter.submitList(list)
        }

        /** 观察加载状态，控制下拉刷新动画 */
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        /** 下拉刷新监听器，触发数据重新加载 */
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        /** 首次加载数据，防止返回时重复加载 */
        if (!viewModel.loaded) {
            viewModel.refresh()
        }
    }
}
