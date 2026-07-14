package com.example.core.model

import com.example.core.model.tag.Tag

/**   
 * 包名称： com.example.core.model
 * 类名称：Item
 * 类描述：包装器
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:46
 *
 */
data class Item (
    val type: String,           //种类
    val data: Any?,              // 根据 type 反序列化到具体类型
    val tag: Tag?,              //Item关联的标签
    val id: Int?,               //Item本身的ID
    val adIndex: Int?,          //广告位
    val trackingData: Any?      //没啥用
)