package com.example.ept.hot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.core.model.Item
import com.example.core.model.videoEntity.VideoData
import com.example.core.model.videoEntity.VideoSmallCardData
import com.example.ept.hot.R
import com.google.gson.Gson

/**
 * description ： 视频列表项数据模型
 * email : 3014386984@qq.com
 * date : 2026/7/15 15:23
 */
data class VideoItem(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String,
    val category: String = "",
    val playUrl: String = "",
    val description: String = ""
)

/**
 * description ： 热门视频列表适配器，负责视频卡片展示和点击事件
 * email : 3014386984@qq.com
 * date : 2026/7/15 15:23
 */
class HotVideoAdapter(
    private val onItemClick: (VideoItem) -> Unit = {}
) : RecyclerView.Adapter<HotVideoAdapter.VideoViewHolder>() {

    private var items: List<VideoItem> = emptyList()
    private val gson = Gson()

    /**
     * 提交数据列表并刷新
     */
    fun submitList(newItems: List<VideoItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hot, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /**
     * 将Item列表解析为VideoItem
     */
    fun parseItems(rawItems: List<Item>): List<VideoItem> {
        return rawItems.mapNotNull { item ->
            parseVideoItem(item)
        }
    }

    /**
     * 根据item.type解析Video、videoSmallCard或followCard类型数据
     */
    private fun parseVideoItem(item: Item): VideoItem? {
        return try {
            when (item.type) {
                "video" -> {
                    val json = gson.toJson(item.data)
                    val video = gson.fromJson(json, VideoData::class.java)
                    VideoItem(
                        id = video.id,
                        title = video.title,
                        coverUrl = video.cover.feed,
                        duration = video.duration,
                        authorName = video.author?.name ?: "",
                        authorIcon = video.author?.icon ?: "",
                        category = video.category,
                        playUrl = video.playUrl,
                        description = video.description
                    )
                }
                "videoSmallCard" -> {
                    val json = gson.toJson(item.data)
                    val card = gson.fromJson(json, VideoSmallCardData::class.java)
                    VideoItem(
                        id = card.id,
                        title = card.title,
                        coverUrl = card.cover.feed,
                        duration = card.duration,
                        authorName = card.author?.name ?: "",
                        authorIcon = card.author?.icon ?: "",
                        category = card.category ?: "",
                        playUrl = card.playUrl ?: "",
                        description = card.description ?: ""
                    )
                }
                "followCard" -> {
                    // followCard.content is an Item, not VideoData — parse manually
                    val data = item.data as? Map<*, *> ?: return null
                    val content = data["content"] as? Map<*, *> ?: return null
                    val contentData = content["data"] as? Map<*, *> ?: return null
                    val videoJson = gson.toJson(contentData)
                    val video = gson.fromJson(videoJson, VideoData::class.java)
                    VideoItem(
                        id = video.id,
                        title = video.title,
                        coverUrl = video.cover.feed,
                        duration = video.duration,
                        authorName = video.author?.name ?: "",
                        authorIcon = video.author?.icon ?: "",
                        category = video.category,
                        playUrl = video.playUrl,
                        description = video.description
                    )
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 视频卡片 ViewHolder，绑定封面图、时长、作者、标题到 itemView
     */
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_hot_title)
        private val tvDuration: TextView = itemView.findViewById(R.id.actv_hot_geration)
        private val ivAuthor: ImageView = itemView.findViewById(R.id.iv_hot_author)
        private val tvTitle: TextView = itemView.findViewById(R.id.actv_hot_description)
        private val tvAuthor: TextView = itemView.findViewById(R.id.actv_hot_author)

        /**
         * 绑定封面图、时长、作者、标题到 itemView
         */
        fun bind(item: VideoItem) {
            Glide.with(itemView.context)
                .load(item.coverUrl)
                .transform(CenterCrop(), RoundedCorners(20))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivCover)

            tvDuration.text = formatDuration(item.duration)

            if (item.authorIcon.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.authorIcon)
                    .circleCrop()
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivAuthor)
            }
            tvAuthor.text = if (item.category.isNotEmpty()) {
                "${item.authorName} #${item.category}"
            } else {
                item.authorName
            }

            tvTitle.text = item.title

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        /**
         * 将秒数格式化为 mm:ss
         */
        private fun formatDuration(seconds: Long): String {
            val mins = seconds / 60
            val secs = seconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
    }
}
