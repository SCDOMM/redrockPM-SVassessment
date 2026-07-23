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
import com.example.ept.search.utils.findDelimiterIndex

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultArticlesAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:00
 *
 */
class ResultArticlesAdapter :
    ListAdapter<MetroData, ResultArticlesAdapter.ArticlesViewHolder>(ArticlesDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_articles, parent, false)
        return ArticlesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ArticlesViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class ArticlesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivResultArticlesCoverItem: ImageView =
            itemView.findViewById(R.id.iv_result_articles_cover_item)
        val tvResultArticlesTitleItem: TextView =
            itemView.findViewById(R.id.tv_result_articles_title_item)
        val tvResultArticlesDescItem: TextView =
            itemView.findViewById(R.id.tv_result_articles_desc_item)
        val tvResultArticlesInteractItem: TextView =
            itemView.findViewById(R.id.tv_result_articles_interact_item)
        val ivResultArticlesStackItem: ImageView =
            itemView.findViewById(R.id.iv_result_articles_stack_item)

        fun bindData(metroData: MetroData) {
            metroData.imageCount?.let {
                if (it > 1) {
                    ivResultArticlesStackItem.visibility = ImageView.VISIBLE
                    ivResultArticlesStackItem.setImageResource(R.drawable.ic_search_stack)
                } else {
                    ivResultArticlesStackItem.visibility = ImageView.GONE
                }
            }
            Glide.with(ivResultArticlesCoverItem.context)
                .load(metroData.cover?.url)
                .placeholder(R.drawable.eyepetater)
                .error(R.drawable.eyepetater)
                .into(ivResultArticlesCoverItem)
            val dashIndex = findDelimiterIndex(metroData.title, "\n")
            if (dashIndex >= 0) {
                val mainPart = metroData.title?.substring(0, dashIndex)?.trim()
                val subPart = metroData.title?.substring(dashIndex + 1)?.trim()
                tvResultArticlesTitleItem.text = mainPart
                tvResultArticlesDescItem.text = subPart
            } else {
                tvResultArticlesTitleItem.text = metroData.title
                tvResultArticlesDescItem.text = ""
            }
            if (metroData.title == null) {
                tvResultArticlesInteractItem.text = "0人互动"
            } else {
                tvResultArticlesInteractItem.text =
                    "${metroData.consumption?.likeCount}人喜欢,${metroData.consumption?.collectionCount}人收藏"
            }
        }
    }
}

object ArticlesDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.imageId == p1.imageId
    }

    override fun areContentsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.link == p1.link
    }
}