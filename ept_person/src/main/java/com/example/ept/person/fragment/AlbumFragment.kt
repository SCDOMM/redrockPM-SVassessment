package com.example.ept.person.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.media.VideoPlayerActivity
import com.example.ept.person.R
import com.example.ept.person.adapter.content.AlbumAdapter
import com.example.ept.person.viewmodel.AlbumState
import com.example.ept.person.viewmodel.AlbumViewModel
import com.example.ept.person.viewmodel.CreatorViewModel

class AlbumFragment : Fragment() {
    private lateinit var rvAlbumDefault: RecyclerView
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var creatorViewModel: CreatorViewModel
    private lateinit var tvAlbumTitle: TextView
    private lateinit var adapter: AlbumAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        rvAlbumDefault = view.findViewById(R.id.rv_album_default)
        albumViewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        creatorViewModel = ViewModelProvider(requireActivity())[CreatorViewModel::class.java]
        tvAlbumTitle = view.findViewById(R.id.tv_album_title)
        initEvent()
        initAdd()
        return view
    }

    private fun initEvent() {
        adapter = AlbumAdapter { videoId ->
            VideoPlayerActivity.start(requireContext(), videoId)
        }
        rvAlbumDefault.layoutManager = LinearLayoutManager(requireContext())
        rvAlbumDefault.adapter = adapter
        creatorViewModel.albumLiveData.observe(viewLifecycleOwner) { tab ->
            albumViewModel.initLiveData(tab)
        }
        albumViewModel.liveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AlbumState.FailedState -> {
                    Toast.makeText(requireContext(), state.msg, Toast.LENGTH_SHORT).show()
                    isLoading = false
                }

                is AlbumState.InitState -> {
                    tvAlbumTitle.text = if (state.title == "null") "暂无作品" else state.title
                    adapter.submitList(state.albumList)
                }

                is AlbumState.LoadingState -> {
                    isLoading = false
                    adapter.submitList(state.newAlbumData)
                }

                is AlbumState.RefreshState -> {
                }
            }
        }

    }

    fun initAdd() {
        rvAlbumDefault.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoading) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalCount = layoutManager.itemCount
                if (totalCount <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val preloadThreshold = 5
                val remainingItems = totalCount - lastVisible - 1
                if (remainingItems <= preloadThreshold) {
                    isLoading = true
                    albumViewModel.loadMore()
                }
            }
        })
    }
}
