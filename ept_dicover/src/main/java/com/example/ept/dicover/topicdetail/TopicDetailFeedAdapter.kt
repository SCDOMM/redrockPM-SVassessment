package com.example.ept.dicover.topicdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.ept.dicover.R

/**
 * description ： 话题详情页 Feed 适配器，支持图片和视频
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicDetailFeedAdapter : ListAdapter<TopicFeedItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_VIDEO = 0
        private const val TYPE_IMAGE = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TopicFeedItem>() {
            override fun areItemsTheSame(old: TopicFeedItem, new: TopicFeedItem) = old.id == new.id
            override fun areContentsTheSame(old: TopicFeedItem, new: TopicFeedItem) = old == new
        }
    }

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorAvatar: ImageView = view.findViewById(R.id.iv_author_avatar)
        val authorName: TextView = view.findViewById(R.id.tv_author_name)
        val publishTime: TextView = view.findViewById(R.id.tv_publish_time)
        val contentText: TextView = view.findViewById(R.id.tv_content_text)
        val coverImage: ImageView = view.findViewById(R.id.iv_cover)
        val likeCount: TextView = view.findViewById(R.id.tv_like_count)
        val collectionCount: TextView = view.findViewById(R.id.tv_collection_count)
        val commentCount: TextView = view.findViewById(R.id.tv_comment_count)
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorAvatar: ImageView = view.findViewById(R.id.iv_author_avatar)
        val authorName: TextView = view.findViewById(R.id.tv_author_name)
        val publishTime: TextView = view.findViewById(R.id.tv_publish_time)
        val contentText: TextView = view.findViewById(R.id.tv_content_text)
        val rvImages: RecyclerView = view.findViewById(R.id.rv_images)
        val likeCount: TextView = view.findViewById(R.id.tv_like_count)
        val collectionCount: TextView = view.findViewById(R.id.tv_collection_count)
        val commentCount: TextView = view.findViewById(R.id.tv_comment_count)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isVideo) TYPE_VIDEO else TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_topic_detail_feed_image, parent, false)
                ImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_topic_detail_feed, parent, false)
                VideoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is VideoViewHolder -> {
                holder.authorName.text = item.authorName
                holder.publishTime.text = item.publishTime
                holder.contentText.text = item.text
                holder.likeCount.text = item.likeCount.toString()
                holder.collectionCount.text = item.collectionCount.toString()
                holder.commentCount.text = item.commentCount.toString()

                Glide.with(holder.authorAvatar.context)
                    .load(item.authorAvatar)
                    .transform(CircleCrop())
                    .into(holder.authorAvatar)

                if (item.coverUrl.isNotEmpty()) {
                    holder.coverImage.visibility = View.VISIBLE
                    Glide.with(holder.coverImage.context)
                        .load(item.coverUrl)
                        .transform(CenterCrop(), RoundedCorners(16))
                        .into(holder.coverImage)
                } else {
                    holder.coverImage.visibility = View.GONE
                }
            }
            is ImageViewHolder -> {
                holder.authorName.text = item.authorName
                holder.publishTime.text = item.publishTime
                holder.contentText.text = item.text
                holder.likeCount.text = item.likeCount.toString()
                holder.collectionCount.text = item.collectionCount.toString()
                holder.commentCount.text = item.commentCount.toString()

                Glide.with(holder.authorAvatar.context)
                    .load(item.authorAvatar)
                    .transform(CircleCrop())
                    .into(holder.authorAvatar)

                // 图片网格
                val imageUrls = if (item.coverUrl.isNotEmpty()) listOf(item.coverUrl) else emptyList()
                val imageAdapter = TopicFeedImageAdapter(imageUrls)
                holder.rvImages.apply {
                    layoutManager = GridLayoutManager(holder.itemView.context, 2)
                    adapter = imageAdapter
                }
            }
        }
    }
}

/**
 * 话题 Feed 图片网格适配器
 */
class TopicFeedImageAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<TopicFeedImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic_feed_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.image.context)
            .load(imageUrls[position])
            .transform(CenterCrop(), RoundedCorners(8))
            .into(holder.image)
    }

    override fun getItemCount() = imageUrls.size
}
