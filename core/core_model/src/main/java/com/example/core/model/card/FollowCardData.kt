package com.example.core.model.card

import com.example.core.model.author.Follow
import com.example.core.model.navigation.TextHeaderData
import com.example.core.model.videoEntity.VideoData

data class FollowCardData(
    val dataType: String,
    val id: Long,
    val content: ContentWrapper?,
    val header: TextHeaderData?,
    val follow: Follow?
)

data class ContentWrapper(
    val type: String,
    val data: VideoData?
)
