package com.example.ept.search.adapter.resultcontent

import android.util.Log
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
 * 类名称：ResultCreatorsAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:01
 *
 */
class ResultCreatorsAdapter :
    ListAdapter<MetroData, ResultCreatorsAdapter.CreatorViewHolder>(CreatorDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreatorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_creators, parent, false)
        return CreatorViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CreatorViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class CreatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivResultCreatorCoverItem: ImageView =
            itemView.findViewById(R.id.iv_result_creators_cover_item)
        val tvResultCreatorTitleItem: TextView =
            itemView.findViewById(R.id.tv_result_creators_name_item)
        val tvResultCreatorDescItem: TextView =
            itemView.findViewById(R.id.tv_result_creators_desc_item)
        val ivResultCreatorAddItem: ImageView = itemView.findViewById(R.id.iv_result_creators_add)
        fun bindData(metroData: MetroData) {
            Glide.with(ivResultCreatorCoverItem.context)
                .load(metroData.avatar?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivResultCreatorCoverItem)
            tvResultCreatorTitleItem.text = metroData.nick
            tvResultCreatorDescItem.text = metroData.description
        }
    }
}

object CreatorDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.uid == p1.uid
    }

    override fun areContentsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.link == p1.link
    }
}