package com.example.ept.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * description ： 社区页适配器
 * email : 3014386984@qq.com
 * date : 2026/7/17 19:10
 */
class CommunityAdapter(
    private val onVideoClick: (CommunityItem.ContentCard) -> Unit = {}
) : ListAdapter<CommunityItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_HEADER = -1
        private const val TYPE_ENTRY = 0
        private const val TYPE_CONTENT = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommunityItem>() {
            override fun areItemsTheSame(old: CommunityItem, new: CommunityItem): Boolean {
                return when {
                    old is CommunityItem.HeaderCard && new is CommunityItem.HeaderCard -> old.title == new.title
                    old is CommunityItem.EntryCard && new is CommunityItem.EntryCard -> old.title == new.title
                    old is CommunityItem.ContentCard && new is CommunityItem.ContentCard -> old.id == new.id
                    else -> false
                }
            }

            override fun areContentsTheSame(old: CommunityItem, new: CommunityItem): Boolean {
                return old == new
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerImage: ImageView = view.findViewById(R.id.iv_topic_header)
        val title: TextView = view.findViewById(R.id.actv_topic_title)
        val description: TextView = view.findViewById(R.id.actv_topic_desc)
    }

    class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bg: ImageView = view.findViewById(R.id.iv_entry_bg)
        val title: TextView = view.findViewById(R.id.tv_entry_title)
        val subTitle: TextView = view.findViewById(R.id.tv_entry_sub_title)
    }

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flVideo: ConstraintLayout = view.findViewById(R.id.fl_video)
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val duration: TextView = view.findViewById(R.id.tv_duration)
        val collectionCount: TextView = view.findViewById(R.id.tv_collection_count)
        val replyCount: TextView = view.findViewById(R.id.tv_reply_count)
        val rvImages: RecyclerView = view.findViewById(R.id.rv_images)
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val nickname: TextView = view.findViewById(R.id.tv_nickname)
        val description: TextView = view.findViewById(R.id.tv_description)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CommunityItem.HeaderCard -> TYPE_HEADER
            is CommunityItem.EntryCard -> TYPE_ENTRY
            is CommunityItem.ContentCard -> TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_topic_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_ENTRY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_community_entry, parent, false)
                EntryViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_community_content, parent, false)
                ContentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CommunityItem.HeaderCard -> {
                val vh = holder as HeaderViewHolder
                vh.title.text = item.title
                vh.description.text = item.description
                Glide.with(vh.headerImage)
                    .load(item.headerImage)
                    .placeholder(android.R.color.darker_gray)
                    .transform(CenterCrop())
                    .into(vh.headerImage)
            }
            is CommunityItem.EntryCard -> {
                val vh = holder as EntryViewHolder
                vh.title.text = item.title
                vh.subTitle.text = item.subTitle
                Glide.with(vh.bg).load(item.bgPicture).transform(CenterCrop(), RoundedCorners(16)).into(vh.bg)
            }
            is CommunityItem.ContentCard -> {
                val vh = holder as ContentViewHolder
                vh.nickname.text = item.nickname
                vh.description.text = item.description
                vh.collectionCount.text = formatCount(item.collectionCount)
                vh.replyCount.text = formatCount(item.replyCount)

                Glide.with(vh.avatar).load(item.avatar).transform(CircleCrop()).into(vh.avatar)

                if (item.isVideo) {
                    // 视频：显示 CardView 封面
                    vh.flVideo.visibility = View.VISIBLE
                    vh.rvImages.visibility = View.GONE

                    Glide.with(vh.cover).load(item.coverUrl).transform(CenterCrop()).into(vh.cover)
                    vh.cover.setOnClickListener { onVideoClick(item) }

                    if (item.duration > 0) {
                        vh.duration.visibility = View.VISIBLE
                        val mins = item.duration / 60
                        val secs = item.duration % 60
                        vh.duration.text = String.format("%02d:%02d", mins, secs)
                    } else {
                        vh.duration.visibility = View.GONE
                    }
                } else {
                    // 图片：隐藏视频卡片，显示图片网格
                    vh.flVideo.visibility = View.GONE

                    if (item.imageUrls.isNotEmpty()) {
                        vh.rvImages.visibility = View.VISIBLE
                        if (vh.rvImages.adapter == null) {
                            vh.rvImages.layoutManager = GridLayoutManager(vh.itemView.context, 2)
                            vh.rvImages.adapter = ImageAdapter()
                        }
                        (vh.rvImages.adapter as ImageAdapter).submitList(item.imageUrls)
                    } else {
                        vh.rvImages.visibility = View.GONE
                    }
                }
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

/**
 * 图片网格适配器
 */
private class ImageAdapter : ListAdapter<String, ImageAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(old: String, new: String) = old == new
        override fun areContentsTheSame(old: String, new: String) = old == new
    }
) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.image)
            .load(getItem(position))
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.image)
    }
}
