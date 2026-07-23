package com.example.ept.search.adapter.resultcontent

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
import com.example.core.model.MetroData
import com.example.ept.search.R

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultVideosFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:01
 *
 */
class ResultVideosAdapter :
    ListAdapter<MetroData, ResultVideosAdapter.VideosViewHolder>(VideosDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideosViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_videos, parent, false)
        return VideosViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: VideosViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivVideosCoverItem: ImageView =
            itemView.findViewById(R.id.iv_videos_cover_item)
        val tvVideosTitleItem: TextView =
            itemView.findViewById(R.id.tv_videos_title_item)
        val tvVideosDurationItem: TextView =
            itemView.findViewById(R.id.tv_videos_duration_item)
        val tvVideosLabelItem: TextView =
            itemView.findViewById(R.id.tv_videos_label_item)

        fun bindData(metroData: MetroData) {
            Glide.with(ivVideosCoverItem.context)
                .load(metroData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivVideosCoverItem)
            tvVideosTitleItem.text = metroData.title
            val label = metroData.tags?.firstOrNull()?.title?.removePrefix("#")
            tvVideosLabelItem.text = "#$label"
            val duration = DateUtils.formatElapsedTime(metroData.duration?.value?.toLong()!!)
            tvVideosDurationItem.text = "▶$duration"
        }
    }
}

object VideosDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(
        oldItem: MetroData,
        newItem: MetroData
    ): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(
        oldItem: MetroData,
        newItem: MetroData
    ): Boolean {
        return oldItem.playUrl == newItem.playUrl
    }

}