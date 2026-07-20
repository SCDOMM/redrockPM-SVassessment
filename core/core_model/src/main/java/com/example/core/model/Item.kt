package com.example.core.model

/**
 * 包名称： com.example.core.model
 * 类名称：Item
 * 类描述：包装器
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:46
 *
 */
data class Item(
    val type: String,                 // 种类（决定 data 的真实类型）
    val data: Any? = null,            // 根据 type 反序列化到具体类型
    val tag: Any? = null,             // Item关联的标签（可能是 Tag 或其他）
    val id: Int = 0,                  // Item本身的ID（顶级item通常为0）
    val adIndex: Int = -1,            // 广告位索引
    val trackingData: Any? = null     // 追踪数据（通常为null）
)

data class TitleItem(
    val title: String?,              // 标题文本
    val type: String?                // 类型（可选）
)