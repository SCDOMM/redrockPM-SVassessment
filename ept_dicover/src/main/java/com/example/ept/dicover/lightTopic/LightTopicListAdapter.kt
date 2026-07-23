package com.example.ept.dicover.lightTopic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.dicover.R
import com.example.ept.dicover.topicdetail.TopicPlaylistVideo

/**
 * description ： 主题播单列表适配器
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LightTopicListAdapter(
    private val onTopicClick: (LightTopicItem) -> Unit,
    private val onVideoClick: (TopicPlaylistVideo) -> Unit
) : ListAdapter<LightTopicItem, LightTopicListAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** 视图持有者，持有标题、描述和预览视频列表控件 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val description: TextView = view.findViewById(R.id.tv_description)
        val rvPreviewVideos: RecyclerView = view.findViewById(R.id.rv_preview_videos)
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_light_topic_card, parent, false)
        return ViewHolder(view)
    }

    /** 绑定视图数据，设置预览视频横向列表 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.title.text = item.title
        holder.description.text = item.description

        // 设置预览视频横向列表
        val previewAdapter = PreviewVideoAdapter { video ->
            onVideoClick(video)
        }
        previewAdapter.setData(item.videos)

        holder.rvPreviewVideos.apply {
            layoutManager = LinearLayoutManager(
                holder.itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = previewAdapter
        }

        // 点击整个卡片跳转详情
        holder.itemView.setOnClickListener { onTopicClick(item) }
    }

    /** DiffUtil 回调，用于列表差量更新 */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LightTopicItem>() {
            override fun areItemsTheSame(old: LightTopicItem, new: LightTopicItem) = old.topicId == new.topicId
            override fun areContentsTheSame(old: LightTopicItem, new: LightTopicItem) = old == new
        }
    }
}
