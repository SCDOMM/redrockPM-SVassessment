package com.example.ept.home.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.videoEntity.VideoData
import com.example.ept.home.R
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

/**   
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeAdapter : ListAdapter<VideoData, RecyclerView.ViewHolder>(HomeDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        } else if (position in 1..<5) {
            return 1
        }
        return 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview1_home, parent, false)
                HeaderViewHolder(view)
            }

            VIEW_TYPE_SELECTED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview2_home, parent, false)
                SelectedViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview3_home, parent, false)
                NormalViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val video = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bindData(video)
            is SelectedViewHolder -> holder.bindData(video)
            is NormalViewHolder -> holder.bindData(video)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gsyvHomeDefaultItem1: GSYVideoPlayer = view.findViewById(R.id.gsyv_home_item1)
        val ivHomeDefaultItem1: ImageView = view.findViewById(R.id.iv_home_default_item1)
        val tvHomeTitleItem1: TextView = view.findViewById(R.id.tv_home_title_item1)
        val tvHomeLabelItem1: TextView = view.findViewById(R.id.tv_home_label_item1)
        val tvHomeDescItem1: TextView = view.findViewById(R.id.tv_home_desc_item1)
        val tvHomeDurationItem1: TextView = view.findViewById(R.id.tv_home_duration_item1)
        fun bindData(videoData: VideoData) {
            gsyvHomeDefaultItem1.setUp(videoData.playUrl, true, videoData.title)
            Glide.with(ivHomeDefaultItem1)
                .load(videoData.cover.feed)
                .into(ivHomeDefaultItem1)
            tvHomeTitleItem1.text = videoData.title
            tvHomeLabelItem1.text = videoData.label?.text
                ?: videoData.category ?: videoData.tags?.firstOrNull()?.name
            tvHomeDescItem1.text = videoData.description
            tvHomeDurationItem1.text = DateUtils.formatElapsedTime(videoData.duration)
        }
    }

    inner class SelectedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gsyvHomeCoverItem2: GSYVideoPlayer = view.findViewById(R.id.gsyv_home_cover_item2)
        val ivHomeDefaultItem2: ImageView = view.findViewById(R.id.iv_home_default_item2)
        val tvHomeTitleItem2: TextView = view.findViewById(R.id.tv_home_title_item2)
        val tvHomeLabelItem2: TextView = view.findViewById(R.id.tv_home_label_item2)
        val tvHomeDescItem2: TextView = view.findViewById(R.id.tv_home_desc_item2)
        val tvHomeDurationItem2: TextView = view.findViewById(R.id.tv_home_duration_item2)
        fun bindData(videoData: VideoData) {
            gsyvHomeCoverItem2.setUp(videoData.playUrl, false, "") // false = 不自动播放
            gsyvHomeCoverItem2.let { player ->
                Glide.with(player.context)
                    .load(videoData.cover.feed)
                    .into(player.thumbImageView as ImageView)
            }
            Glide.with(ivHomeDefaultItem2)
                .load(videoData.cover.feed)
                .into(ivHomeDefaultItem2)
            tvHomeTitleItem2.text = videoData.title
            tvHomeLabelItem2.text = videoData.label?.text
                ?: videoData.category ?: videoData.tags?.firstOrNull()?.name
            tvHomeDescItem2.text = videoData.description
            tvHomeDurationItem2.text = DateUtils.formatElapsedTime(videoData.duration)
        }
    }

    inner class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeCoverItem3: GSYVideoPlayer = view.findViewById(R.id.iv_home_cover_item3)
        val tvHomeTitleItem3: TextView = view.findViewById(R.id.tv_home_title_item3)
        val tvHomeLabelItem3: TextView = view.findViewById(R.id.tv_home_label_item3)
        val tvHomeDurationItem3: TextView = view.findViewById(R.id.tv_home_duration_item3)
        fun bindData(videoData: VideoData) {
            ivHomeCoverItem3.setUp(videoData.playUrl, false, "")
            ivHomeCoverItem3.let { player ->
                Glide.with(player.context)
                    .load(videoData.cover.feed)
                    .into(player.thumbImageView as ImageView)
            }
            tvHomeTitleItem3.text = videoData.title
            tvHomeLabelItem3.text = videoData.label?.text
                ?: videoData.category ?: videoData.tags?.firstOrNull()?.name
            tvHomeDurationItem3.text = DateUtils.formatElapsedTime(videoData.duration)
        }
    }
}

object HomeDiffCallback : DiffUtil.ItemCallback<VideoData>() {
    override fun areItemsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem == newItem
    }
}

const val VIEW_TYPE_HEADER = 0
const val VIEW_TYPE_SELECTED = 1
const val VIEW_TYPE_NORMAL = 2