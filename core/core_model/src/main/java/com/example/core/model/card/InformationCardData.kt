package com.example.core.model.card

import com.example.core.model.interact.Consumption
import com.example.core.model.media.WebUrl

/**   
 * 包名称： com.example.core.model.banner
 * 类名称：InformationCardData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:56
 *
 */
data class InformationCardData(
    val dataType: String,           // "InformationCard"
    val id: Long,
    val title: String?,
    val description: String?,
    val image: String?,             // 图片
    val actionUrl: String?,
    val webUrl: WebUrl?,
    val consumption: Consumption?
)