package com.example.ept.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.MessageData
import com.example.ept.home.R
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
class InfoAdapter : ListAdapter<MessageData, InfoAdapter.ViewHolder>(InfoDiffCallback) {
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
        fun bindData(msgData: MessageData) {
            if (msgData.icon != null) {
                Glide.with(itemView).load(msgData.icon).into(ivInfoDefaultItem)
            } else {
                ivInfoDefaultItem.setImageResource(R.drawable.eyepetater)
            }
            tvInfoTitleItem.text = msgData.title ?: "开眼通知"
            tvInfoDescItem.text = msgData.content
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            tvInfoDateItem.text = sdf.format(Date(msgData.date))

        }
    }
}

object InfoDiffCallback : DiffUtil.ItemCallback<MessageData>() {
    override fun areItemsTheSame(p0: MessageData, p1: MessageData): Boolean {
        return p0.id == p1.id
    }

    override fun areContentsTheSame(p0: MessageData, p1: MessageData): Boolean {
        return p0 == p1
    }
}