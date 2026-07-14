package com.example.core.model.author

import com.example.core.model.media.Cover

/**
 * 包名称： com.example.core.model
 * 类名称：Author
 * 类描述：TODO
 * 创建人：韦西波
 * 创建时间：2026-07-14 17:48
 *
 */
data class Author(
    val id: Long,                   // 作者ID
    val icon: String,               // ⭐ 头像地址
    val name: String,               // ⭐ 昵称
    val description: String?,       // 描述
    val link: String?,              // 链接
    val latestReleaseTime: Long?,   // 最新发布时间
    val videoNum: Int?,             // 视频数量
    val follow: Follow?,            // 关注信息
    val shield: Shield?,            // 屏蔽信息
    val approved: Boolean?,         // 是否认证
    val ifPgc: Boolean?,            // 是否PGC
    val library: String?,           // 库
    val trackUserId: Int?,          // 埋点用户ID
    val cover: Cover?,              // 作者封面
    val expert: Boolean?,           // 是否专家
    val unlockType: Int?            // 解锁类型
)