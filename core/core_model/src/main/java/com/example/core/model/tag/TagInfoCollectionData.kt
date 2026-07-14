package com.example.core.model.tag

import com.example.core.model.navigation.TextFooterData
import com.example.core.model.navigation.TextHeaderData

/**   
 * 包名称： com.example.core.model
 * 类名称：TagInfoCollectionData
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:58
 *
 */
data class TagInfoCollectionData(
    val dataType: String,
    val id: Long,
    val header: TextHeaderData?,
    val footer: TextFooterData?,
    val tagList: List<Tag>?,
    val count: Int
)