package com.example.ept.dicover.lightTopic

import com.example.ept.dicover.topicdetail.TopicPlaylistVideo

/**
 * description ： 主题播单列表项
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
data class LightTopicItem(
    val topicId: Int,
    val title: String,
    val description: String,
    val videos: List<TopicPlaylistVideo>
)
