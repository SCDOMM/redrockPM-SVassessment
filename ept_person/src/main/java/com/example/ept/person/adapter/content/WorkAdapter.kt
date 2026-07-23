package com.example.ept.person.adapter.content

import android.text.TextUtils
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
import com.example.core.model.MetroData
import com.example.ept.person.R
import com.google.android.material.imageview.ShapeableImageView


/**
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：WorkAdapter
 * 类描述：用于展示作品页面的item，包含有视频与图文两种，图文为单独的RV用于展示其中的图片。
 * 创建人：韦西波
 * 创建时间：2026-07-21 20:39
 *
 */
private const val TYPE_VIDEO = 0
private const val TYPE_GRAPHIC = 1
private const val TYPE_UNKNOWN = 2

class WorkAdapter: ListAdapter<MetroData, RecyclerView.ViewHolder>(WorkDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.resourceType) {
            "pgc_video", "ugc_video" -> TYPE_VIDEO
            "ugc_picture", "pgc_picture" -> TYPE_GRAPHIC
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
                    .inflate(R.layout.item_video, parent, false)
                return VideoViewHolder(view)
            }

            TYPE_GRAPHIC -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_graphic, parent, false)
                return GraphicViewHolder(view)
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

            is GraphicViewHolder -> {
                holder.bindData(item)
            }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivVideoCover: ImageView = itemView.findViewById(R.id.iv_video_cover)
        val ivVideoAuthorProfileItem: ShapeableImageView = itemView.findViewById(R.id.iv_video_author_profile_item)
        val tvVideoAuthorNameItem: TextView = itemView.findViewById(R.id.tv_video_author_name_item)
        val tvVideoTimeItem: TextView = itemView.findViewById(R.id.tv_video_time_item)
        val tvVideoDescItem: TextView = itemView.findViewById(R.id.tv_video_desc_item)
        val tvVideoLabelItem: TextView = itemView.findViewById(R.id.tv_video_label_item)
        val tvVideoLikeItem: TextView = itemView.findViewById(R.id.tv_video_like_item)
        val tvVideoCollectionItem: TextView = itemView.findViewById(R.id.tv_video_collection_item)
        val tvVideoCommentItem: TextView = itemView.findViewById(R.id.tv_video_comment_item)
        val tvVideoTopicItem: TextView=itemView.findViewById(R.id.tv_video_topic_item)
        val ivVideoTopicItem: ImageView=itemView.findViewById(R.id.iv_video_topic_item)

        var isExpanded=false

        val ivVideoForwardItem: ImageView = itemView.findViewById(R.id.iv_video_forward_item)
        val tvVideoFoldItem: TextView = itemView.findViewById(R.id.tv_video_fold_item)
        fun bindData(metroData: MetroData) {
            Glide.with(ivVideoCover.context)
                .load(metroData.video?.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivVideoCover)
            Glide.with(ivVideoAuthorProfileItem.context)
                .load(metroData.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivVideoAuthorProfileItem)

            tvVideoAuthorNameItem.text=metroData.author?.nick
            tvVideoTimeItem.text=metroData.publishTime
            tvVideoDescItem.text=metroData.text
            tvVideoLabelItem.text= metroData.tags?.map { it.title } as CharSequence?
            tvVideoTopicItem.text=metroData.topics?.firstOrNull()?.title

            tvVideoLikeItem.text = metroData.consumption?.likeCount.toString()
            tvVideoCollectionItem.text =metroData.consumption?.collectionCount.toString()
            tvVideoCommentItem.text=metroData.consumption?.commentCount.toString()

            tvVideoFoldItem.post {
                if (tvVideoDescItem.lineCount>=2){
                    tvVideoFoldItem.visibility= View.VISIBLE
                }else{
                    tvVideoFoldItem.visibility=View.GONE
                }
            }
            tvVideoFoldItem.setOnClickListener {
                if (isExpanded){
                    tvVideoDescItem.maxLines=2
                    tvVideoDescItem.ellipsize = TextUtils.TruncateAt.END
                    tvVideoFoldItem.text="展开"
                }else{
                    tvVideoDescItem.maxLines=Integer.MAX_VALUE
                    tvVideoDescItem.ellipsize = null
                    tvVideoFoldItem.text="收起"
                }
                isExpanded=!isExpanded
            }
        }
    }

    inner class GraphicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGraphicAuthorProfileItem: ShapeableImageView =
            itemView.findViewById(R.id.iv_graphic_author_profile_item)
        val tvGraphicAuthorNameItem: TextView = itemView.findViewById(R.id.tv_graphic_author_name_item)
        val tvGraphicTimeItem: TextView = itemView.findViewById(R.id.tv_graphic_time_item)
        val tvGraphicDescItem: TextView = itemView.findViewById(R.id.tv_graphic_desc_item)
        val tvGraphicLabelItem: TextView = itemView.findViewById(R.id.tv_graphic_label_item)
        val tvGraphicLikeItem: TextView = itemView.findViewById(R.id.tv_graphic_like_item)
        val tvGraphicCollectionItem: TextView = itemView.findViewById(R.id.tv_graphic_collection_item)
        val tvGraphicCommentItem: TextView = itemView.findViewById(R.id.tv_graphic_comment_item)
        val tvGraphicTopicItem: TextView=itemView.findViewById(R.id.tv_graphic_topic_item)

        val ivGraphicForwardItem: ImageView = itemView.findViewById(R.id.iv_graphic_forward_item)
        val tvGraphicFoldItem: TextView = itemView.findViewById(R.id.tv_graphic_fold_item)
        val rvGraphicCoverItem: RecyclerView = itemView.findViewById(R.id.rv_graphic_cover_item)
        val adapter= WorkGraphicAdapter()
        var isExpanded=false
        fun bindData(metroData: MetroData) {
            Glide.with(ivGraphicAuthorProfileItem.context)
                .load(metroData.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder( R.drawable.eyepetater)
                .into(ivGraphicAuthorProfileItem)

            rvGraphicCoverItem.adapter=adapter
            val manager= LinearLayoutManager(itemView.context)
            manager.orientation= LinearLayoutManager.HORIZONTAL
            rvGraphicCoverItem.layoutManager= manager
            val snapHelper = PagerSnapHelper()
            rvGraphicCoverItem.onFlingListener=null
            snapHelper.attachToRecyclerView(rvGraphicCoverItem)
            adapter.submitList(metroData.images)


            tvGraphicAuthorNameItem.text=metroData.author?.nick
            tvGraphicTimeItem.text=metroData.publishTime
            tvGraphicDescItem.text=metroData.text
            tvGraphicTopicItem.text=metroData.topics?.firstOrNull()?.title
            tvGraphicLabelItem.text=metroData.tags?.map { it.title } ?.firstOrNull()
            tvGraphicLikeItem.text = metroData.consumption?.likeCount.toString()
            tvGraphicCollectionItem.text =metroData.consumption?.collectionCount.toString()
            tvGraphicCommentItem.text=metroData.consumption?.commentCount.toString()

            tvGraphicFoldItem.post {
                if (tvGraphicDescItem.lineCount>=2){
                    tvGraphicFoldItem.visibility= View.VISIBLE
                }else{
                    tvGraphicFoldItem.visibility=View.GONE
                }
            }
            tvGraphicFoldItem.setOnClickListener {
                if (isExpanded){
                    tvGraphicDescItem.maxLines=2
                    tvGraphicDescItem.ellipsize = TextUtils.TruncateAt.END
                    tvGraphicFoldItem.text="展开"
                }else{
                    tvGraphicDescItem.maxLines=Integer.MAX_VALUE
                    tvGraphicDescItem.ellipsize = null
                    tvGraphicFoldItem.text="收起"
                }
                isExpanded=!isExpanded
            }
        }
    }
}

object WorkDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(oldItem: MetroData, newItem: MetroData): Boolean {
        return oldItem.resourceId != null && oldItem.resourceId == newItem.resourceId
                || oldItem.itemId != null && oldItem.itemId == newItem.itemId
                || oldItem.videoId != null && oldItem.videoId == newItem.videoId
    }
    override fun areContentsTheSame(oldItem: MetroData, newItem: MetroData): Boolean {
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


