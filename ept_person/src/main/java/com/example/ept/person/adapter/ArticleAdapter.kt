package com.example.ept.person.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.official.WorkImage
import com.example.ept.person.R

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：ArticleAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-22 09:50
 *
 */
class ArticleAdapter : ListAdapter<WorkImage, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_item_vertical_article,parent,false)
        return ArticleViewHolder(view)
    }
    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItemItemContent: ImageView=itemView.findViewById(R.id.iv_item_item_content)
        fun bindData(workImage: WorkImage) {
            Glide.with(ivItemItemContent.context)
                .load(workImage.cover?.url)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivItemItemContent)
        }

    }


}
object ArticleDiffCallback: DiffUtil.ItemCallback<WorkImage>(){
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


