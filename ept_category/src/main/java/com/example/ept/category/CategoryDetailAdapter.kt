package com.example.ept.category

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

sealed class CategoryItem {
    data class Header(val text: String) : CategoryItem()
    data class Video(
        val videoId: Long,
        val title: String,
        val coverUrl: String,
        val duration: Long,
        val authorName: String,
        val authorIcon: String,
        val description: String,
        val playUrl: String = "",
        val category: String = "",
        val collectionCount: Int = 0,
        val shareCount: Int = 0,
        val replyCount: Int = 0
    ) : CategoryItem()
}

class CategoryDetailAdapter(
    private val onVideoClick: (CategoryItem.Video) -> Unit = {}
) : ListAdapter<CategoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_VIDEO = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
                return when {
                    old is CategoryItem.Header && new is CategoryItem.Header -> old.text == new.text
                    old is CategoryItem.Video && new is CategoryItem.Video -> old.videoId == new.videoId
                    else -> false
                }
            }

            override fun areContentsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
                return old == new
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tv_header)
    }

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_hot_title)
        val duration: TextView = view.findViewById(R.id.actv_hot_geration)
        val authorIcon: ImageView = view.findViewById(R.id.iv_hot_author)
        val title: TextView = view.findViewById(R.id.actv_hot_description)
        val description: TextView = view.findViewById(R.id.actv_hot_author)
        val collectionCount: TextView = view.findViewById(R.id.tv_category_collection)
        val replyIcon: ImageView = view.findViewById(R.id.iv_category_reply)
        val replyCount: TextView = view.findViewById(R.id.tv_category_reply)
        val shareIcon: ImageView = view.findViewById(R.id.iv_category_share)
        val shareCount: TextView = view.findViewById(R.id.tv_category_share)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryItem.Header -> TYPE_HEADER
            is CategoryItem.Video -> TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_video, parent, false)
                VideoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryItem.Header -> {
                (holder as HeaderViewHolder).text.text = item.text
            }
            is CategoryItem.Video -> {
                val vh = holder as VideoViewHolder
                vh.title.text = item.title
                vh.description.text = item.authorName

                val mins = item.duration / 60
                val secs = item.duration % 60
                vh.duration.text = String.format("%02d:%02d", mins, secs)

                vh.collectionCount.text = formatCount(item.collectionCount)
                vh.replyCount.text = formatCount(item.replyCount)
                vh.shareCount.text = formatCount(item.shareCount)

                Glide.with(vh.itemView)
                    .load(item.coverUrl)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(vh.cover)

                if (item.authorIcon.isNotEmpty()) {
                    Glide.with(vh.itemView)
                        .load(item.authorIcon)
                        .transform(CircleCrop())
                        .into(vh.authorIcon)
                }

                vh.itemView.setOnClickListener { onVideoClick(item) }
            }
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }
}
