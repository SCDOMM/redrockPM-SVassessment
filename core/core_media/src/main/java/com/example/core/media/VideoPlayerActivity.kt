package com.example.core.media

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.core.network.RetrofitClient
import com.example.core.network.api.KaiyanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description ： 视频播放页 Activity，通过 videoId 从 API 获取所有数据
 * email : 3014386984@qq.com
 * date : 2026/7/22 15:00
 */
class VideoPlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VIDEO_ID = "video_id"
        private const val EXTRA_RESOURCE_TYPE = "resource_type"

        fun start(context: Context, videoId: String, resourceType: String = "pgc_video") {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_ID, videoId)
                putExtra(EXTRA_RESOURCE_TYPE, resourceType)
            }
            context.startActivity(intent)
        }
    }

    private val api = RetrofitClient.create<KaiyanApi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        setupStatusBar()
        setContentView(R.layout.activity_video_player)

        val root = findViewById<View>(R.id.fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        val resourceType = intent.getStringExtra(EXTRA_RESOURCE_TYPE) ?: "pgc_video"

        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频ID无效", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        api.getItemDetail(videoId, resourceType).execute()
                    }
                    val body = response.body()
                    if (body?.code == 0) {
                        val detail = body.result
                        if (detail != null) {
                            // 清理 play_url 中的转义字符
                            val rawPlayUrl = detail.video?.play_url ?: ""
                            val cleanPlayUrl = rawPlayUrl
                                .replace("\\u003d", "=")
                                .replace("\\u0026", "&")

                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    VideoPlayerFragment.newInstance(
                                        videoId = videoId.toLongOrNull() ?: 0L,
                                        videoUrl = cleanPlayUrl,
                                        videoTitle = detail.video?.title ?: "",
                                        videoCover = detail.video?.cover?.url ?: "",
                                        authorName = detail.author?.nick ?: "",
                                        authorIcon = detail.author?.avatar?.url ?: "",
                                        category = detail.category?.name ?: "",
                                        description = detail.text,
                                        collectionCount = detail.consumption?.collection_count ?: 0,
                                        replyCount = detail.consumption?.comment_count ?: 0,
                                        playUrl = ""
                                    )
                                )
                                .commit()
                        } else {
                            Toast.makeText(this@VideoPlayerActivity, "加载视频详情失败", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@VideoPlayerActivity, "加载视频详情失败", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@VideoPlayerActivity, "网络错误: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupStatusBar() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkMode
    }
}
