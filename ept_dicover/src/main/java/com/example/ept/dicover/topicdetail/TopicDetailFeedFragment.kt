package com.example.ept.dicover.topicdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.example.ept.dicover.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 话题详情页 Feed Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailFeedFragment : Fragment() {

    companion object {
        /** 页面标签参数键 */
        private const val ARG_PAGE_LABEL = "page_label"

        /** 创建实例并传递页面标签 */
        fun newInstance(pageLabel: String): TopicDetailFeedFragment {
            val fragment = TopicDetailFeedFragment()
            val args = Bundle()
            args.putString(ARG_PAGE_LABEL, pageLabel)
            fragment.arguments = args
            return fragment
        }
    }

    private val api = RetrofitClient.create<KaiyanApi>()
    private var pageLabel = ""
    private var adapter: TopicDetailFeedAdapter? = null
    private var swipeRefresh: SwipeRefreshLayout? = null

    /** 获取页面标签参数 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageLabel = arguments?.getString(ARG_PAGE_LABEL) ?: ""
    }

    /** 创建布局视图 */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_detail_feed, container, false)
    }

    /** 初始化视图组件并加载数据 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        val rvFeed = view.findViewById<RecyclerView>(R.id.rv_feed)

        adapter = TopicDetailFeedAdapter()
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        rvFeed.adapter = adapter

        swipeRefresh?.setOnRefreshListener {
            loadFeed()
        }

        loadFeed()
    }

    /** 刷新 Feed 数据 */
    fun refresh() {
        loadFeed()
    }

    /** 加载 Feed 列表数据 */
    private fun loadFeed() {
        swipeRefresh?.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getPage(pageLabel = pageLabel).execute()
                val body = response.body()

                val items = mutableListOf<TopicFeedItem>()

                if (body?.code == 0 && body.result != null) {
                    val result = body.result!!
                    for (card in result.card_list) {
                        if (card.type != "set_metro_list") continue
                        val metroList = card.card_data?.body?.metro_list ?: continue

                        for (metro in metroList) {
                            if (metro.type != "item") continue
                            val data = metro.metro_data ?: continue

                            val itemId = data.item_id.toLongOrNull() ?: 0L
                            if (itemId == 0L) continue

                            // 判断是图片还是视频
                            val isVideo = data.video?.video_id?.isNotEmpty() == true
                            val coverUrl = if (isVideo) {
                                data.video?.cover?.url ?: ""
                            } else {
                                data.images?.firstOrNull()?.cover?.url ?: ""
                            }

                            val authorName = data.author?.nick ?: ""
                            val authorAvatar = data.author?.avatar?.url ?: ""
                            val text = data.text ?: ""
                            val likeCount = data.consumption?.like_count ?: 0
                            val collectionCount = data.consumption?.collection_count ?: 0
                            val commentCount = data.consumption?.comment_count ?: 0

                            items.add(
                                TopicFeedItem(
                                    id = itemId,
                                    text = text,
                                    coverUrl = coverUrl,
                                    isVideo = isVideo,
                                    authorName = authorName,
                                    authorAvatar = authorAvatar,
                                    likeCount = likeCount,
                                    collectionCount = collectionCount,
                                    commentCount = commentCount,
                                    publishTime = data.publish_time ?: ""
                                )
                            )
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    swipeRefresh?.isRefreshing = false
                    adapter?.submitList(items)
                    Log.d("TopicDetailFeed", "Loaded ${items.size} items for $pageLabel")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefresh?.isRefreshing = false
                    Toast.makeText(requireContext(), "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("TopicDetailFeed", "loadFeed failed", e)
            }
        }
    }
}
