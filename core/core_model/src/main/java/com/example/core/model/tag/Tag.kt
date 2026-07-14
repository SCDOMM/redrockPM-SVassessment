package com.example.core.model.tag

/**   
 * 包名称： com.example.core.model.general
 * 类名称：Tag
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:50
 *
 */
data class Tag(
    val id: Long,                   // 标签ID
    val name: String,               // ⭐ 标签名（"创意" / "搞笑" / "动物"）
    val actionUrl: String?,         // 点击标签后的跳转地址
    val desc: String?,              // 描述
    val bgPicture: String?,         // 背景图
    val headerImage: String?,       // 头部图
    val tagRecType: String?,        // 推荐类型
    val communityIndex: Int?        // 社区索引
)