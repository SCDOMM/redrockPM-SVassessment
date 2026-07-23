package com.example.ept.dicover.discovery

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
 * description ： 推荐主题横向列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/17  13:28
 */
/** 话题横向列表适配器 */
class TopicAdapter(
    private val onItemClick: (TopicItem) -> Unit = {}
) : ListAdapter<TopicItem, TopicAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 视图持有者：保存话题图标和标题引用 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_topic_icon)
        val title: TextView = view.findViewById(R.id.tv_topic_title)

    }

    /** 创建视图持有者：加载话题列表项布局 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据：设置标题、加载图标圆角图片、设置点击事件 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.title.text = item.title

        Glide.with(holder.icon)
            .load(item.icon)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.icon)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        /** 差异回调：用于 ListAdapter 局部刷新 */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TopicItem>() {
            override fun areItemsTheSame(old: TopicItem, new: TopicItem) = old.id == new.id
            override fun areContentsTheSame(old: TopicItem, new: TopicItem) = old == new
        }
    }
}
