package com.example.ept.search.adapter.resultcontent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.official.MetroData
import com.example.ept.search.R

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultTopicsFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:01
 *
 */
class ResultTopicsAdapter :
    ListAdapter<MetroData, ResultTopicsAdapter.TopicsViewHolder>(TopicsDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_topics, parent, false)
        return TopicsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TopicsViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class TopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivResultTopicsCoverItem: ImageView =
            itemView.findViewById(R.id.iv_result_topics_cover_item)
        val tvResultTopicsTitleItem: TextView =
            itemView.findViewById(R.id.tv_result_topics_title_item)
        val tvResultTopicsDescItem: TextView =
            itemView.findViewById(R.id.tv_result_topics_desc_item)
        val tvResultTopicsInteractItem: TextView =
            itemView.findViewById(R.id.tv_result_topics_interact_item)

        fun bindData(metroData: MetroData) {
            Glide.with(ivResultTopicsCoverItem.context)
                .load(metroData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivResultTopicsCoverItem)
            tvResultTopicsTitleItem.text = metroData.title
            tvResultTopicsDescItem.text = metroData.description
            tvResultTopicsInteractItem.text = metroData.tags?.firstOrNull()?.title
        }
    }


}

object TopicsDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.topicId == p1.topicId
    }

    override fun areContentsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.link == p1.link
    }
}