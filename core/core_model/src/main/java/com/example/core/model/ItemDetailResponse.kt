package com.example.core.model

/**
 * description ： 视频详情接口响应模型
 * email : 3014386984@qq.com
 * date : 2026/7/21
 */
data class ItemDetailResponse(
    val code: Int = -1,
    val message: Any? = null,
    val result: ItemDetailResult? = null
)

data class ItemDetailResult(
    val item_id: String = "",
    val title: String = "",
    val text: String = "",
    val category: ItemDetailCategory? = null,
    val author: ItemDetailAuthor? = null,
    val consumption: ItemDetailConsumption? = null,
    val video: ItemDetailVideo? = null,
    val resource_type: String = "",
    val liked: Boolean = false,
    val collected: Boolean = false
)

data class ItemDetailCategory(
    val id: Int = 0,
    val name: String = ""
)

data class ItemDetailAuthor(
    val uid: Long = 0,
    val nick: String = "",
    val description: String = "",
    val avatar: ItemDetailAvatar? = null,
    val followed: Boolean = false
)

data class ItemDetailAvatar(
    val url: String = "",
    val img_info: ItemDetailImgInfo? = null,
    val shape: String = ""
)

data class ItemDetailConsumption(
    val like_count: Int = 0,
    val collection_count: Int = 0,
    val comment_count: Int = 0,
    val share_count: Int = 0
)

data class ItemDetailVideo(
    val video_id: String = "",
    val title: String = "",
    val duration: ItemDetailDuration? = null,
    val play_url: String = "",
    val play_info: List<ItemDetailPlayInfo>? = null,
    val cover: ItemDetailCover? = null,
    val tags: List<ItemDetailTag>? = null
)

data class ItemDetailDuration(
    val value: Long = 0,
    val text: String = ""
)

data class ItemDetailCover(
    val url: String = "",
    val img_info: ItemDetailImgInfo? = null
)

data class ItemDetailImgInfo(
    val width: Int = 0,
    val height: Int = 0,
    val scale: Float = 0f
)

data class ItemDetailPlayInfo(
    val height: String = "",
    val width: String = "",
    val name: String = "",
    val type: String = "",
    val url: String = "",
    val url_list: List<ItemDetailUrlItem>? = null
)

data class ItemDetailUrlItem(
    val name: String = "",
    val url: String = "",
    val size: Long = 0
)

data class ItemDetailTag(
    val id: Long = 0,
    val title: String = "",
    val link: String = ""
)
