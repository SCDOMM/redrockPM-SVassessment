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

    private val viewModel: CommunityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rvCommunity = view.findViewById<RecyclerView>(R.id.rv_community)

        val layoutManager = LinearLayoutManager(requireContext())
        rvCommunity.layoutManager = layoutManager

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

        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        // 上拉加载更多
        rvCommunity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val totalItems = layoutManager.itemCount
                    if (lastVisible >= totalItems - 3 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        if (!viewModel.loaded) {
            viewModel.refresh()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CommunityFragment()
    }
}
