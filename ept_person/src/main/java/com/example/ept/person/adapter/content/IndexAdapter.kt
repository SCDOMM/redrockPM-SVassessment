package com.example.ept.person.adapter.content

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ept.person.R
import com.example.core.common.UserHomeItem
import com.google.android.material.imageview.ShapeableImageView

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：IndexAdapter
 * 类描述：用于展示主页，包含有RV"最近更新"，普通item"最受欢迎"和以ll为基础的item"专辑"
 * 创建人：韦西波
 * 创建时间：2026-07-22 14:17
 *
 */
private const val TYPE_SECTION_TITLE = 0
private const val TYPE_VIDEO_RECENT = 1
private const val TYPE_VIDEO_POPULAR = 2
private const val TYPE_ALBUM = 3

class IndexAdapter : ListAdapter<UserHomeItem, RecyclerView.ViewHolder>(IndexDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is UserHomeItem.SectionTitle -> TYPE_SECTION_TITLE
            is UserHomeItem.VideoRecent -> TYPE_VIDEO_RECENT
            is UserHomeItem.VideoPopular -> TYPE_VIDEO_POPULAR
            is UserHomeItem.Album -> TYPE_ALBUM
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION_TITLE -> {
                val view = inflater.inflate(R.layout.item_item_title, parent, false)
                SectionTitleViewHolder(view)
            }

            TYPE_VIDEO_RECENT -> {
                val view =
                    inflater.inflate(R.layout.item_new, parent, false)
                RecentVideoViewHolder(view)
            }

            TYPE_VIDEO_POPULAR -> {
                val view = inflater
                    .inflate(R.layout.item_video, parent, false)
                PopularVideoViewHolder(view)
            }

            TYPE_ALBUM -> {
                val view =
                    inflater.inflate(R.layout.item_album, parent, false)
                AlbumViewHolder(view)
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
            is SectionTitleViewHolder -> holder.bindData(item as UserHomeItem.SectionTitle)
            is RecentVideoViewHolder -> holder.bindData(item as UserHomeItem.VideoRecent)
            is PopularVideoViewHolder -> holder.bindData(item as UserHomeItem.VideoPopular)
            is AlbumViewHolder -> holder.bindData(item as UserHomeItem.Album)
        }
    }

    inner class SectionTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitleItemItem: TextView = itemView.findViewById(R.id.tv_title_item_item)
        fun bindData(item: UserHomeItem.SectionTitle) {
            if (item.text.isNullOrEmpty())       tvTitleItemItem.text ="暂无作品"
            else tvTitleItemItem.text = item.text
        }
    }

    inner class RecentVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvNewItem: RecyclerView = itemView.findViewById(R.id.rv_new_item)
        val recentAdapter = RecentAdapter()
        fun bindData(item: UserHomeItem.VideoRecent) {
            rvNewItem.adapter = recentAdapter
            val manager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvNewItem.layoutManager = manager
            rvNewItem.onFlingListener = null
            recentAdapter.submitList(item.videoItems)
        }
    }

    inner class PopularVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivVideoCover: ImageView = itemView.findViewById(R.id.iv_video_cover)
        val ivVideoAuthorProfileItem: ShapeableImageView =
            itemView.findViewById(R.id.iv_video_author_profile_item)
        val tvVideoAuthorNameItem: TextView = itemView.findViewById(R.id.tv_video_author_name_item)
        val tvVideoTimeItem: TextView = itemView.findViewById(R.id.tv_video_time_item)
        val tvVideoDescItem: TextView = itemView.findViewById(R.id.tv_video_desc_item)
        val tvVideoLabelItem: TextView = itemView.findViewById(R.id.tv_video_label_item)
        val tvVideoLikeItem: TextView = itemView.findViewById(R.id.tv_video_like_item)
        val tvVideoCollectionItem: TextView = itemView.findViewById(R.id.tv_video_collection_item)
        val tvVideoCommentItem: TextView = itemView.findViewById(R.id.tv_video_comment_item)
        val tvVideoTopicItem: TextView = itemView.findViewById(R.id.tv_video_topic_item)

        val ivVideoForwardItem: ImageView = itemView.findViewById(R.id.iv_video_forward_item)
        val tvVideoFoldItem: TextView = itemView.findViewById(R.id.tv_video_fold_item)
        var isExpanded=false
        fun bindData(item: UserHomeItem.VideoPopular) {
            val data = item.data
            Glide.with(ivVideoCover.context)
                .load(data.video?.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivVideoCover)
            Glide.with(ivVideoAuthorProfileItem.context)
                .load(data.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivVideoAuthorProfileItem)
            tvVideoAuthorNameItem.text = data.nick ?: data.author?.nick ?: ""
            tvVideoTimeItem.text = data.publishTime ?: data.rawPublishTime ?: ""
            tvVideoDescItem.text = data.text ?: ""
            tvVideoLabelItem.text = data.tags?.firstOrNull()?.title ?: ""
            tvVideoLikeItem.text = data.consumption?.likeCount?.toString() ?: "0"
            tvVideoCollectionItem.text = data.consumption?.collectionCount?.toString() ?: "0"
            tvVideoCommentItem.text = data.consumption?.commentCount?.toString() ?: "0"
            tvVideoTopicItem.text = data.topics?.firstOrNull()?.title ?: ""

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

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hsAlbumDefaultItem: HorizontalScrollView =
            itemView.findViewById(R.id.hs_album_default_item)
        val llAlbumContainerItem: LinearLayout = itemView.findViewById(R.id.ll_album_container_item)
        val ivAlbumCoverItem: ImageView = itemView.findViewById(R.id.iv_album_cover_item)
        val tvAlbumTitleItem: TextView = itemView.findViewById(R.id.tv_album_title_item)
        val tvAlbumDescItem: TextView = itemView.findViewById(R.id.tv_album_desc_item)

        fun bindData(item: UserHomeItem.Album) {

            val albumData = item.albumData
            tvAlbumTitleItem.text = albumData.albumName
            tvAlbumDescItem.text = albumData.albumDescription
            Glide.with(ivAlbumCoverItem.context)
                .load(albumData.albumCoverUrl)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivAlbumCoverItem)


            llAlbumContainerItem.removeAllViews()
            val context = llAlbumContainerItem.context
            val screenWidth = context.resources.displayMetrics.widthPixels
            val previewList = item.videoPreviews
            previewList.forEach { video ->
                val videoView = LayoutInflater.from(context)
                    .inflate(R.layout.item_item_album, llAlbumContainerItem, false)
                val ivNewCoverItemItem =
                    videoView.findViewById<ImageView>(R.id.iv_new_cover_item_item)
                Glide.with(ivNewCoverItemItem.context)
                    .load(video.coverUrl)
                    .error(R.drawable.eyepetater)
                    .placeholder(R.drawable.eyepetater)
                    .into(ivNewCoverItemItem)
                val itemWidth = if (previewList.size == 1) {
                    screenWidth
                } else {
                    (screenWidth * 0.8).toInt()
                }
                videoView.layoutParams = LinearLayout.LayoutParams(
                    itemWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llAlbumContainerItem.addView(videoView)
            }
        }
    }
}

object IndexDiffCallback : DiffUtil.ItemCallback<UserHomeItem>() {
    override fun areItemsTheSame(oldItem: UserHomeItem, newItem: UserHomeItem): Boolean {
        return when (oldItem) {
            is UserHomeItem.SectionTitle if newItem is UserHomeItem.SectionTitle ->
                oldItem.text == newItem.text

            is UserHomeItem.VideoRecent if newItem is UserHomeItem.VideoRecent ->
                oldItem.videoItems == newItem.videoItems

            is UserHomeItem.VideoPopular if newItem is UserHomeItem.VideoPopular ->
                oldItem.data.resourceId == newItem.data.resourceId &&
                        oldItem.data.itemId == newItem.data.itemId

            is UserHomeItem.Album if newItem is UserHomeItem.Album ->
                oldItem.albumData.albumName == newItem.albumData.albumName

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: UserHomeItem, newItem: UserHomeItem): Boolean {
        return oldItem == newItem
    }
}