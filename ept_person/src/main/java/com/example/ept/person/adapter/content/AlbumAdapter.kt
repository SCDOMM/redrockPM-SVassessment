package com.example.ept.person.adapter.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.AlbumData
import com.example.ept.person.R

/**   
 * 包名称： com.example.ept.person.pgc.adapter
 * 类名称：AlbumAdapter
 * 类描述：专辑页面的Adapter，包含的item是可以横向滑动的三张图片
 * 创建人：韦西波
 * 创建时间：2026-07-21 11:22
 *
 */
class AlbumAdapter : ListAdapter<AlbumData, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hsAlbumDefaultItem: HorizontalScrollView=itemView.findViewById(R.id.hs_album_default_item)
        val llAlbumContainerItem: LinearLayout = itemView.findViewById(R.id.ll_album_container_item)
        val ivAlbumCoverItem: ImageView = itemView.findViewById(R.id.iv_album_cover_item)
        val tvAlbumTitleItem: TextView = itemView.findViewById(R.id.tv_album_title_item)
        val tvAlbumDescItem: TextView = itemView.findViewById(R.id.tv_album_desc_item)
        fun bindData(albumData: AlbumData) {
            Glide.with(ivAlbumCoverItem.context)
                .load(albumData.albumCoverUrl)
                .error(R.drawable.eyepetater)
                .placeholder(R.drawable.eyepetater)
                .into(ivAlbumCoverItem)
            tvAlbumTitleItem.text = albumData.albumName
            tvAlbumDescItem.text = albumData.albumDescription

            llAlbumContainerItem.removeAllViews()
            val context = llAlbumContainerItem.context
            albumData.videos.forEach { video ->
                val videoView = LayoutInflater.from(context)
                    .inflate(R.layout.item_item_album, llAlbumContainerItem, false)
                val ivCover = videoView.findViewById<ImageView>(R.id.iv_new_cover_item_item)
                Glide.with(ivCover.context)
                    .load(video.coverUrl)
                    .error(R.drawable.eyepetater)
                    .placeholder(R.drawable.eyepetater)
                    .into(ivCover)

                val screenWidth = context.resources.displayMetrics.widthPixels
                val itemWidth = if (albumData.videos.size == 1) {
                    screenWidth
                } else {
                    (screenWidth * 0.8).toInt()
                }
                videoView.layoutParams = LinearLayout.LayoutParams(
                    itemWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llAlbumContainerItem.addView(videoView)
            }
        }
    }
}

object AlbumDiffCallBack : DiffUtil.ItemCallback<AlbumData>() {
    override fun areItemsTheSame(
        oldItem: AlbumData,
        newItem: AlbumData
    ): Boolean {
        return oldItem.albumName == newItem.albumName
    }

    override fun areContentsTheSame(
        oldItem: AlbumData,
        newItem: AlbumData
    ): Boolean {
        return oldItem.albumLink == newItem.albumLink
    }
}
