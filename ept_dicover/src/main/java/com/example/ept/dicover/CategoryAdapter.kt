package com.example.ept.dicover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 分类数据项
 * @param name 分类名称
 * @param apiUrl 分类详情页 API 地址
 * @param iconRes 分类图标资源 ID
 */
data class CategoryItem(
    val name: String,
    val apiUrl: String,
    @DrawableRes val iconRes: Int
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
        holder.icon.setImageResource(item.iconRes)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        /** DiffUtil 回调，用于高效更新列表 */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(old: CategoryItem, new: CategoryItem) = old.name == new.name
            override fun areContentsTheSame(old: CategoryItem, new: CategoryItem) = old == new
        }

        /** 分类名称到图标的映射表 */
        private val categoryIconMap = mapOf(
            "广告" to R.drawable.ic_category_ad,
            "生活" to R.drawable.ic_category_life,
            "动画" to R.drawable.ic_category_animation,
            "搞笑" to R.drawable.ic_category_funny,
            "开胃" to R.drawable.ic_category_appetizing,
            "创意" to R.drawable.ic_category_creative,
            "运动" to R.drawable.ic_category_sport,
            "音乐" to R.drawable.ic_category_music,
            "萌宠" to R.drawable.ic_category_pet,
            "剧情" to R.drawable.ic_category_drama,
            "科技" to R.drawable.ic_category_tech,
            "旅行" to R.drawable.ic_category_travel,
            "影视" to R.drawable.ic_category_film,
            "记录" to R.drawable.ic_category_record,
            "游戏" to R.drawable.ic_category_game,
            "综艺" to R.drawable.ic_category_variety,
            "时尚" to R.drawable.ic_category_fashion,
            "集锦" to R.drawable.ic_category_collection
        )

        /**
         * 根据分类名称获取对应的图标资源 ID
         */
        fun getIconForCategory(name: String): Int {
            return categoryIconMap[name] ?: R.drawable.ic_launcher_foreground
        }
    }
}
