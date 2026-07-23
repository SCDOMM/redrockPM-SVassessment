package com.example.ept.dicover.topicsquare

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
 * description ： 话题广场列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class TopicSquareAdapter(
    private val onItemClick: (TopicSquareItem) -> Unit = {}
) : ListAdapter<TopicSquareItem, TopicSquareAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 话题卡片视图持有者 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_tag_icon)
        val title: TextView = view.findViewById(R.id.tv_tag_title)
        val description: TextView = view.findViewById(R.id.tv_tag_desc)
    }

    /** 创建话题卡片视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag_topic, parent, false)
        return ViewHolder(view)
    }

    /** 绑定话题卡片数据：标题、描述、封面图及点击事件 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.title.text = item.title
        holder.description.text = item.description

        Glide.with(holder.icon.context)
            .load(item.coverUrl)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.icon)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    /** 差异比较回调，用于局部刷新列表 */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TopicSquareItem>() {
            override fun areItemsTheSame(old: TopicSquareItem, new: TopicSquareItem) = old.id == new.id
            override fun areContentsTheSame(old: TopicSquareItem, new: TopicSquareItem) = old == new
        }
    }
}
