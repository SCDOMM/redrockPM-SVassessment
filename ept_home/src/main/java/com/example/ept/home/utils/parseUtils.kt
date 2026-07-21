package com.example.ept.home.utils

import com.example.core.model.official.HomePageResponse
import com.example.core.model.official.MetroData

/**   
 * 包名称： com.example.ept.home.utils
 * 类名称：parseUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 16:03
 *
 */
fun parseHomeVideos(response: HomePageResponse): List<MetroData> {
    val list = mutableListOf<MetroData>()
    response.result?.card_list?.forEach { card ->
        if (card.type == "set_metro_list") {
            card.card_data?.body?.metro_list?.forEach { metro ->
                if (metro.type == "video") {
                    metro.metroData?.let { list.add(it) }
                }
            }
        }
    }
    return list
}