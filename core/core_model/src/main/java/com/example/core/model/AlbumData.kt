package com.example.core.model

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model.official
 * 类名称：AlbumData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-21 11:38
 *
 */
// 这里的数据类不是原生传入的，是为了适配Adapter存在的
data class AlbumData(
    val albumName: String,           // 专辑名称，如 "VLOG"
    val albumDescription: String,    // 专辑描述，如 "专辑 · 2019-11-25 更新 / 18 个视频"
    val albumCoverUrl: String,       // 专辑封面图URL（来自footer中avatar.url）
    val albumLink: String,           // 点击专辑跳转的链接
    val videos: List<AlbumVideoPreview> // 该专辑下的视频预览列表（最多3个）
)
data class AlbumVideoPreview(
    val videoId: String,
    val title: String,
    val coverUrl: String,
    val duration: Int
)
data class CallAlbumCardListResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: CallAlbumCardListResult? = null
)
data class CallAlbumCardListResult(
    @SerializedName("item_list")
    val itemList: List<SearchCard> = emptyList(),  // 复用 SearchCard
    @SerializedName("item_count")
    val itemCount: Int = 0,
    @SerializedName("last_item_id")
    val lastItemId: Int = 0
)