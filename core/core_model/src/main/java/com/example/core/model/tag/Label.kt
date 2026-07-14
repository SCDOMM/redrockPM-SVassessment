package com.example.core.model.tag

/**
 * 包名称： com.example.core.model.general
 * 类名称：Label
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:51
 *
 */
data class Label(
    val text: String?,               // 标签文本
    val card: String?,               // 卡片类型（可选）
    val detail: LabelDetail?,        // 详情
    val actionUrl: String?           // 跳转
)
data class LabelDetail(
    val text: String?,
    val icon: String?,
    val color: ColorData?,
    val bgColor: ColorData?
)