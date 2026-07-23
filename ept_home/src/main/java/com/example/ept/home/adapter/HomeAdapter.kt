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
import com.example.core.model.MetroData
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
class HomeAdapter : ListAdapter<MetroData, RecyclerView.ViewHolder>(HomeDiffCallback) {
    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE_HEADER
        } else if (position in 1..<6) {
            return VIEW_TYPE_SELECTED
        }
        return VIEW_TYPE_NORMAL
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
        val tvHomeAuthorItem1: TextView = view.findViewById(R.id.tv_home_author_item1)
        val tvHomeDurationItem1: TextView = view.findViewById(R.id.tv_home_duration_item1)
        fun bindData(videoData: MetroData) {
            gsyvHomeDefaultItem1.setUp(videoData.playUrl, true, videoData.title)

            gsyvHomeDefaultItem1.backButton.visibility = View.GONE
            gsyvHomeDefaultItem1.thumbImageView = ImageView(gsyvHomeDefaultItem1.context)
            Glide.with(gsyvHomeDefaultItem1.context)
                .load(videoData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(gsyvHomeDefaultItem1.thumbImageView!! as ImageView)

            Glide.with(ivHomeDefaultItem1)
                .load(videoData.author?.avatar?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivHomeDefaultItem1)
            tvHomeTitleItem1.text = videoData.title
            val label =  videoData.tags?.firstOrNull()?.title?.removePrefix("# ")
            tvHomeLabelItem1.text = "#$label"
            tvHomeAuthorItem1.text = videoData.author?.nick
            val duration = DateUtils.formatElapsedTime(videoData.duration?.value?.toLong()!!)
            tvHomeDurationItem1.text = "▶$duration"
        }
    }

    inner class SelectedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeCoverItem2: ImageView = view.findViewById(R.id.iv_home_cover_item2)
        val ivHomeDefaultItem2: ImageView = view.findViewById(R.id.iv_home_default_item2)
        val tvHomeTitleItem2: TextView = view.findViewById(R.id.tv_home_title_item2)
        val tvHomeLabelItem2: TextView = view.findViewById(R.id.tv_home_label_item2)
        val tvHomeAuthorItem2: TextView = view.findViewById(R.id.tv_home_author_item2)
        val tvHomeDurationItem2: TextView = view.findViewById(R.id.tv_home_duration_item2)
        fun bindData(videoData: MetroData) {
            Glide.with(ivHomeCoverItem2.context)
                .load(videoData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivHomeCoverItem2)
            Glide.with(ivHomeDefaultItem2)
                .load(videoData.author?.avatar?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivHomeDefaultItem2)
            tvHomeTitleItem2.text = videoData.title
            val label =  videoData.tags?.firstOrNull()?.title?.removePrefix("# ")
            tvHomeLabelItem2.text = "#$label"
            tvHomeAuthorItem2.text = videoData.author?.nick
            val duration = DateUtils.formatElapsedTime(videoData.duration?.value?.toLong()!!)
            tvHomeDurationItem2.text = "▶$duration"
        }
    }

    inner class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeCoverItem3: ImageView = view.findViewById(R.id.iv_home_cover_item3)
        val tvHomeTitleItem3: TextView = view.findViewById(R.id.tv_home_title_item3)
        val tvHomeLabelItem3: TextView = view.findViewById(R.id.tv_home_label_item3)
        val tvHomeDurationItem3: TextView = view.findViewById(R.id.tv_home_duration_item3)
        fun bindData(videoData: MetroData) {
                Glide.with(ivHomeCoverItem3.context)
                    .load(videoData.cover?.url)
                    .placeholder(R.drawable.eyepetater)
                    .error(R.drawable.eyepetater)
                    .into(ivHomeCoverItem3)
            tvHomeTitleItem3.text = videoData.title
            val label =  videoData.tags?.firstOrNull()?.title?.removePrefix("# ")
            tvHomeLabelItem3.text = "#$label"
            val duration = DateUtils.formatElapsedTime(videoData.duration?.value?.toLong()!!)
            tvHomeDurationItem3.text = "▶$duration"
        }
    }
}

object HomeDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(
        oldItem: MetroData,
        newItem: MetroData
    ): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(
        oldItem: MetroData,
        newItem: MetroData
    ): Boolean {
        return oldItem.playUrl == newItem.playUrl
    }
}

const val VIEW_TYPE_HEADER = 0
const val VIEW_TYPE_SELECTED = 1
const val VIEW_TYPE_NORMAL = 2