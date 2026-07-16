package com.example.ept.category

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

class CategoryDetailFragment : Fragment() {

    private val viewModel: CategoryDetailViewModel by viewModels()

    companion object {
        private const val ARG_API_URL = "api_url"

        fun newInstance(apiUrl: String): CategoryDetailFragment {
            return CategoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_API_URL, apiUrl)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_category_detail)
        val layoutManager = LinearLayoutManager(requireContext())
        rvCategory.layoutManager = layoutManager

        val adapter = CategoryDetailAdapter { video ->
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, video.videoId)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, video.playUrl)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.title)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_COVER, video.coverUrl)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_NAME, video.authorName)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_ICON, video.authorIcon)
                putExtra(VideoPlayerActivity.EXTRA_CATEGORY, video.category)
                putExtra(VideoPlayerActivity.EXTRA_DESCRIPTION, video.description)
            }
            startActivity(intent)
        }
        rvCategory.adapter = adapter

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
            val apiUrl = arguments?.getString(ARG_API_URL) ?: return@setOnRefreshListener
            viewModel.loadCategoryDetail(apiUrl)
        }

        rvCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val totalItems = layoutManager.itemCount
                    if (lastVisible >= totalItems - 5 && viewModel.hasNextPage && viewModel.isLoading.value != true) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        val apiUrl = arguments?.getString(ARG_API_URL) ?: return
        viewModel.loadCategoryDetail(apiUrl)
    }
}
