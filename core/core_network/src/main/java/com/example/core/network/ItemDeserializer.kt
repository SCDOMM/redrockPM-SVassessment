package com.example.core.network

import com.example.core.model.*
import com.google.gson.*
import java.lang.reflect.Type

/**
 * 自定义 Item 反序列化器
 * 根据 item.type 的值，将 data 字段反序列化为对应的具体数据类
 */
class ItemDeserializer : JsonDeserializer<Item> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Item {
        val obj = json.asJsonObject

        val itemType = obj.get("type")?.asString ?: ""

        // 根据 type 解析 data 到正确的数据类
        val data: Any? = if (obj.has("data") && !obj.get("data").isJsonNull) {
            val dataElement = obj.get("data")
            try {
                when (itemType) {
                    "video" -> context.deserialize<VideoData>(dataElement, VideoData::class.java)
                    "videoSmallCard" -> context.deserialize<VideoData>(dataElement, VideoData::class.java)
                    "followCard", "autoPlayFollowCard" -> context.deserialize<FollowCardData>(dataElement, FollowCardData::class.java)
                    "squareCardCollection" -> context.deserialize<SquareCardCollectionData>(dataElement, SquareCardCollectionData::class.java)
                    "textCard" -> context.deserialize<TextCardData>(dataElement, TextCardData::class.java)
                    "banner" -> context.deserialize<BannerData>(dataElement, BannerData::class.java)
                    "atmospheric" -> context.deserialize<AtmosphericData>(dataElement, AtmosphericData::class.java)
                    "informationCard" -> context.deserialize<InformationCardData>(dataElement, InformationCardData::class.java)
                    "navigation" -> context.deserialize<NavigationData>(dataElement, NavigationData::class.java)
                    "authorRecommendationCard" -> context.deserialize<AuthorRecommendationCardData>(dataElement, AuthorRecommendationCardData::class.java)
                    "tagInfoCollection" -> context.deserialize<TagInfoCollectionData>(dataElement, TagInfoCollectionData::class.java)
                    "reply" -> context.deserialize<ReplyData>(dataElement, ReplyData::class.java)
                    else -> context.deserialize(dataElement, Any::class.java)
                }
            } catch (e: Exception) {
                // 解析失败时返回 null 而不是崩溃
                null
            }
        } else {
            null
        }

        // 解析其他字段
        val tag = try {
            if (obj.has("tag") && !obj.get("tag").isJsonNull) {
                context.deserialize<Tag>(obj.get("tag"), Tag::class.java)
            } else null
        } catch (e: Exception) {
            null
        }

        val id = obj.get("id")?.asInt ?: 0
        val adIndex = obj.get("adIndex")?.asInt ?: -1
        val trackingData = if (obj.has("trackingData") && !obj.get("trackingData").isJsonNull) {
            obj.get("trackingData").toString()
        } else null

        return Item(
            type = itemType,
            data = data,
            tag = tag,
            id = id,
            adIndex = adIndex,
            trackingData = trackingData
        )
    }
}