package com.example.core.media

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.core.network.RetrofitClient
import com.example.core.network.api.SpecficApi
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.GSYVideoManager.backFromWindowFull
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 视频播放 Fragment，包含播放器、视频信息展示、相关推荐列表
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class VideoPlayerFragment : Fragment() {

    /** 视频播放器 */
    private lateinit var videoPlayer: StandardGSYVideoPlayer
    /** 嵌套滚动视图 */
    private lateinit var scrollView: NestedScrollView
    /** 视频标题 */
    private lateinit var tvTitle: TextView
    /** 作者头像 */
    private lateinit var ivAuthor: ImageView
    /** 作者名称 */
    private lateinit var tvAuthorName: TextView
    /** 视频分类 */
    private lateinit var tvCategory: TextView
    /** 视频描述 */
    private lateinit var tvDescription: TextView
    /** 收藏数量 */
    private lateinit var tvCollectionCount: TextView
    /** 评论数量 */
    private lateinit var tvReplyCount: TextView
    /** 分享按钮 */
    private lateinit var ivShare: ImageView
    /** 相关推荐列表 */
    private lateinit var rvRelated: RecyclerView
    /** 屏幕方向工具类 */
    private var orientationUtils: OrientationUtils? = null

    private val api = RetrofitClient.create<SpecficApi>()
    private lateinit var relatedAdapter: RelatedVideoAdapter
    private var isPlay: Boolean = false

    /** 视频 ID */
    private var videoId: Long = 0
    /** 视频播放地址 */
    private var videoUrl: String = ""
    /** 视频标题 */
    private var videoTitle: String = ""
    /** 视频封面图 URL */
    private var videoCover: String = ""
    /** 作者名称 */
    private var authorName: String = ""
    /** 作者头像 URL */
    private var authorIcon: String = ""
    /** 视频分类 */
    private var category: String = ""
    /** 视频描述 */
    private var description: String = ""
    /** 收藏数量 */
    private var collectionCount: Int = 0
    /** 评论数量 */
    private var replyCount: Int = 0
    /** 视频播放地址（用于分享） */
    private var playUrl: String = ""

    /** Fragment 创建时从参数中提取视频数据 */
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
            collectionCount = it.getInt(ARG_COLLECTION_COUNT, 0)
            replyCount = it.getInt(ARG_REPLY_COUNT, 0)
            playUrl = it.getString(ARG_PLAY_URL, "")
        }
    }

    /** 加载 Fragment 布局 */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_player, container, false)
    }

    /** 初始化视图组件，绑定数据并加载相关推荐 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayer = view.findViewById(R.id.video_player)
        tvTitle = view.findViewById(R.id.tv_title)
        ivAuthor = view.findViewById(R.id.iv_author)
        tvAuthorName = view.findViewById(R.id.tv_author_name)
        tvCategory = view.findViewById(R.id.tv_category)
        tvDescription = view.findViewById(R.id.tv_description)
        tvCollectionCount = view.findViewById(R.id.tv_collection_count)
        tvReplyCount = view.findViewById(R.id.tv_reply_count)
        ivShare = view.findViewById(R.id.iv_share)
        rvRelated = view.findViewById(R.id.rv_related)
        scrollView = view.findViewById(R.id.nsv_media_detail)

        bindData()
        initRelatedVideos()
        loadRelatedVideos()
        resolveAndPlay()
    }

    /**
     * 异步解析 playUrl 302 重定向后初始化播放器
     */
    private fun resolveAndPlay() {
        viewLifecycleOwner.lifecycleScope.launch {
            val realUrl = withContext(Dispatchers.IO) {
                RetrofitClient.resolvePlayUrl(videoUrl)
            }
            initVideoPlayer(realUrl)
        }
    }

    /**
     * 配置 GSYVideoPlayer 参数并开始播放
     */
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
                orientationUtils?.setEnable(!lock)
            }
            .build(videoPlayer)
        //全屏时返回键
        videoPlayer.setBackFromFullScreenListener {

            backFromWindowFull(requireActivity())
        }



        orientationUtils = OrientationUtils(requireActivity(), videoPlayer)
        videoPlayer.fullscreenButton.setOnClickListener {

            orientationUtils?.resolveByClick()
            videoPlayer.startWindowFullscreen(requireActivity(), true, true)

        }
        //非全屏返回键
        videoPlayer.backButton.setOnClickListener {
            if(!videoPlayer.isIfCurrentIsFullscreen() ){
                requireActivity().finish()
            }
        }


    }


    /**
     * 初始化相关推荐列表及点击跳转逻辑
     */
    private fun initRelatedVideos() {
        relatedAdapter = RelatedVideoAdapter { item ->
            // 只传 videoId，Activity 会从 API 获取所有数据
            val videoId = item.id.toString()
            if (videoId != "0") {
                VideoPlayerActivity.start(requireContext(), videoId)
            }
        }
        rvRelated.layoutManager = LinearLayoutManager(requireContext())
        rvRelated.adapter = relatedAdapter
    }

    /**
     * 加载相关推荐视频数据
     */
    private fun loadRelatedVideos() {
        if (videoId == 0L) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getRelatedRecommend(videoId.toString()).execute()
                }
                val body = response.body()
                if (body?.code == 0) {
                    val result = body.result
                    if (result != null) {
                        val items = result.item_list.mapNotNull { item ->
                        val video = item.video ?: return@mapNotNull null
                        val title = video.title
                        if (title.isEmpty()) return@mapNotNull null

                        val coverUrl = video.cover?.url ?: ""
                        val videoIdVal = video.video_id.toLongOrNull() ?: 0L
                        val duration = video.duration?.value ?: 0L
                        val playUrl = video.play_url
                            .replace("\\u003d", "=")
                            .replace("\\u0026", "&")
                        val authorName = item.author?.nick ?: ""
                        val authorIcon = item.author?.avatar?.url ?: ""
                        val category = item.category?.name ?: ""
                        val consumption = item.consumption

                        RelatedVideoAdapter.RelatedVideoItem(
                            id = videoIdVal,
                            title = title,
                            coverUrl = coverUrl,
                            duration = duration,
                            authorName = authorName,
                            authorIcon = authorIcon,
                            category = category,
                            description = item.text,
                            playUrl = playUrl
                        )
                    }
                    relatedAdapter.submitList(items)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 绑定视频标题、作者、分类、描述到 UI
     */
    private fun bindData() {
        tvTitle.text = videoTitle
        tvAuthorName.text = authorName
        tvCategory.text = if (category.isNotEmpty()) "#$category" else ""
        tvDescription.text = description
        tvCollectionCount.text = formatCount(collectionCount)
        tvReplyCount.text = formatCount(replyCount)

        if (authorIcon.isNotEmpty()) {
            Glide.with(this)
                .load(authorIcon)
                .transform(CircleCrop())
                .into(ivAuthor)
        }

        // 分享按钮
        val shareUrl = playUrl.ifEmpty { videoUrl }
        ivShare.setOnClickListener {
            if (shareUrl.isNotEmpty()) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareUrl)
                }
                startActivity(Intent.createChooser(shareIntent, "分享视频"))
            }
        }
    }

    /** 格式化数量显示，超过1万显示为 "x.x万" */
    private fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }


    /** 暂停视频播放 */
    override fun onPause() {
        videoPlayer.currentPlayer.onVideoPause()
        super.onPause()
    }

    /** 恢复视频播放 */
    override fun onResume() {
        videoPlayer.currentPlayer.onVideoResume(false)
        super.onResume()
    }

    /** 释放播放器资源 */
    override fun onDestroyView() {
        orientationUtils?.releaseListener()
        orientationUtils = null
        GSYVideoManager.releaseAllVideos()
        super.onDestroyView()
    }



    /** Fragment 参数常量 */
    companion object {
        /** 视频 ID 参数键名 */
        private const val ARG_VIDEO_ID = "video_id"
        /** 视频播放地址参数键名 */
        private const val ARG_VIDEO_URL = "video_url"
        /** 视频标题参数键名 */
        private const val ARG_VIDEO_TITLE = "video_title"
        /** 视频封面图参数键名 */
        private const val ARG_VIDEO_COVER = "video_cover"
        /** 作者名称参数键名 */
        private const val ARG_AUTHOR_NAME = "author_name"
        /** 作者头像参数键名 */
        private const val ARG_AUTHOR_ICON = "author_icon"
        /** 视频分类参数键名 */
        private const val ARG_CATEGORY = "category"
        /** 视频描述参数键名 */
        private const val ARG_DESCRIPTION = "description"
        /** 收藏数参数键名 */
        private const val ARG_COLLECTION_COUNT = "collection_count"
        /** 回复数参数键名 */
        private const val ARG_REPLY_COUNT = "reply_count"
        /** 视频播放地址参数键名（用于分享） */
        private const val ARG_PLAY_URL = "play_url"

        /**
         * 创建 Fragment 实例并传入视频参数
         */
        @JvmStatic
        fun newInstance(
            videoId: Long,
            videoUrl: String,
            videoTitle: String,
            videoCover: String,
            authorName: String,
            authorIcon: String,
            category: String,
            description: String,
            collectionCount: Int = 0,
            replyCount: Int = 0,
            playUrl: String = ""
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
                putInt(ARG_COLLECTION_COUNT, collectionCount)
                putInt(ARG_REPLY_COUNT, replyCount)
                putString(ARG_PLAY_URL, playUrl)
            }
        }
    }
}
