package com.example.ept.dicover.topiclist

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
 * description ： 话题标签列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/18  16:45
 */
/** 标签话题列表适配器 */
class TagTopicAdapter(
    private val onItemClick: (TagTopicItem) -> Unit = {}
) : ListAdapter<TagTopicItem, TagTopicAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 视图持有者：保存图标、标题和描述引用 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_tag_icon)
        val title: TextView = view.findViewById(R.id.tv_tag_title)
        val desc: TextView = view.findViewById(R.id.tv_tag_desc)
    }

    /** 创建视图持有者：加载标签话题列表项布局 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag_topic, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据：设置标题和描述、加载圆角图标、设置点击事件 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.title.text = item.title
        holder.desc.text = item.description

        Glide.with(holder.icon)
            .load(item.icon)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.icon)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        /** 差异回调：用于 ListAdapter 局部刷新 */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TagTopicItem>() {
            override fun areItemsTheSame(old: TagTopicItem, new: TagTopicItem) = old.id == new.id
            override fun areContentsTheSame(old: TagTopicItem, new: TagTopicItem) = old == new
        }
    }
}
