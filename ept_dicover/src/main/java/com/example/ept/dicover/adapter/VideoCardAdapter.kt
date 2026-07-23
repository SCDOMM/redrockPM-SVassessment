package com.example.ept.dicover.adapter

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ept.dicover.R
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo

/**
 * description ： 视频卡片适配器（详情页视频列表）
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class VideoCardAdapter(
    private val onItemClick: (TopicPlaylistVideo) -> Unit = {}
) : ListAdapter<TopicPlaylistVideo, VideoCardAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 视图持有者，持有视频卡片各控件引用 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorIcon: ImageView = view.findViewById(R.id.iv_author_icon)
        val authorName: TextView = view.findViewById(R.id.tv_author_name)
        val title: TextView = view.findViewById(R.id.tv_title)
        val description: TextView = view.findViewById(R.id.tv_description)
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val duration: TextView = view.findViewById(R.id.tv_duration)
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_card, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.authorName.text = item.authorName
        holder.title.text = item.title
        holder.description.text = item.description

        // 时长格式化 mm:ss
        val totalSeconds = item.duration.toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        holder.duration.text = String.format("%02d:%02d", minutes, seconds)

        Glide.with(holder.cover.context)
            .load(item.coverUrl)
            .transform(CenterCrop(), RoundedCorners(24))
            .into(holder.cover)

        if (item.authorIcon.isNotEmpty()) {
            Glide.with(holder.authorIcon.context)
                .load(item.authorIcon)
                .transform(CenterCrop(), RoundedCorners(36))
                .into(holder.authorIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    /** DiffUtil 回调，用于列表差量更新 */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TopicPlaylistVideo>() {
            override fun areItemsTheSame(old: TopicPlaylistVideo, new: TopicPlaylistVideo) = old.id == new.id
            override fun areContentsTheSame(old: TopicPlaylistVideo, new: TopicPlaylistVideo) = old == new
        }
    }
}
