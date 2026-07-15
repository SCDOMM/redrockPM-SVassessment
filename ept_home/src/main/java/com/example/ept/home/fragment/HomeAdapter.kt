package com.example.ept.home.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.core.model.videoEntity.VideoData
import com.example.ept.home.R
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

/**   
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeAdapter : ListAdapter<VideoData, RecyclerView.ViewHolder>(HomeDiffCallback) {
    private val videoList: MutableList<VideoData> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        if (position ==0) {
            return 0
        }else if(position in 1..<5){
            return 1
        }
        return 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview1_home, parent, false)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(view)
            VIEW_TYPE_SELECTED-> SelectedViewHolder(view)
            else-> NormalViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gsyvHomeDefaultItem1=view.findViewById<GSYVideoPlayer>(R.id.gsyv_home_item1)
        val ivHomeDefaultItem1=view.findViewById<ImageView>(R.id.iv_home_default_item1)
        val tvHomeTitleItem1=view.findViewById<TextView>(R.id.tv_home_title_item1)
        val tvHomeLabelItem1=view.findViewById<TextView>(R.id.tv_home_label_item1)
        val tvHomeDescItem1=view.findViewById<TextView>(R.id.tv_home_desc_item1)
        val tvHomeDurationItem1=view.findViewById<TextView>(R.id.tv_home_duration_item1)
        fun bindData(){

        }
    }

    inner class SelectedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeCoverItem2=view.findViewById<GSYVideoPlayer>(R.id.iv_home_cover_item2)
        val ivHomeDefaultItem2=view.findViewById<ImageView>(R.id.iv_home_default_item2)
        val tvHomeTitleItem2=view.findViewById<TextView>(R.id.tv_home_title_item2)
        val tvHomeLabelItem2=view.findViewById<TextView>(R.id.tv_home_label_item2)
        val tvHomeDescItem2=view.findViewById<TextView>(R.id.tv_home_desc_item2)
        val tvHomeDurationItem2=view.findViewById<TextView>(R.id.tv_home_duration_item2)
        fun bindData(){

        }
    }

    inner class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeCoverItem3=view.findViewById<GSYVideoPlayer>(R.id.iv_home_cover_item3)
        val tvHomeTitleItem3=view.findViewById<TextView>(R.id.tv_home_title_item3)
        val tvHomeLabelItem3=view.findViewById<TextView>(R.id.tv_home_label_item3)
        val tvHomeDurationItem3=view.findViewById<TextView>(R.id.tv_home_duration_item3)
        fun bindData(){

        }
    }
}

object HomeDiffCallback : DiffUtil.ItemCallback<VideoData>() {
    override fun areItemsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem == newItem
    }
}

const val VIEW_TYPE_HEADER = 0
const val VIEW_TYPE_SELECTED = 1
const val VIEW_TYPE_NORMAL = 2