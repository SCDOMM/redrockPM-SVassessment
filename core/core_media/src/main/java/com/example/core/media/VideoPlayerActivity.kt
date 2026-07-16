package com.example.core.media

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core.media.R

/**
 * description ： 视频播放页 Activity，初始化 Edge-to-Edge 并加载 VideoPlayerFragment
 * email : 3014386984@qq.com
 * date : 2026/7/15 15:00
 */
class VideoPlayerActivity : AppCompatActivity() {

    companion object {
        /** 视频 ID */
        const val EXTRA_VIDEO_ID = "video_id"
        /** 视频播放地址 */
        const val EXTRA_VIDEO_URL = "video_url"
        /** 视频标题 */
        const val EXTRA_VIDEO_TITLE = "video_title"
        /** 视频封面图 */
        const val EXTRA_VIDEO_COVER = "video_cover"
        /** 作者名称 */
        const val EXTRA_AUTHOR_NAME = "author_name"
        /** 作者头像 */
        const val EXTRA_AUTHOR_ICON = "author_icon"
        /** 视频分类 */
        const val EXTRA_CATEGORY = "category"
        /** 视频描述 */
        const val EXTRA_DESCRIPTION = "description"
    }

    /**
     * 初始化窗口适配、解析 Intent 参数、加载 VideoPlayerFragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        setContentView(R.layout.activity_video_player)

        val root = findViewById<View>(R.id.fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoId = intent.getLongExtra(EXTRA_VIDEO_ID, 0)
        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: ""
        val videoCover = intent.getStringExtra(EXTRA_VIDEO_COVER) ?: ""
        val authorName = intent.getStringExtra(EXTRA_AUTHOR_NAME) ?: ""
        val authorIcon = intent.getStringExtra(EXTRA_AUTHOR_ICON) ?: ""
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: ""

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    VideoPlayerFragment.newInstance(
                        videoId, videoUrl, videoTitle, videoCover,
                        authorName, authorIcon, category, description
                    )
                )
                .commit()
        }
    }
}
