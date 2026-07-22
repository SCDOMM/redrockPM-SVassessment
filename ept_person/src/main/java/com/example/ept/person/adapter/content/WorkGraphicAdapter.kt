package com.example.ept.person.adapter.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.WorkImage
import com.example.ept.person.R

/**   
 * 包名称： com.example.ept.person.content.adapter
 * 类名称：GraphicAdapter
 * 类描述：用于展示作品页面中
 * 创建人：韦西波
 * 创建时间：2026-07-22 09:50
 *
 */
class WorkGraphicAdapter : ListAdapter<WorkImage, WorkGraphicAdapter.GraphicViewHolder>(WorkGraphicDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GraphicViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_item_graphic,parent,false)
        return GraphicViewHolder(view)
    }
    override fun onBindViewHolder(
        holder: GraphicViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }
    inner class GraphicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGraphicCoverItemItem: ImageView=itemView.findViewById(R.id.iv_graphic_cover_item_item)
        fun bindData(workImage: WorkImage) {
            Glide.with(ivGraphicCoverItemItem.context)
                .load(workImage.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivGraphicCoverItemItem)
        }
    }
}
object WorkGraphicDiffCallback: DiffUtil.ItemCallback<WorkImage>(){
    override fun areItemsTheSame(
        oldItem: WorkImage,
        newItem: WorkImage
    ): Boolean {
        return oldItem.imageId==newItem.imageId
    }

    override fun areContentsTheSame(
        oldItem: WorkImage,
        newItem: WorkImage
    ): Boolean {
        return oldItem.cover?.url==newItem.cover?.url
    }
}


