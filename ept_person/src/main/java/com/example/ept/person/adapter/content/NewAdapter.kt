package com.example.ept.person.adapter.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.MetroData
import com.example.ept.person.R
import com.google.android.material.imageview.ShapeableImageView

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：NewAdapter
 * 类描述：用于展示"最近更新"横向滑动RV的视频
 * 创建人：韦西波
 * 创建时间：2026-07-22 15:22
 *
 */
class RecentAdapter : ListAdapter<MetroData, RecentAdapter.RecentVideoViewHolder>(
    NewDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentVideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_item_new, parent, false)

        val screenWidth = view.context.resources.displayMetrics.widthPixels
        val itemWidth= (screenWidth * 0.8).toInt()

        view.layoutParams = LinearLayout.LayoutParams(
            itemWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        return RecentVideoViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecentVideoViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
    inner class RecentVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivNewCoverItemItem: ImageView = itemView.findViewById(R.id.iv_new_cover_item_item)
        val ivNewAuthorCoverItemItem: ShapeableImageView = itemView.findViewById(R.id.iv_new_author_cover_item_item)
        val tvNewTitleItemItem: TextView = itemView.findViewById(R.id.tv_new_title_item_item)
        val tvNewAuthorNameItemItem: TextView = itemView.findViewById(R.id.tv_new_author_name_item_item)
        val tvNewLabelItemItem: TextView = itemView.findViewById(R.id.tv_new_label_item_item)
        val tvNewDurationItemItem: TextView = itemView.findViewById(R.id.tv_new_duration_item_item)
        fun bindData(data: MetroData) {
            Glide.with(ivNewCoverItemItem.context)
                .load(data.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivNewCoverItemItem)
            Glide.with(ivNewAuthorCoverItemItem.context)
                .load(data.author?.avatar?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivNewAuthorCoverItemItem)
            tvNewTitleItemItem.text = data.title ?: ""
            tvNewAuthorNameItemItem.text = data.author?.nick ?: ""
            tvNewLabelItemItem.text = data.tags?.firstOrNull()?.title ?: ""
            tvNewDurationItemItem.text = data.duration?.text ?: ""

        }
    }
}
object NewDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(oldItem: MetroData, newItem: MetroData): Boolean {
        return oldItem.resourceId == newItem.resourceId
    }
    override fun areContentsTheSame(oldItem: MetroData, newItem: MetroData): Boolean {
        return oldItem == newItem
    }
}