package com.example.ept.community

import android.content.Intent
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

/**
 * description ： 社区页 Fragment
 * email : 3014386984@qq.com
 * date : 2026/7/17 20:23
 */
class CommunityFragment : Fragment() {

    /** 社区页 ViewModel，管理数据加载和分页逻辑 */
    private val viewModel: CommunityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** 下拉刷新布局，用于触发刷新操作和显示加载状态 */
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        /** 社区内容列表，展示视频和图片卡片 */
        val rvCommunity = view.findViewById<RecyclerView>(R.id.rv_community)

        /** 线性布局管理器，垂直排列列表项 */
        val layoutManager = LinearLayoutManager(requireContext())
        rvCommunity.layoutManager = layoutManager

        /** 社区适配器，处理卡片点击事件并跳转到视频播放页 */
        val adapter = CommunityAdapter { video ->
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, video.id)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, video.playUrl)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.nickname)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_COVER, video.coverUrl)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_NAME, video.nickname)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_ICON, video.avatar)
                putExtra(VideoPlayerActivity.EXTRA_CATEGORY, "")
                putExtra(VideoPlayerActivity.EXTRA_DESCRIPTION, video.description)
                putExtra(VideoPlayerActivity.EXTRA_COLLECTION_COUNT, video.collectionCount)
                putExtra(VideoPlayerActivity.EXTRA_REPLY_COUNT, video.replyCount)
                putExtra(VideoPlayerActivity.EXTRA_PLAY_URL, video.playUrl)
            }
            startActivity(intent)
        }
        rvCommunity.adapter = adapter

        /** 观察数据变化，更新列表显示 */
        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        /** 观察加载状态，控制下拉刷新动画 */
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        /** 观察错误信息，显示 Toast 提示 */
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        /** 下拉刷新监听器，触发数据重新加载 */
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        /** 上拉加载更多监听器，滚动到底部时自动加载下一页 */
        rvCommunity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 向下滚动时检测是否需要加载更多
                if (dy > 0) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val totalItems = layoutManager.itemCount
                    // 距离底部还有3个item时触发加载，且有下一页且不在加载中
                    if (lastVisible >= totalItems - 3 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        /** 首次加载数据，防止返回时重复加载 */
        if (!viewModel.loaded) {
            viewModel.refresh()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CommunityFragment()
    }
}
