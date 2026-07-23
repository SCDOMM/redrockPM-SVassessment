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
import com.example.core.model.MetroData
import com.example.ept.search.R
import com.example.core.common.findDelimiterIndex

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultGraphicsAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:00
 *
 */
class ResultGraphicsAdapter :
    ListAdapter<MetroData, ResultGraphicsAdapter.GraphicsViewHolder>(GraphicsDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GraphicsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_graphics, parent, false)
        return GraphicsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GraphicsViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class GraphicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGraphicsCoverItem: ImageView =
            itemView.findViewById(R.id.iv_graphics_cover_item)
        val tvGraphicsTitleItem: TextView =
            itemView.findViewById(R.id.tv_graphics_title_item)
        val tvGraphicsDescItem: TextView =
            itemView.findViewById(R.id.tv_graphics_desc_item)
        val tvGraphicsInteractItem: TextView =
            itemView.findViewById(R.id.tv_graphics_interact_item)
        val ivGraphicsStackItem: ImageView =
            itemView.findViewById(R.id.iv_graphics_stack_item)

        fun bindData(metroData: MetroData) {
            metroData.imageCount?.let {
                if (it > 1) {
                    ivGraphicsStackItem.visibility = ImageView.VISIBLE
                    ivGraphicsStackItem.setImageResource(R.drawable.ic_search_stack)
                } else {
                    ivGraphicsStackItem.visibility = ImageView.GONE
                }
            }
            Glide.with(ivGraphicsCoverItem.context)
                .load(metroData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivGraphicsCoverItem)
            val dashIndex = findDelimiterIndex(metroData.title, "\n")
            if (dashIndex >= 0) {
                val mainPart = metroData.title?.substring(0, dashIndex)?.trim()
                val subPart = metroData.title?.substring(dashIndex + 1)?.trim()
                tvGraphicsTitleItem.text = mainPart
                tvGraphicsDescItem.text = subPart
            } else {
                tvGraphicsTitleItem.text = metroData.title
                tvGraphicsDescItem.text = ""
            }
            if (metroData.title == null) {
                tvGraphicsInteractItem.text = "0人互动"
            } else {
                tvGraphicsInteractItem.text =
                    "${metroData.consumption?.likeCount}人喜欢,${metroData.consumption?.collectionCount}人收藏"
            }
        }
    }
}

object GraphicsDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.imageId == p1.imageId
    }

    override fun areContentsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.link == p1.link
    }
}