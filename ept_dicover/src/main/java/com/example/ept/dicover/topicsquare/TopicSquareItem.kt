package com.example.ept.dicover.topicsquare

/**
 * description ： 话题广场列表项
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
data class TopicSquareItem(
    val id: Long,
    val title: String,
    val description: String,
    val coverUrl: String,
    val participantCount: String,
    val pageLabel: String
)
