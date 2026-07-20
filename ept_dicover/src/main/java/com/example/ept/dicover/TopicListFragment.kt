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
class TopicListFragment : Fragment() {

    companion object {
        private const val ARG_API_URL = "api_url"

        fun newInstance(apiUrl: String): TopicListFragment {
            return TopicListFragment().apply {
                arguments = Bundle().apply { putString(ARG_API_URL, apiUrl) }
            }
        }
    }

    private val api = RetrofitClient.create<KaiyanApi>()
    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rv = view.findViewById<RecyclerView>(R.id.rv_topics)
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        val adapter = TagTopicAdapter { topic ->
            val tagId = Regex("tag/(\\d+)").find(topic.actionUrl)?.groupValues?.get(1)?.toIntOrNull()
                ?: topic.id.toInt()
            TopicDetailActivity.start(requireContext(), tagId, topic.title)
        }
        rv.adapter = adapter

        val apiUrl = arguments?.getString(ARG_API_URL) ?: return

        swipeRefresh.setOnRefreshListener { loadContent(apiUrl, adapter, swipeRefresh) }
        loadContent(apiUrl, adapter, swipeRefresh)
    }

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
                val topics = items.mapNotNull { item ->
                    if (item.type != "briefCard") return@mapNotNull null
                    val data = item.data as? Map<*, *> ?: return@mapNotNull null
                    val id = (data["id"] as? Double)?.toLong() ?: return@mapNotNull null
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

data class TagTopicItem(
    val id: Long,
    val title: String,
    val description: String,
    val icon: String,
    val actionUrl: String = ""
)
