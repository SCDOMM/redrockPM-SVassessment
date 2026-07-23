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
 * description ： 主题播单视频列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
class TopicPlaylistAdapter(
    private val onItemClick: (TopicPlaylistVideo) -> Unit = {}
) : ListAdapter<TopicPlaylistVideo, TopicPlaylistAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 视图持有者，持有主题图标和标题控件引用 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_topic_icon)
        val title: TextView = view.findViewById(R.id.tv_topic_title)
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.title.text = item.title

        Glide.with(holder.icon.context)
            .load(item.coverUrl)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.icon)

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
