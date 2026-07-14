package com.example.core.model.media

/**   
 * 包名称： com.example.core.model
 * 类名称：Provider
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:48
 *
 */
data class Provider(
    val name: String,               // "aliyun" / "ucloud" / "qiniu"
    val icon: String                // 图标URL（可选）
)