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
) : RecyclerView.Adapter<HotVideoAdapter.VideoViewHolder>() {

    private var items: List<VideoItem> = emptyList()
    private val gson = Gson()

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

    fun parseItems(rawItems: List<Item>): List<VideoItem> {
        return rawItems.mapNotNull { item ->
            parseVideoItem(item)
        }
    }

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
                        category = video.category
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
                        category = ""
                    )
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
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
