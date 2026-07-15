package com.example.ept.hot.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ept.hot.R
import com.example.ept.hot.adapter.HotVideoAdapter
import kotlinx.coroutines.launch

class HotFragment1 : Fragment() {

    private val viewModel: HotViewModel by viewModels()
    private lateinit var adapter: HotVideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var apiUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apiUrl = it.getString(ARG_API_URL, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.mRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout)
        setupRecyclerView()
        setupSwipeRefresh()
        observeData()
        loadVideos()
    }

    private fun setupRecyclerView() {
        adapter = HotVideoAdapter { videoItem ->
            Toast.makeText(requireContext(), videoItem.title, Toast.LENGTH_SHORT).show()
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HotFragment1.adapter
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            loadVideos()
        }
    }

    private fun loadVideos() {
        viewModel.loadHotVideosByUrl(apiUrl)
    }

    private fun observeData() {
        viewModel.hotList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(adapter.parseItems(list))
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_API_URL = "api_url"

        @JvmStatic
        fun newInstance(apiUrl: String) =
            HotFragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_API_URL, apiUrl)
                }
            }
    }
}
