package com.example.ept.person.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.official.WorkMetroData
import com.example.ept.person.R
import com.google.android.material.imageview.ShapeableImageView


/**
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：WorkAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 20:39
 *
 */
private const val TYPE_VIDEO = 0
private const val TYPE_ARTICLE = 1
private const val TYPE_UNKNOWN = 2

class WorkAdapter() : ListAdapter<WorkMetroData, RecyclerView.ViewHolder>(WorkDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.resourceType) {
            "pgc_video", "ugc_video" -> TYPE_VIDEO
            "ugc_picture", "pgc_picture" -> TYPE_ARTICLE
            else -> TYPE_UNKNOWN
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_VIDEO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_vertical_video, parent, false)
                return VideoViewHolder(view)
            }

            TYPE_ARTICLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_vertical_article, parent, false)
                return ArticleViewHolder(view)
            }

            else -> {
                error("Unknown viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        when (holder) {
            is VideoViewHolder -> {
                holder.bindData(item)
            }

            is ArticleViewHolder -> {
                holder.bindData(item)
            }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivVideoCover: ImageView = itemView.findViewById(R.id.iv_video_cover)
        val ivVideoAuthorItem: ShapeableImageView = itemView.findViewById(R.id.iv_video_author_item)
        val tvVideoNameItem: TextView = itemView.findViewById(R.id.tv_video_name_item)
        val tvVideoTimeItem: TextView = itemView.findViewById(R.id.tv_video_time_item)
        val tvVideoDescItem: TextView = itemView.findViewById(R.id.tv_video_desc_item)
        val tvVideoLabelItem: TextView = itemView.findViewById(R.id.tv_video_label_item)
        val tvVideoLikeItem: TextView = itemView.findViewById(R.id.tv_video_like_item)
        val tvVideoFavorItem: TextView = itemView.findViewById(R.id.tv_video_favor_item)
        val tvVideoCommentItem: TextView = itemView.findViewById(R.id.tv_video_comment_item)
        val tvVideoTopicItem: TextView=itemView.findViewById(R.id.tv_video_topic_item)


        val ivVideoForwardItem: ImageView = itemView.findViewById(R.id.iv_video_forward_item)
        val tvVideoFoldItem: TextView = itemView.findViewById(R.id.tv_video_fold_item)
        fun bindData(metroData: WorkMetroData) {
            Glide.with(ivVideoCover.context)
                .load(metroData.video?.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivVideoCover)
            Glide.with(ivVideoAuthorItem.context)
                .load(metroData.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivVideoAuthorItem)

            tvVideoNameItem.text=metroData.author?.nick
            tvVideoTimeItem.text=metroData.publishTime
            tvVideoDescItem.text=metroData.text
            tvVideoLabelItem.text= metroData.tags?.map { it.title } as CharSequence?
            tvVideoTopicItem.text=metroData.topics?.firstOrNull()?.title
            tvVideoLikeItem.text = metroData.consumption?.likeCount.toString()
            tvVideoFavorItem.text =metroData.consumption?.collectionCount.toString()
            tvVideoCommentItem.text=metroData.consumption?.commentCount.toString()
        }
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivArticleAuthorItem: ShapeableImageView =
            itemView.findViewById(R.id.iv_article_author_item)
        val tvArticleNameItem: TextView = itemView.findViewById(R.id.tv_article_name_item)
        val tvArticleTimeItem: TextView = itemView.findViewById(R.id.tv_article_time_item)
        val tvArticleDescItem: TextView = itemView.findViewById(R.id.tv_article_desc_item)
        val tvArticleLabelItem: TextView = itemView.findViewById(R.id.tv_article_label_item)
        val tvArticleLikeItem: TextView = itemView.findViewById(R.id.tv_article_like_item)
        val tvArticleFavorItem: TextView = itemView.findViewById(R.id.tv_article_favor_item)
        val tvArticleCommentItem: TextView = itemView.findViewById(R.id.tv_article_comment_item)
        val tvArticleTopicItem: TextView=itemView.findViewById(R.id.tv_article_topic_item)

        val ivArticleForwardItem: ImageView = itemView.findViewById(R.id.iv_article_forward_item)
        val tvArticleFoldItem: TextView = itemView.findViewById(R.id.tv_article_fold_item)
        val rvArticleCover: RecyclerView = itemView.findViewById(R.id.rv_article_cover)
        val adapter= ArticleAdapter()
        fun bindData(metroData: WorkMetroData) {
            Glide.with(ivArticleAuthorItem.context)
                .load(metroData.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivArticleAuthorItem)

            rvArticleCover.adapter=adapter
            val manager= LinearLayoutManager(itemView.context)
            manager.orientation= LinearLayoutManager.HORIZONTAL
            rvArticleCover.layoutManager= manager
            val snapHelper = PagerSnapHelper()
            rvArticleCover.onFlingListener=null
            snapHelper.attachToRecyclerView(rvArticleCover)
            adapter.submitList(metroData.images)


            tvArticleNameItem.text=metroData.author?.nick
            tvArticleTimeItem.text=metroData.publishTime
            tvArticleDescItem.text=metroData.text
            tvArticleTopicItem.text=metroData.topics?.firstOrNull()?.title
            tvArticleLabelItem.text=metroData.tags?.map { it.title } ?.firstOrNull()
            tvArticleLikeItem.text = metroData.consumption?.likeCount.toString()
            tvArticleFavorItem.text =metroData.consumption?.collectionCount.toString()
            tvArticleCommentItem.text=metroData.consumption?.commentCount.toString()
        }
    }
}

object WorkDiffCallback : DiffUtil.ItemCallback<WorkMetroData>() {
    override fun areItemsTheSame(oldItem: WorkMetroData, newItem: WorkMetroData): Boolean {
        return oldItem.resourceId != null && oldItem.resourceId == newItem.resourceId
                || oldItem.itemId != null && oldItem.itemId == newItem.itemId
                || oldItem.videoId != null && oldItem.videoId == newItem.videoId
    }
    override fun areContentsTheSame(oldItem: WorkMetroData, newItem: WorkMetroData): Boolean {
        return oldItem.liked == newItem.liked
                && oldItem.collected == newItem.collected
                && oldItem.consumption?.likeCount == newItem.consumption?.likeCount
                && oldItem.consumption?.collectionCount == newItem.consumption?.collectionCount
                && oldItem.consumption?.commentCount == newItem.consumption?.commentCount
                && oldItem.consumption?.shareCount == newItem.consumption?.shareCount
                && oldItem.text == newItem.text
                && oldItem.images?.size == newItem.images?.size
                && oldItem.title == newItem.title
                && oldItem.duration?.value == newItem.duration?.value
                && oldItem.cover?.url == newItem.cover?.url
    }
}


