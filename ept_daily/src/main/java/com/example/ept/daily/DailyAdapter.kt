package com.example.ept.daily

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.VideoData

/**   
 * 包名称： com.example.ept.daily
 * 类名称：DailyAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 21:11
 *
 */
class DailyAdapter : ListAdapter<VideoData, DailyAdapter.DailyViewHolder>(DailyDiffCallback) {
    override fun onBindViewHolder(
        holder: DailyViewHolder,
        position: Int
    ) {
        val video = getItem(position)
        holder.bindData(video)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_daily, parent, false)
        return DailyViewHolder(view)
    }

    inner class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDailyCoverItem: ImageView = view.findViewById(R.id.iv_daily_cover_item)
        val ivDailyAuthorItem: ImageView = view.findViewById(R.id.iv_daily_author_item)
        val tvDailyTitleItem: TextView = view.findViewById(R.id.tv_daily_title_item)
        val tvDailyLabelItem: TextView = view.findViewById(R.id.tv_daily_label_item)
        val tvAuthorDescItem: TextView = view.findViewById(R.id.tv_daily_author_item)
        val tvDailyDurationItem: TextView = view.findViewById(R.id.tv_daily_duration_item)
        fun bindData(videoData: VideoData) {
            ivDailyCoverItem.let { player ->
                Glide.with(player.context)
                    .load(videoData.cover?.feed)
                    .into(player)
            }
            Glide.with(ivDailyAuthorItem)
                .load(videoData.cover?.feed)
                .into(ivDailyAuthorItem)
            tvDailyTitleItem.text = videoData.title
            val label = videoData.label?.text
                ?: videoData.category
            tvDailyLabelItem.text = "#$label"
            tvAuthorDescItem.text = videoData.author?.name
            val duration = DateUtils.formatElapsedTime(videoData.duration)
            tvDailyDurationItem.text = "▶$duration"
        }
    }
}

object DailyDiffCallback : DiffUtil.ItemCallback<VideoData>() {
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




