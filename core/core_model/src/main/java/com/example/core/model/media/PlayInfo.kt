package com.example.core.model.media

/**   
 * 包名称： com.example.core.model
 * 类名称：PlayInfo
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:43
 *
 */
data class PlayInfo(
    val name: String,               // "标清" / "高清" / "超清"
    val type: String,               // "normal" / "high" / "super"
    val url: String,                // ⭐ 播放中间地址（302→真实mp4）
    val urlList: List<UrlItem>,     // ⭐ URL列表（含多种格式）
    val width: Int,                 // 视频宽度（854 / 1280 / 1920）
    val height: Int,                // 视频高度（480 / 720 / 1080）
    val provider: Provider?         // CDN提供商
)
