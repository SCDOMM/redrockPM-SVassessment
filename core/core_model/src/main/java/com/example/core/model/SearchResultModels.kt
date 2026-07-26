package com.example.core.model

/**
 * description ：搜索结果封装
 */
data class SearchResultData(
    val videoList: List<MetroData> = emptyList(),
    val creatorList: List<MetroData> = emptyList(),
    val articleList: List<MetroData> = emptyList(),
    val topicList: List<MetroData> = emptyList(),
    val userList: List<MetroData> = emptyList(),
    val query: String
)
