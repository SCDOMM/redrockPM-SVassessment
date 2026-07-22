package com.example.core.model

import com.google.gson.annotations.SerializedName

/**   
 * 包名称： com.example.core.model
 * 类名称：WeeklyRankResponse
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-19 20:54
 *
 */
data class WeeklyRankResponse(
    val code: Int = 0,
    val message: Any? = null,
    val result: WeeklyRankResult? = null
)

data class WeeklyRankResult(
    @SerializedName("card_list") val cardList: List<Card> = emptyList()
)