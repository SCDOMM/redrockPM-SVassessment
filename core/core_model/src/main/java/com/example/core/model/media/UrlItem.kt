package com.example.core.model.media

/**   
 * 包名称： com.example.core.model
 * 类名称：UrlItem
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:47
 *
 */
data class UrlItem(
    val name: String,               // "aliyun" / "ucloud" / ...
    val url: String                 // ⭐ 实际播放地址（真实mp4地址）
)