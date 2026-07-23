/**
 * description ： 发现页 Fragment，展示分类网格、主题播单和话题广场
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
package com.example.ept.dicover.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ept.category.CategoryDetailActivity
import com.example.ept.dicover.R
import com.example.ept.dicover.lightTopic.LightTopicListActivity
import com.example.ept.dicover.lightTopic.LightTopicsActivity
import com.example.ept.dicover.topiclist.TopicListActivity
import com.example.ept.dicover.topicsquare.TopicSquareActivity

class DiscoveryFragment : Fragment() {

    /** 发现页 ViewModel */
    private val viewModel: DiscoveryViewModel by viewModels()
    /** 下拉刷新布局 */
    private lateinit var swipeRefresh: SwipeRefreshLayout
    /** 话题广场卡片堆叠视图 */
    private lateinit var cardStackView: LoopingCardStackView
    /** 话题广场页码指示器 */
    private lateinit var tvIndicator: TextView
    /** 话题广场数据列表 */
    private val squareItems = mutableListOf<TopicItem>()
    /** 当前显示的话题广场索引 */
    private var currentSquareIndex = 0
    /** 话题广场是否已初始化 */
    private var isSquareInitialized = false

    /** 创建视图 */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_discovery, container, false)

    /** 初始化视图和数据观察 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        // === 分类网格 ===
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_doscovery_category)
        rvCategory.layoutManager = GridLayoutManager(requireContext(), 3)
        val categoryAdapter = CategoryAdapter { category ->
            CategoryDetailActivity.start(requireContext(), category.pageLabel, category.name)
        }
        rvCategory.adapter = categoryAdapter

        // === 主题播单 ===
        view.findViewById<android.widget.ImageView>(R.id.iv_doscovery_topicMore).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), TopicListActivity::class.java))
        }
        // 主题播单"更多"按钮 → 跳转主题播单列表页
        view.findViewById<android.widget.ImageView>(R.id.iv_doscovery_lightTopicMore).setOnClickListener {
            LightTopicListActivity.start(requireContext())
        }
        val rvTopic = view.findViewById<RecyclerView>(R.id.rv_discovery_topic)
        rvTopic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val topicAdapter = TopicAdapter { topic ->
            if (topic.id > 0) {
                LightTopicsActivity.start(requireContext(), topic.id.toInt(), topic.title)
            }
        }
        rvTopic.adapter = topicAdapter

        // === 话题广场 ===
        cardStackView = view.findViewById(R.id.card_stack_view)
        tvIndicator = view.findViewById(R.id.tv_square_indicator)

        // 滑动回调：更新指示器
        cardStackView.onCardSwiped = { _ ->
            currentSquareIndex++
            updateIndicator()
        }

        // 卡片回收回调：为回收到底部的卡片重新绑定数据
        cardStackView.onCardRecycled = { recycledView ->
            // 回收的卡片应显示"当前位置 + 可见层数 - 1"的 item，避免与已显示的重复
            val nextIndex = (currentSquareIndex + 2) % squareItems.size
            bindCardData(recycledView, squareItems[nextIndex])
        }

        // 卡片提供者：创建卡片 View（带点击监听）
        cardStackView.cardProvider = {
            val cardView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_topic_square_card, cardStackView, false)
            // 点击监听：通过 tag 传递 topic
            cardView.setOnClickListener {
                val topic = it.tag as? TopicItem ?: return@setOnClickListener
                if (topic.id > 0) {
                    LightTopicsActivity.start(requireContext(), topic.id.toInt(), topic.title)
                }
            }
            cardView
        }

        // 话题广场"更多"按钮 → 跳转话题广场列表页
        view.findViewById<android.widget.ImageView>(R.id.iv_doscovery_topicMore).setOnClickListener {
            TopicSquareActivity.start(requireContext())
        }

        // === 观察数据 ===
        viewModel.categories.observe(viewLifecycleOwner) { categoryAdapter.submitList(it) }
        viewModel.topics.observe(viewLifecycleOwner) { topicAdapter.submitList(it) }

        viewModel.squareItems.observe(viewLifecycleOwner) { list ->
            squareItems.clear()
            squareItems.addAll(list)

            // 预加载前几张卡片的图片，避免滑动时触发网络/磁盘加载
            for (i in 0 until minOf(5, squareItems.size)) {
                Glide.with(this)
                    .load(squareItems[i].icon)
                    .transform(CenterCrop(), RoundedCorners(16))
                    .preload()
            }

            if (!isSquareInitialized && squareItems.isNotEmpty()) {
                isSquareInitialized = true
                currentSquareIndex = 0
                cardStackView.start()
                // start() 创建了 maxVisibleCards 个空卡片，手动绑定数据
                for (i in 0 until minOf(cardStackView.childCount, squareItems.size)) {
                    bindCardData(cardStackView.getChildAt(i), squareItems[i])
                }
            }
            updateIndicator()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { swipeRefresh.isRefreshing = it }
        swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        if (!viewModel.loaded) viewModel.refresh()
    }

    /** 绑定卡片数据（封面图和标题） */
    private fun bindCardData(view: View, topic: TopicItem) {
        val ivCover = view.findViewById<ImageView>(R.id.iv_cover)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = topic.title
        Glide.with(this)
            .load(topic.icon)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(ivCover)
        view.tag = topic
    }

    /** 更新页码指示器文本 */
    private fun updateIndicator() {
        val total = squareItems.size
        if (total > 0) {
            tvIndicator.text = "${(currentSquareIndex % total) + 1}/$total"
        }
    }
}
