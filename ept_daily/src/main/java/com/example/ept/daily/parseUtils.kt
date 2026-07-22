package com.example.ept.daily

import com.example.core.model.PageResponse
import com.example.core.model.MetroData

/**   
 * 包名称： com.example.ept.daily
 * 类名称：parseUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 17:20
 *
 */
fun parseDailyVideos(response: PageResponse): List<MetroData> {
    val list = mutableListOf<MetroData>()
    response.result?.cardList?.forEach { card ->
        if (card.type == "set_metro_list") {
            card.cardData?.body?.metroList?.forEach { metro ->
                if (metro.type == "video") {
                    metro.metroData?.let { list.add(it) }
                }
            }
        }
    }
    return list
}