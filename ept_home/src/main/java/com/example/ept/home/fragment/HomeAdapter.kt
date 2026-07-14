package com.example.ept.home.fragment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.core.model.videoEntity.VideoData

/**   
 * 包名称： com.example.ept.home.fragment
 * 类名称：HomeAdapter
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 16:44
 *
 */
class HomeAdapter : ListAdapter<VideoData, HomeAdapter.ViewHolder>(HomeDiffCallback){
    private val videoList: MutableList<VideoData> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){


    }




}
object HomeDiffCallback: DiffUtil.ItemCallback<VideoData>(){
    override fun areItemsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem.id==newItem.id
    }
    override fun areContentsTheSame(
        oldItem: VideoData,
        newItem: VideoData
    ): Boolean {
        return oldItem==newItem
    }
}
