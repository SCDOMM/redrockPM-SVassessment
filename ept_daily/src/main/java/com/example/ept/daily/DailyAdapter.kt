package com.example.ept.daily

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
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

/**   
 * 包名称： com.example.ept.daily
 * 类名称：DailyAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 21:11
 *
 */
class DailyAdapter  : ListAdapter<VideoData, DailyAdapter.DailyViewHolder>(DailyDiffCallback) {
    override fun onBindViewHolder(
        holder: DailyViewHolder,
        position: Int
    ) {
        val video=getItem(position)
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
        val gsyvDailyCoverItem: GSYVideoPlayer = view.findViewById(R.id.gsyv_daily_cover_item)
        val ivDailyDefaultItem: ImageView = view.findViewById(R.id.iv_daily_default_item)
        val tvDailyTitleItem: TextView = view.findViewById(R.id.tv_daily_title_item)
        val tvDailyLabelItem: TextView = view.findViewById(R.id.tv_daily_label_item)
        val tvDailyDescItem: TextView = view.findViewById(R.id.tv_daily_desc_item)
        val tvDailyDurationItem: TextView = view.findViewById(R.id.tv_daily_duration_item)
        fun bindData(videoData: VideoData){
            gsyvDailyCoverItem.setUp(videoData.playUrl, false, "") // false = 不自动播放
            gsyvDailyCoverItem.let { player ->
                Glide.with(player.context)
                    .load(videoData.cover.feed)
                    .into(player.thumbImageView as ImageView)
            }
            Glide.with(ivDailyDefaultItem)
                .load(videoData.cover.feed)
                .into(ivDailyDefaultItem)
            tvDailyTitleItem.text = videoData.title
            tvDailyLabelItem.text = videoData.label?.text
                ?: videoData.category ?: videoData.tags?.firstOrNull()?.name
            tvDailyDescItem.text = videoData.description
            tvDailyDurationItem.text = DateUtils.formatElapsedTime(videoData.duration)
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




