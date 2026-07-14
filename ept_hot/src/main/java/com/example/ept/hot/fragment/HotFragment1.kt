package com.example.ept.hot.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ept.hot.databinding.FragmentHotBinding
import kotlinx.coroutines.launch

class HotFragment1 : Fragment() {

    private var _binding: FragmentHotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HotViewModel by viewModels()

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
    ): View {
        _binding = FragmentHotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        loadVideos()
    }

    private fun setupRecyclerView() {
        binding.mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadHotVideosByUrl(apiUrl)
        }
    }

    private fun observeData() {
        viewModel.hotList.observe(viewLifecycleOwner) { list ->
            // TODO: 设置适配器数据
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.mSwipeRefreshLayout.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
