package com.example.ept.dicover.lightTopic

import com.example.core.model.LightTopicPlaylistVideo
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

/**
 * description ： 主题播单列表页中的横向视频适配器
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class PreviewVideoAdapter(
    private val onItemClick: (LightTopicPlaylistVideo) -> Unit = {}
) : ListAdapter<LightTopicPlaylistVideo, PreviewVideoAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val duration: TextView = view.findViewById(R.id.tv_duration)
        val title: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preview_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.title.text = item.title

        val totalSeconds = item.duration.toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        holder.duration.text = String.format("%02d:%02d", minutes, seconds)

        Glide.with(holder.cover.context)
            .load(item.coverUrl)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.cover)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LightTopicPlaylistVideo>() {
            override fun areItemsTheSame(old: LightTopicPlaylistVideo, new: LightTopicPlaylistVideo) = old.id == new.id
            override fun areContentsTheSame(old: LightTopicPlaylistVideo, new: LightTopicPlaylistVideo) = old == new
        }
    }
}
