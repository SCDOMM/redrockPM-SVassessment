package com.example.ept.dicover.topicsquare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.model.GetPageMetroData
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.dicover.R
import com.example.ept.dicover.discovery.TopicItem
import com.example.ept.dicover.topicdetail.TopicDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题广场 Tab 内容 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareFragment : Fragment() {

    companion object {
        /** 页面标签参数键 */
        private const val ARG_PAGE_LABEL = "page_label"

        /** 通过页面标签创建 Fragment 实例 */
        fun newInstance(pageLabel: String): TopicSquareFragment {
            val fragment = TopicSquareFragment()
            val args = Bundle()
            args.putString(ARG_PAGE_LABEL, pageLabel)
            fragment.arguments = args
            return fragment
        }
    }

    private val api = RetrofitClient.create<KaiyanApi>()
    private var pageLabel = ""

    /** 从 arguments 中读取页面标签 */
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

    /** 初始化视图：配置下拉刷新、列表及点击事件 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rvTopics = view.findViewById<RecyclerView>(R.id.rv_topics)

        val topicItems = mutableListOf<TopicItem>()
        val adapter = TopicSquareAdapter { item ->
            // 点击话题卡片跳转详情
            Log.d("TopicSquareFragment", "Click: title=${item.title}, pageLabel=${item.pageLabel}")
            if (item.pageLabel.isNotEmpty()) {
                TopicDetailActivity.start(requireContext(), item.pageLabel, item.title)
            } else {
                Toast.makeText(requireContext(), "无法跳转: pageLabel为空", Toast.LENGTH_SHORT).show()
            }
        }
        rvTopics.layoutManager = LinearLayoutManager(requireContext())
        rvTopics.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            loadContent(swipeRefresh, adapter, topicItems)
        }

        loadContent(swipeRefresh, adapter, topicItems)
    }

    /** 加载话题广场数据，解析 API 返回的卡片列表 */
    private fun loadContent(
        swipeRefresh: SwipeRefreshLayout,
        adapter: TopicSquareAdapter,
        topicItems: MutableList<TopicItem>
    ) {
        swipeRefresh.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getPage(pageLabel = pageLabel).execute()
                val body = response.body()

                val items = mutableListOf<TopicSquareItem>()

                if (body?.code == 0 && body.result != null) {
                    val result = body.result!!
                    for (card in result.card_list) {
                        if (card.type != "set_metro_list") continue
                        val metroList = card.card_data?.body?.metro_list ?: continue

                        for (metro in metroList) {
                            if (metro.type != "topic") continue
                            val data = metro.metro_data ?: continue

                            val topicId = data.topic_id.toLongOrNull() ?: 0L
                            if (topicId == 0L) continue

                            val coverUrl = data.cover?.url ?: ""
                            val participantCount = data.tags?.firstOrNull()?.title ?: ""

                            // 从 link 中提取 page_label
                            Log.d("TopicSquareFragment", "metro.link='${metro.link}'")
                            val pageLabel = extractPageLabel(metro.link)

                            items.add(
                                TopicSquareItem(
                                    id = topicId,
                                    title = data.title ?: "",
                                    description = data.description,
                                    coverUrl = coverUrl,
                                    participantCount = participantCount,
                                    pageLabel = pageLabel
                                )
                            )
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    adapter.submitList(items)
                    Log.d("TopicSquareFragment", "Loaded ${items.size} topics for $pageLabel")
                    for (item in items) {
                        Log.d("TopicSquareFragment", "  - ${item.title}: pageLabel=${item.pageLabel}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("TopicSquareFragment", "loadContent failed", e)
            }
        }
    }

    /** 从 link 参数中通过正则提取 page_label 值 */
    private fun extractPageLabel(link: String): String {
        // 从 link 中提取 page_label
        // 格式: ...&api_request={"url":"...","params":{"page_label":"topic_detail-xxx",...}}
        try {
            val decoded = java.net.URLDecoder.decode(link, "UTF-8")
            // 直接用正则提取 page_label 的值
            val regex = Regex("page_label[\"\\s]*:[\"\\s]*([^\"]+)\"")
            val match = regex.find(decoded)
            return match?.groupValues?.get(1) ?: ""
        } catch (e: Exception) {
            Log.e("TopicSquareFragment", "extractPageLabel failed: $link", e)
            return ""
        }
    }
}
