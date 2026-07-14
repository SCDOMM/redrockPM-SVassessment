package com.example.core.model.media

/**
 * 包名称： com.example.core.model
 * 类名称：Cover
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:44
 *
 */
data class Cover(
    val feed: String,               // ⭐ 列表页封面（常用）
    val detail: String,             // ⭐ 详情页封面
    val blurred: String,            // 模糊封面
    val homepage: String?,           // 首页封面（可选）
    val sharing: String?            // 分享封面（可选）
)