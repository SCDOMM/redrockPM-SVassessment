package com.example.ept.hot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.core.model.Item
import com.example.ept.hot.R

data class VideoItem(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String,
    val category: String = ""
)

class HotVideoAdapter(
    private val onItemClick: (VideoItem) -> Unit = {}
) : ListAdapter<VideoItem, HotVideoAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(old: VideoItem, new: VideoItem) = old.id == new.id
            override fun areContentsTheSame(old: VideoItem, new: VideoItem) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hot, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun parseItems(rawItems: List<Item>): List<VideoItem> {
        return rawItems.mapNotNull { parseVideoItem(it) }
    }

    private fun parseVideoItem(item: Item): VideoItem? {
        return try {
            when (item.type) {
                "video", "videoSmallCard" -> parseVideoData(item.data)
                "followCard" -> {
                    val data = item.data as? Map<*, *> ?: return null
                    val content = data["content"] as? Map<*, *> ?: return null
                    val contentData = content["data"] as? Map<*, *> ?: return null
                    parseVideoData(contentData)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseVideoData(data: Any?): VideoItem? {
        val map = data as? Map<*, *> ?: return null

        val id = when (val v = map["id"]) {
            is Number -> v.toLong()
            is String -> v.toLongOrNull() ?: 0L
            else -> 0L
        }
        val title = map["title"] as? String ?: ""
        if (title.isEmpty()) return null

        val coverMap = map["cover"] as? Map<*, *>
        val coverUrl = coverMap?.get("feed") as? String ?: ""

        val duration = when (val v = map["duration"]) {
            is Number -> v.toLong()
            is Map<*, *> -> (v["value"] as? Number)?.toLong() ?: 0L
            else -> 0L
        }

        val authorMap = map["author"] as? Map<*, *>
        val authorName = authorMap?.get("name") as? String ?: ""
        val authorIcon = authorMap?.get("icon") as? String ?: ""

        val category = map["category"] as? String ?: ""

        return VideoItem(
            id = id,
            title = title,
            coverUrl = coverUrl,
            duration = duration,
            authorName = authorName,
            authorIcon = authorIcon,
            category = category
        )
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_hot_title)
        private val tvDuration: TextView = itemView.findViewById(R.id.actv_hot_geration)
        private val ivAuthor: ImageView = itemView.findViewById(R.id.iv_hot_author)
        private val tvTitle: TextView = itemView.findViewById(R.id.actv_hot_description)
        private val tvAuthor: TextView = itemView.findViewById(R.id.actv_hot_author)

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

        private fun formatDuration(seconds: Long): String {
            val mins = seconds / 60
            val secs = seconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
    }
}
