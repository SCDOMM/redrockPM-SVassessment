package com.example.core.media

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.OnBackPressedCallback;
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.GSYVideoManager.backFromWindowFull
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 视频播放 Fragment，包含播放器、视频信息展示、相关推荐列表
 * email : 3014386984@qq.com
 * date : 2026/7/15 14:46
 */
class VideoPlayerFragment : Fragment() {

    private lateinit var videoPlayer: StandardGSYVideoPlayer
    private lateinit var scrollView: NestedScrollView
    private lateinit var tvTitle: TextView
    private lateinit var ivAuthor: ImageView
    private lateinit var tvAuthorName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var rvRelated: RecyclerView
    private var orientationUtils: OrientationUtils? = null

    private val api = RetrofitClient.create<KaiyanApi>()
    private lateinit var relatedAdapter: RelatedVideoAdapter
    private var isPlay: Boolean = false

    private var videoId: Long = 0
    private var videoUrl: String = ""
    private var videoTitle: String = ""
    private var videoCover: String = ""
    private var authorName: String = ""
    private var authorIcon: String = ""
    private var category: String = ""
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getLong(ARG_VIDEO_ID, 0)
            videoUrl = it.getString(ARG_VIDEO_URL, "")
            videoTitle = it.getString(ARG_VIDEO_TITLE, "")
            videoCover = it.getString(ARG_VIDEO_COVER, "")
            authorName = it.getString(ARG_AUTHOR_NAME, "")
            authorIcon = it.getString(ARG_AUTHOR_ICON, "")
            category = it.getString(ARG_CATEGORY, "")
            description = it.getString(ARG_DESCRIPTION, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayer = view.findViewById(R.id.video_player)
        tvTitle = view.findViewById(R.id.tv_title)
        ivAuthor = view.findViewById(R.id.iv_author)
        tvAuthorName = view.findViewById(R.id.tv_author_name)
        tvCategory = view.findViewById(R.id.tv_category)
        tvDescription = view.findViewById(R.id.tv_description)
        rvRelated = view.findViewById(R.id.rv_related)
        scrollView = view.findViewById(R.id.nsv_media_detail)

        bindData()
        initRelatedVideos()
        loadRelatedVideos()
        resolveAndPlay()
    }

    //异步解析重定向 URL 后初始化播放器
    private fun resolveAndPlay() {
        viewLifecycleOwner.lifecycleScope.launch {
            val realUrl = withContext(Dispatchers.IO) {
                RetrofitClient.resolvePlayUrl(videoUrl)
            }
            initVideoPlayer(realUrl)
        }
    }

    //配置 GSYVideoPlayer 参数并开始播放
    private fun initVideoPlayer(realUrl: String) {

        GSYVideoOptionBuilder()
            .setUrl(realUrl)
            .setVideoTitle(videoTitle)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(false)
            .setCacheWithPlay(false)
            .setStartAfterPrepared(true)
            .setNeedOrientationUtils(false)
            .setVideoAllCallBack(object : GSYSampleCallBack() {


                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    // 开始播放了才能旋转和全屏
                    orientationUtils?.setEnable(true)
                    isPlay = true
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    Log.e("VideoFragment",
                        "***** onQuitFullscreen **** ${objects[0]}") // title
                    Log.e(
                        "VideoFragment",
                        "***** onQuitFullscreen **** ${objects[1]}"
                    ) // 当前非全屏player
                    orientationUtils?.backToProtVideo()
                }
            })
            .setLockClickListener { view, lock ->
                // 配合下方的 onConfigurationChanged
                orientationUtils?.setEnable(!lock)
            }
            .build(videoPlayer)

        videoPlayer.setBackFromFullScreenListener {

            backFromWindowFull(requireActivity())
        }


        orientationUtils = OrientationUtils(requireActivity(), videoPlayer)
        videoPlayer.fullscreenButton.setOnClickListener {

            orientationUtils?.resolveByClick()
            videoPlayer.startWindowFullscreen(requireActivity(), true, true)

        }


    }


    //初始化相关推荐列表及点击跳转逻辑
    private fun initRelatedVideos() {
        relatedAdapter = RelatedVideoAdapter { item ->
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, item.id)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, item.playUrl)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, item.title)
                putExtra(VideoPlayerActivity.EXTRA_VIDEO_COVER, item.coverUrl)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_NAME, item.authorName)
                putExtra(VideoPlayerActivity.EXTRA_AUTHOR_ICON, item.authorIcon)
                putExtra(VideoPlayerActivity.EXTRA_CATEGORY, item.category)
                putExtra(VideoPlayerActivity.EXTRA_DESCRIPTION, item.description)
            }
            startActivity(intent)
        }
        rvRelated.layoutManager = LinearLayoutManager(requireContext())
        rvRelated.adapter = relatedAdapter
    }

    //加载相关推荐视频数据
    private fun loadRelatedVideos() {
        if (videoId == 0L) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.getVideoRelated(videoId)
                relatedAdapter.submitList(relatedAdapter.parseItems(response.itemList))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //绑定视频标题、作者、分类、描述到 UI
    private fun bindData() {
        tvTitle.text = videoTitle
        tvAuthorName.text = authorName
        tvCategory.text = if (category.isNotEmpty()) "#$category" else ""
        tvDescription.text = description

        if (authorIcon.isNotEmpty()) {
            Glide.with(this)
                .load(authorIcon)
                .transform(CircleCrop())
                .into(ivAuthor)
        }
    }


    override fun onPause() {
        videoPlayer.currentPlayer.onVideoPause()
        super.onPause()
    }

    override fun onResume() {
        videoPlayer.currentPlayer.onVideoResume(false)
        super.onResume()
    }

    override fun onDestroyView() {
        orientationUtils?.releaseListener()
        orientationUtils = null
        GSYVideoManager.releaseAllVideos()
        super.onDestroyView()
    }



    companion object {
        private const val ARG_VIDEO_ID = "video_id"
        private const val ARG_VIDEO_URL = "video_url"
        private const val ARG_VIDEO_TITLE = "video_title"
        private const val ARG_VIDEO_COVER = "video_cover"
        private const val ARG_AUTHOR_NAME = "author_name"
        private const val ARG_AUTHOR_ICON = "author_icon"
        private const val ARG_CATEGORY = "category"
        private const val ARG_DESCRIPTION = "description"

        //创建 Fragment 实例并传入视频参数
        @JvmStatic
        fun newInstance(
            videoId: Long,
            videoUrl: String,
            videoTitle: String,
            videoCover: String,
            authorName: String,
            authorIcon: String,
            category: String,
            description: String
        ) = VideoPlayerFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_VIDEO_ID, videoId)
                putString(ARG_VIDEO_URL, videoUrl)
                putString(ARG_VIDEO_TITLE, videoTitle)
                putString(ARG_VIDEO_COVER, videoCover)
                putString(ARG_AUTHOR_NAME, authorName)
                putString(ARG_AUTHOR_ICON, authorIcon)
                putString(ARG_CATEGORY, category)
                putString(ARG_DESCRIPTION, description)
            }
        }
    }
}
