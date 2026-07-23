package com.example.ept.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.search.R

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：SuggestionAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-19 19:49
 *
 */
class PreSearchAdapter :
    ListAdapter<String, PreSearchAdapter.PreSearchViewHolder>(PreSearchDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreSearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_presearch, parent, false)
        return PreSearchViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PreSearchViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }


    inner class PreSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvPreSearchDefault: TextView =
            itemView.findViewById(R.id.tv_presearch_default)

        fun bindData(str: String) {
            tvPreSearchDefault.text = str
        }
    }
}

object PreSearchDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem.contentEquals(newItem)
    }


}