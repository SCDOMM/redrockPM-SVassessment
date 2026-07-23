package com.example.ept.dicover.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ept.dicover.R

/**
 * 分类数据项
 * @param name 分类名称
 * @param pageLabel 分类页面标识符 (如 "topic_detail-2034431085")
 * @param iconUrl 分类图标网络 URL
 */
data class CategoryItem(
    val name: String,
    val pageLabel: String,
    val iconUrl: String
)
/**
 * description ： 分类网格Adapter
 * email : 3014386984@qq.com
 * date : 2026/7/17 11:39
 */

class CategoryAdapter(
    /** 分类点击回调，跳转到分类详情页 */
    private val onItemClick: (CategoryItem) -> Unit = {}
) : ListAdapter<CategoryItem, CategoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** ViewHolder，包含分类图标和名称 */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_doscovery_category)
        val text: TextView = view.findViewById(R.id.tv_doscovery_category)
    }

    /** 创建 ViewHolder，加载分类网格项布局 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_grid, parent, false)
        return ViewHolder(view)
    }

    /** 绑定数据到 ViewHolder */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.text.text = item.name
        Glide.with(holder.icon.context)
            .load(item.iconUrl)
            .into(holder.icon)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(old: CategoryItem, new: CategoryItem) = old.name == new.name
            override fun areContentsTheSame(old: CategoryItem, new: CategoryItem) = old == new
        }
    }
}
