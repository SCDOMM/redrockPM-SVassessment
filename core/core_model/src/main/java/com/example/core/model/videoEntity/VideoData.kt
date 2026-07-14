package com.example.core.model.videoEntity

import com.example.core.model.TitleItem
import com.example.core.model.author.*
import com.example.core.model.interact.Consumption
import com.example.core.model.media.*
import com.example.core.model.tag.*

/**
 * 包名称： com.example.core.model
 * 类名称：VideoData
 * 类描述：视频的数据类型
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:42
 *
 */
data class VideoData(
    val dataType: String,               // "VideoBeanForClient"
    val id: Long,                       // ⭐ 视频唯一ID
    val title: String,                  // ⭐ 标题
    val description: String,            // 描述
    val library: String,                // 库类型
    val category: String,               // ⭐ 分类（创意/旅行/记录/搞笑/...）
    val labels: List<Label>?,           // 标签资源（可能为null）
    val label: Label?,                  // 单个标签
    val cover: Cover,                   // ⭐ 封面图
    val playUrl: String,               // ⭐ 播放地址（302→真实mp4）
    val playInfo: List<PlayInfo>,       // ⭐ 各清晰度播放信息
    val thumbPlayUrl: String?,          // 缩略图播放地址
    val duration: Long,                 // ⭐ 时长（秒）
    val webUrl: WebUrl,                 // 网页链接
    val author: Author?,                // ⭐ 作者信息
    val consumption: Consumption,       // ⭐ 消费数据（收藏/分享/评论数）
    val tags: List<Tag>?,               // 标签列表
    val tagList: List<Tag>?,            // 标签列表（另一个字段名）
    val collectionTag: Tag?,            // 合集标签
    val waterMarks: Any?,               // 水印
    val campaign: Any?,                 // 活动
    val ad: Boolean,                    // 是否是广告
    val adTrack: Any?,                  // 广告追踪
    val type: String?,                  // "NORMALVIDEO" / "BANNER" / ...
    val titleList: List<TitleItem>?,    // 标题列表
    val idx: Int?,                      // 索引
    val shareAdTrack: Any?,             // 分享广告追踪
    val favoriteAdTrack: Any?,          // 收藏广告追踪
    val webAdTrack: Any?,               // 网页广告追踪
    val date: Long?,                    // 日期
    val promotion: Any?,                // 推广
    val status: String?,                // 状态
    val slogan: String?,                // 标语
    val text: String?,                  // 文本
    val videoPosterBean: Any?,          // 视频海报
    val subtitles: List<Any>?,          // 字幕
    val ifFullscreen: Boolean?,         // 是否全屏
    val src: Int?,                      // 来源
    val viewCount: Long?,               // 观看数（部分返回）
    val haveCover: Boolean?,            // 是否有封面
    val lastViewTime: Long?,            // 最后观看时间
    val releaseTime: Long?,             // ⭐ 发布时间（时间戳ms）
    val resourceType: String?           // 资源类型
)