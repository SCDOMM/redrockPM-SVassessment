package com.example.core.model.utils

/**   
 * 包名称： com.example.core.model.utils
 * 类名称：ParamsUtils
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-20 18:00
 *
 */
fun Map<String, Any?>.safeInt(key: String, default: Int = 0): Int {
    return (this[key] as? Number)?.toInt() ?: default
}
fun Map<String, Any?>.safeString(key: String, default: String = ""): String {
    return when (val v = this[key]) {
        is String -> v
        else -> v?.toString() ?: default
    }
}