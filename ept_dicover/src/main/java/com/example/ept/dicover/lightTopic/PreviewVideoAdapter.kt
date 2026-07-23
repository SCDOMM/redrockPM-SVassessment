package com.example.ept.dicover.lightTopic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ept.dicover.R
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo

/**
 * description ： 预览视频适配器（主题播单列表中的横向视频列表）
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class PreviewVideoAdapter(
    private val onItemClick: (TopicPlaylistVideo) -> Unit = {}
) : RecyclerView.Adapter<PreviewVideoAdapter.ViewHolder>() {

    private val items = mutableListOf<TopicPlaylistVideo>()

    /** 设置预览视频数据 */
    fun setData(list: List<TopicPlaylistVideo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /** 视图持有者，持有封面、时长和标题控件引用 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val duration: TextView = view.findViewById(R.id.tv_duration)
        val title: TextView = view.findViewById(R.id.tv_title)
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preview_video, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title

        // 时长格式化 mm:ss
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

    /** 返回列表项数量 */
    override fun getItemCount() = items.size
}