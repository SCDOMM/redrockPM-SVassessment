package com.example.core.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.core.model.Item
import com.example.core.model.*
import com.google.gson.Gson

/**
 * description ： 相关推荐视频列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/15 13:27
 */
data class RelatedVideoItem(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String = "",
    val category: String = "",
    val description: String = "",
    val playUrl: String = ""
)

/**
 * description ： 相关推荐视频列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/15 13:27
 */
class RelatedVideoAdapter(
    private val onItemClick: (RelatedVideoItem) -> Unit = {}
) : RecyclerView.Adapter<RelatedVideoAdapter.VideoViewHolder>() {

    private var items: List<RelatedVideoItem> = emptyList()
    private val gson = Gson()

    /**
     * 提交数据列表并刷新 RecyclerView
     */
    fun submitList(newItems: List<RelatedVideoItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * 将 Item 列表解析为 RelatedVideoItem 列表
     */
    fun parseItems(rawItems: List<Item>): List<RelatedVideoItem> {
        return rawItems.mapNotNull { item -> parseVideoItem(item) }
    }

    /**
     * 根据 item.type 解析 video 或 videoSmallCard 类型数据
     */
    private fun parseVideoItem(item: Item): RelatedVideoItem? {
        return try {
            when (item.type) {
                "video" -> {
                    val json = gson.toJson(item.data)
                    val video = gson.fromJson(json, VideoData::class.java)
                    RelatedVideoItem(
                        id = video.id,
                        title = video.title,
                        coverUrl = video.cover.feed,
                        duration = video.duration,
                        authorName = video.author?.name ?: "",
                        authorIcon = video.author?.icon ?: "",
                        category = video.category,
                        description = video.description,
                        playUrl = video.playUrl
                    )
                }
                "videoSmallCard" -> {
                    val json = gson.toJson(item.data)
                    val card = gson.fromJson(json, VideoSmallCardData::class.java)
                    RelatedVideoItem(
                        id = card.id,
                        title = card.title,
                        coverUrl = card.cover.feed,
                        duration = card.duration,
                        authorName = card.author?.name ?: "",
                        authorIcon = card.author?.icon ?: "",
                        category = card.category ?: "",
                        description = card.description ?: "",
                        playUrl = card.playUrl ?: ""
                    )
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /**
     * 相关推荐视频 ViewHolder
     */
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)

        /**
         * 绑定封面图、标题、时长、作者到 itemView
         */
        fun bind(item: RelatedVideoItem) {
            Glide.with(itemView.context)
                .load(item.coverUrl)
                .transform(CenterCrop(), RoundedCorners(12))
                .into(ivCover)

            tvTitle.text = item.title
            tvDuration.text = formatDuration(item.duration)
            tvAuthor.text = item.authorName

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
