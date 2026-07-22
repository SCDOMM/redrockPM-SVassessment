package com.example.ept.person.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.official.WorkMetroData
import com.example.ept.person.R
import com.google.android.material.imageview.ShapeableImageView

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：RecentAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-22 15:22
 *
 */
class RecentAdapter : ListAdapter<WorkMetroData, RecentAdapter.RecentVideoViewHolder>(
    RecentDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentVideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_item_recent, parent, false)
        return RecentVideoViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecentVideoViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
    inner class RecentVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAlbumCoverItemItem: ImageView = itemView.findViewById(R.id.iv_album_cover_item_item)
        val ivRecentDefaultItemItem: ShapeableImageView = itemView.findViewById(R.id.iv_recent_default_item_item)
        val tvRecentTitleItemItem: TextView = itemView.findViewById(R.id.tv_recent_title_item_item)
        val tvRecentAuthorItemItem: TextView = itemView.findViewById(R.id.tv_recent_author_item_item)
        val tvRecentLabelItemItem: TextView = itemView.findViewById(R.id.tv_recent_label_item_item)
        val tvRecentDurationItemItem: TextView = itemView.findViewById(R.id.tv_recent_duration_item_item)
        fun bindData(data: WorkMetroData) {
            Glide.with(ivAlbumCoverItemItem.context)
                .load(data.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivAlbumCoverItemItem)
            Glide.with(ivRecentDefaultItemItem.context)
                .load(data.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivRecentDefaultItemItem)
            tvRecentTitleItemItem.text = data.title ?: ""
            tvRecentAuthorItemItem.text = data.author?.nick ?: ""
            tvRecentLabelItemItem.text = data.tags?.firstOrNull()?.title ?: ""
            tvRecentDurationItemItem.text = data.duration?.text ?: ""
        }
    }
}
object RecentDiffCallback : DiffUtil.ItemCallback<WorkMetroData>() {
    override fun areItemsTheSame(oldItem: WorkMetroData, newItem: WorkMetroData): Boolean {
        return oldItem.resourceId == newItem.resourceId
    }
    override fun areContentsTheSame(oldItem: WorkMetroData, newItem: WorkMetroData): Boolean {
        return oldItem == newItem
    }
}