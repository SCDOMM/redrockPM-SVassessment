package com.example.ept.dicover

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 单个 tab 的话题列表 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/18
 */
/**
 * 单个 tab 的话题列表 Fragment
 * 根据 API URL 加载并展示话题列表
 */
class TopicListFragment : Fragment() {

    companion object {
        /** API URL 参数键 */
        private const val ARG_API_URL = "api_url"

        /**
         * 创建新的 TopicListFragment 实例
         * @param apiUrl 话题列表的 API 地址
         */
        fun newInstance(apiUrl: String): TopicListFragment {
            return TopicListFragment().apply {
                arguments = Bundle().apply { putString(ARG_API_URL, apiUrl) }
            }
        }
    }

    /** 开眼 API 接口实例，用于网络请求 */
    private val api = RetrofitClient.create<KaiyanApi>()
    /** 协程作用域，用于管理异步任务 */
    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** 下拉刷新布局 */
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        /** 话题列表 */
        val rv = view.findViewById<RecyclerView>(R.id.rv_topics)
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        /** 话题适配器，点击话题跳转到话题详情页 */
        val adapter = TagTopicAdapter { topic ->
            // 从 actionUrl 中提取 tagId，如果没有则使用 topic.id
            val tagId = Regex("tag/(\\d+)").find(topic.actionUrl)?.groupValues?.get(1)?.toIntOrNull()
                ?: topic.id.toInt()
            TopicDetailActivity.start(requireContext(), tagId, topic.title)
        }
        rv.adapter = adapter

        val apiUrl = arguments?.getString(ARG_API_URL) ?: return

        /** 下拉刷新监听器 */
        swipeRefresh.setOnRefreshListener { loadContent(apiUrl, adapter, swipeRefresh) }
        // 首次加载数据
        loadContent(apiUrl, adapter, swipeRefresh)
    }

    /**
     * 加载话题列表数据
     * @param apiUrl API 地址
     * @param adapter 话题适配器
     * @param swipeRefresh 下拉刷新布局
     */
    private fun loadContent(
        apiUrl: String,
        adapter: TagTopicAdapter,
        swipeRefresh: SwipeRefreshLayout
    ) {
        swipeRefresh.isRefreshing = true
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTabDetailByUrl(apiUrl).execute()
                }
                val items = response.body()?.itemList ?: emptyList()
                Log.d("TopicListFrag", "API returned ${items.size} items, first type=${items.firstOrNull()?.type}")
                // 解析 briefCard 类型的数据为 TagTopicItem
                val topics = items.mapNotNull { item ->
                    if (item.type != "briefCard") return@mapNotNull null
                    val data = item.data as? Map<*, *> ?: return@mapNotNull null
                    val id = (data["id"] as? Double)?.toLong() ?: return@mapNotNull null
                    // 移除标题前的 # 符号
                    val title = (data["title"] as? String)?.removePrefix("#") ?: return@mapNotNull null
                    val description = data["description"] as? String ?: ""
                    val icon = data["icon"] as? String ?: ""
                    val actionUrl = data["actionUrl"] as? String ?: ""
                    TagTopicItem(id, title, description, icon, actionUrl)
                }
                Log.d("TopicListFrag", "Parsed ${topics.size} topics")
                adapter.submitList(topics)
            } catch (e: Exception) {
                Log.e("TopicListFrag", "loadContent failed", e)
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }
}

/**
 * 话题标签数据项
 * @param id 话题 ID
 * @param title 话题标题
 * @param description 话题描述
 * @param icon 话题图标 URL
 * @param actionUrl 点击跳转的 URL
 */
data class TagTopicItem(
    val id: Long,
    val title: String,
    val description: String,
    val icon: String,
    val actionUrl: String = ""
)
