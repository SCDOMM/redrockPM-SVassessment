package com.example.ept.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


/**
 * description ： 分类详情页列表适配器，支持分区标题、视频卡片与图片帖子混合展示
 * email : 3014386984@qq.com
 * date : 2026/7/21 15:53
 */
sealed class CategoryItem {
    data class Header(val text: String) : CategoryItem()
    data class Video(
        val videoId: Long,
        val title: String,
        val coverUrl: String,
        val duration: Long,
        val authorName: String,
        val authorIcon: String,
        val description: String,
        val playUrl: String = "",
        val category: String = "",
        val collectionCount: Int = 0,
        val shareCount: Int = 0,
        val replyCount: Int = 0,
        val webUrl: String = ""
    ) : CategoryItem()
    data class Image(
        val id: Long,
        val title: String,
        val imageUrls: List<String>,
        val authorName: String,
        val authorIcon: String,
        val description: String = "",
        val likeCount: Int = 0,
        val commentCount: Int = 0,
        val collectionCount: Int = 0,
        val shareCount: Int = 0
    ) : CategoryItem()
}

class CategoryDetailAdapter(
    private val onVideoClick: (CategoryItem.Video) -> Unit = {},
    private val onShareClick: (CategoryItem.Video) -> Unit = {}
) : ListAdapter<CategoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        /** 分区标题类型 */
        private const val TYPE_HEADER = 0
        /** 视频卡片类型 */
        private const val TYPE_VIDEO = 1
        /** 图片帖子类型 */
        private const val TYPE_IMAGE = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
                return when {
                    old is CategoryItem.Header && new is CategoryItem.Header -> old.text == new.text
                    old is CategoryItem.Video && new is CategoryItem.Video -> old.videoId == new.videoId
                    old is CategoryItem.Image && new is CategoryItem.Image -> old.id == new.id
                    else -> false
                }
            }

            override fun areContentsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
                return old == new
            }
        }
    }

    /** 分区标题视图持有者 */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tv_header)
    }

    /** 视频卡片视图持有者 */
    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_hot_title)
        val duration: TextView = view.findViewById(R.id.actv_hot_geration)
        val authorIcon: ImageView = view.findViewById(R.id.iv_hot_author)
        val title: TextView = view.findViewById(R.id.actv_hot_description)
        val description: TextView = view.findViewById(R.id.actv_hot_author)
        val collectionCount: TextView = view.findViewById(R.id.tv_category_collection)
        val replyIcon: ImageView = view.findViewById(R.id.iv_category_reply)
        val replyCount: TextView = view.findViewById(R.id.tv_category_reply)
        val shareIcon: ImageView = view.findViewById(R.id.iv_category_share)
        val shareCount: TextView = view.findViewById(R.id.tv_category_share)
    }

    /** 图片帖子视图持有者 */
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorIcon: ImageView = view.findViewById(R.id.iv_author_avatar)
        val authorName: TextView = view.findViewById(R.id.tv_author_name)
        val title: TextView = view.findViewById(R.id.tv_content_title)
        val rvImages: RecyclerView = view.findViewById(R.id.rv_images)
        val likeCount: TextView = view.findViewById(R.id.tv_like_count)
        val collectionCount: TextView = view.findViewById(R.id.tv_collection_count)
        val commentCount: TextView = view.findViewById(R.id.tv_comment_count)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryItem.Header -> TYPE_HEADER
            is CategoryItem.Video -> TYPE_VIDEO
            is CategoryItem.Image -> TYPE_IMAGE
        }
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_image_post, parent, false)
                ImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_video, parent, false)
                VideoViewHolder(view)
            }
        }
    }

    /** 绑定视图数据 */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryItem.Header -> {
                (holder as HeaderViewHolder).text.text = item.text
            }
            is CategoryItem.Video -> {
                val vh = holder as VideoViewHolder
                vh.title.text = item.title
                vh.description.text = item.authorName

                val mins = item.duration / 60
                val secs = item.duration % 60
                vh.duration.text = String.format("%02d:%02d", mins, secs)

                vh.collectionCount.text = formatCount(item.collectionCount)
                vh.replyCount.text = formatCount(item.replyCount)
                vh.shareCount.text = formatCount(item.shareCount)

                Glide.with(vh.itemView)
                    .load(item.coverUrl)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(vh.cover)

                if (item.authorIcon.isNotEmpty()) {
                    Glide.with(vh.itemView)
                        .load(item.authorIcon)
                        .transform(CircleCrop())
                        .into(vh.authorIcon)
                }

                vh.itemView.setOnClickListener { onVideoClick(item) }
                vh.shareIcon.setOnClickListener { onShareClick(item) }
            }
            is CategoryItem.Image -> {
                val vh = holder as ImageViewHolder
                vh.authorName.text = item.authorName
                vh.title.text = item.title
                vh.likeCount.text = formatCount(item.likeCount)
                vh.collectionCount.text = formatCount(item.collectionCount)
                vh.commentCount.text = formatCount(item.commentCount)

                if (item.authorIcon.isNotEmpty()) {
                    Glide.with(vh.itemView)
                        .load(item.authorIcon)
                        .transform(CircleCrop())
                        .into(vh.authorIcon)
                }

                // 图片网格
                val imageAdapter = CategoryImageAdapter(item.imageUrls)
                vh.rvImages.apply {
                    layoutManager = GridLayoutManager(vh.itemView.context, 2)
                    adapter = imageAdapter
                }
            }
        }
    }

    /** 格式化计数显示，超过10000显示为万 */
    private fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }
}

/**
 * 分类详情页图片网格适配器
 */
class CategoryImageAdapter(
    /** 图片URL列表 */
    private val imageUrls: List<String>
) : RecyclerView.Adapter<CategoryImageAdapter.ImageViewHolder>() {

    /** 图片网格视图持有者 */
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_image)
    }

    /** 创建视图持有者 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_image, parent, false)
        return ImageViewHolder(view)
    }

    /** 绑定视图数据 */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.image.context)
            .load(imageUrls[position])
            .transform(CenterCrop(), RoundedCorners(8))
            .into(holder.image)
    }

    override fun getItemCount() = imageUrls.size
}
