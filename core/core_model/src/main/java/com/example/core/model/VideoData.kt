package com.example.core.model

/**
 * 包名称： com.example.core.model
 * 类名称：VideoData
 * 类描述：视频的数据类型
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:42
 *
 */
data class VideoData(
    val dataType: String = "",                // "VideoBeanForClient"
    val id: Long = 0,                         // ⭐ 视频唯一ID
    val title: String = "",                   // ⭐ 标题
    val description: String = "",             // 描述
    val library: String = "",                 // 库类型
    val category: String = "",                // ⭐ 分类
    // 封面
    val cover: Cover? = null,                 // ⭐ 封面图
    // 播放信息
    val playUrl: String = "",                 // ⭐ 播放地址
    val playInfo: List<PlayInfo> = emptyList(),// ⭐ 各清晰度信息
    val thumbPlayUrl: String? = null,         // 缩略图播放地址
    val duration: Long = 0,                   // ⭐ 时长（秒）
    // 作者/来源
    val author: Author? = null,               // ⭐ 作者
    val provider: Provider? = null,           // 视频来源（YouTube等）
    // 互动数据
    val consumption: Consumption? = null,     // ⭐ 收藏/分享/评论数
    // 标签
    val tags: List<Tag>? = null,              // ⭐ 视频标签
    val label: Label? = null,                 // 单个标签（可能替代 labels）
    val labelList: List<Label>? = null,       // 标签列表
    // 链接
    val webUrl: WebUrl? = null,               // 网页链接
    // 时间
    val releaseTime: Long? = null,            // ⭐ 发布时间
    val date: Long? = null,                   // 日期时间戳
    // 广告
    val ad: Boolean = false,                  // 是否广告
    val adTrack: Any? = null,                 // 广告追踪
    // 其他
    val type: String? = null,                 // "NORMAL" / "BANNER" / ...
    val resourceType: String? = null,         // 资源类型 "video"
    val slogan: String? = null,               // 标语
    val descriptionEditor: String? = null,    // 编辑版描述
    val collected: Boolean = false,           // 是否收藏
    val reallyCollected: Boolean = false,     // 真实收藏状态
    val played: Boolean = false,              // 是否播放过
    val subtitles: List<Any>? = null,         // 字幕
    val lastViewTime: Long? = null,           // 最后观看时间
    val playlists: Any? = null,               // 播放列表
    val src: Int? = null,                     // 来源标记
    val recallSource: String? = null,         // 召回来源
    val recall_source: String? = null,        // 召回来源（备用字段）
    val idx: Int? = null,                     // 索引
    val campaign: Any? = null,                // 活动信息
    val waterMarks: Any? = null,              // 水印
    val titlePgc: String? = null,             // PGC标题
    val descriptionPgc: String? = null,       // PGC描述
    val remark: String? = null,               // 备注
    val ifLimitVideo: Boolean? = null,        // 是否限制视频
    val searchWeight: Int? = null,            // 搜索权重
    val brandWebsiteInfo: Any? = null,        // 品牌网站信息
    val videoPosterBean: Any? = null,         // 视频海报
    val shareAdTrack: Any? = null,            // 分享广告追踪
    val favoriteAdTrack: Any? = null,         // 收藏广告追踪
    val webAdTrack: Any? = null,              // 网页广告追踪
    val promotion: Any? = null,               // 推广信息
    val status: String? = null                // 状态
)

data class UgcSelectedData(
    val dataType: String,           // "UgcSelected"
    val id: Long,
    val title: String?,
    val description: String?,
    val cover: Cover?,
    val playUrl: String?,
    val duration: Long?,
    val author: Author?,
    val consumption: Consumption?,
    val webUrl: WebUrl?,
    val tags: List<Tag>?
)