package com.example.core.media

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RelatedVideoAdapter(
    private val onItemClick: (RelatedVideoItem) -> Unit = {}
) : ListAdapter<RelatedVideoAdapter.RelatedVideoItem, RelatedVideoAdapter.VideoViewHolder>(DIFF_CALLBACK) {

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

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RelatedVideoItem>() {
            override fun areItemsTheSame(old: RelatedVideoItem, new: RelatedVideoItem) = old.id == new.id
            override fun areContentsTheSame(old: RelatedVideoItem, new: RelatedVideoItem) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)

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

        private fun formatDuration(seconds: Long): String {
            val mins = seconds / 60
            val secs = seconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
    }
}
