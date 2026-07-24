package com.example.core.network

import com.example.core.model.FollowCardData
import com.example.core.model.Item
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Item 类型的 JSON 反序列化器
 * 处理 autoPlayFollowCard 等类型的反序列化
 */
class ItemDeserializer : JsonDeserializer<Item> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Item {
        val jsonObject = json?.asJsonObject ?: return Item()
        val type = jsonObject.get("type")?.asString ?: ""
        val dataElement = jsonObject.get("data")

        val data = if (dataElement != null && type == "autoPlayFollowCard") {
            try {
                context?.deserialize(dataElement, FollowCardData::class.java)
            } catch (e: Exception) {
                dataElement
            }
        } else {
            dataElement
        }

        return Item(type = type, data = data)
    }
}
