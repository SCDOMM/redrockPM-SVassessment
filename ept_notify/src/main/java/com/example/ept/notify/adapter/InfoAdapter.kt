package com.example.ept.notify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.NoticeItem
import com.example.ept.notify.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**   
 * 包名称： com.example.ept.home.adapter
 * 类名称：InfoAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-15 17:06
 *
 */
class InfoAdapter : ListAdapter<NoticeItem, InfoAdapter.ViewHolder>(InfoDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivInfoDefaultItem: ImageView = itemView.findViewById(R.id.iv_info_default_item)
        val tvInfoTitleItem: TextView = itemView.findViewById(R.id.tv_info_title_item)
        val tvInfoDescItem: TextView = itemView.findViewById(R.id.tv_info_desc_item)
        val tvInfoDateItem: TextView = itemView.findViewById(R.id.tv_info_date_item)
        fun bindData(msgData: NoticeItem) {
            Glide.with(itemView)
                .load(msgData.avatarList.firstOrNull())
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivInfoDefaultItem)

            tvInfoTitleItem.text = msgData.title ?: "开眼通知"
            tvInfoDescItem.text = msgData.desc
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            tvInfoDateItem.text = sdf.format(Date(msgData.time ))

        }
    }
}

object InfoDiffCallback : DiffUtil.ItemCallback<NoticeItem>() {
    override fun areItemsTheSame(p0: NoticeItem, p1: NoticeItem): Boolean {
        return p0.id == p1.id
    }

    override fun areContentsTheSame(p0: NoticeItem, p1: NoticeItem): Boolean {
        return p0.link == p1.link
    }
}