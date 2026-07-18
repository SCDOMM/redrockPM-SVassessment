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

/**   
 * 包名称： com.example.ept.search.adapter
 * 类名称：ResultUsersFragment
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 15:01
 *
 */
class ResultUsersAdapter: ListAdapter<MetroData, ResultUsersAdapter.UsersViewHolder>(UsersDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.itemview_users,parent,false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UsersViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }
    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivResultUsersCoverItem: ImageView =
            itemView.findViewById(R.id.iv_result_users_cover_item)
        val tvResultUsersTitleItem: TextView =
            itemView.findViewById(R.id.tv_result_users_title_item)
        val tvResultUsersDescItem: TextView =
            itemView.findViewById(R.id.tv_result_users_desc_item)
        fun bindData(metroData: MetroData){
            ivResultUsersCoverItem.let { player->
                Glide.with(player.context).load(metroData.avatar?.url).into(player)
            }
            tvResultUsersTitleItem.text=metroData.title
            tvResultUsersDescItem.text=metroData.description
        }

    }

}
object UsersDiffCallback : DiffUtil.ItemCallback<MetroData>() {
    override fun areItemsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.uid ==p1.uid
    }

    override fun areContentsTheSame(p0: MetroData, p1: MetroData): Boolean {
        return p0.link==p1.link
    }
}