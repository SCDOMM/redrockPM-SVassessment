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
    val itemList: List<Item>,       //列表数据
    val count: Int,                 // 当前返回数量
    val total: Int,                 // 总数（可选）
    val nextPageUrl: String?,       // 下一页URL（分页）
    val date: Long?,                // 请求日期时间戳
    val refreshCount: Int?,         // 刷新数
    val adExist: Boolean?           // 是否有广告
)

