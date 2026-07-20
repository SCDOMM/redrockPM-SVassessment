package com.example.core.model

/**   
 * 包名称： com.example.core.model
 * 类名称：EyepetizerResponse
 * 类描述：所有接口返回的顶层结构
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:41
 *
 */
data class EyepetizerResponse(
    val itemList: List<Item> = emptyList(),   // 列表数据（核心字段）
    val count: Int = 0,                        // 当前返回数量
    val total: Int = 0,                        // 总数
    val nextPageUrl: String? = null,           // 下一页URL（分页用）
    val adExist: Boolean? = null               // 是否有广告
    // ⚠️ 实际JSON中不存在 date、refreshCount，已移除
)
