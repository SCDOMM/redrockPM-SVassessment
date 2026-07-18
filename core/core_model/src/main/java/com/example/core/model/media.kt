package com.example.core.model

/**   
 * 包名称： com.example.core.model.media
 * 类名称：media
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-16 17:04
 *
 */
data class Cover(
    val feed: String = "",                  // ⭐ 列表页封面（最常用）
    val detail: String = "",                // ⭐ 详情页封面
    val blurred: String = "",               // 模糊封面
    val homepage: String? = null,           // 首页封面（可选）
    val sharing: String? = null             // 分享封面（可选）
)
data class PlayInfo(
    val name: String = "",                  // "流畅" / "标清" / "高清"
    val type: String = "",                  // "low" / "normal" / "high"
    val url: String = "",                   // ⭐ 播放地址（302跳转）
    val urlList: List<UrlItem> = emptyList(), // ⭐ URL列表
    val width: Int = 0,                     // 宽度
    val height: Int = 0                     // 高度
)
//CDN提供商
data class Provider(
    val name: String = "",                  // "YouTube" / "优酷" / ...
    val alias: String? = null,
    val icon: String = ""                   // 图标URL
)
data class UrlItem(
    val name: String = "",                  // "aliyun" / "ucloud"
    val url: String = ""                    // ⭐ 实际播放地址
)
data class WebUrl(
    val raw: String = "",                   // 原始链接
    val forWeibo: String? = null            // 微博分享链接
)
