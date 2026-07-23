package com.example.ept.dicover.topicdetail

/**
 * description ： 主题播单视频项
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
data class TopicPlaylistVideo(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val duration: Long,
    val authorName: String,
    val authorIcon: String,
    val description: String,
    val playUrl: String
)
