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

data class CategoryItem(
    val name: String,
    val apiUrl: String,
    @DrawableRes val iconRes: Int
)

class CategoryAdapter(
    private val onItemClick: (CategoryItem) -> Unit = {}
) : ListAdapter<CategoryItem, CategoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_doscovery_category)
        val text: TextView = view.findViewById(R.id.tv_doscovery_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.text.text = item.name
        holder.icon.setImageResource(item.iconRes)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(old: CategoryItem, new: CategoryItem) = old.name == new.name
            override fun areContentsTheSame(old: CategoryItem, new: CategoryItem) = old == new
        }

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

        fun getIconForCategory(name: String): Int {
            return categoryIconMap[name] ?: R.drawable.ic_launcher_foreground
        }
    }
}
