# 开眼 (Eyepetizer) API 完整文档

> 整合自多个开源项目，所有API均已测试可用

## 测试结果汇总

| API | 端点 | 状态 | 来源 |
|-----|------|------|------|
| **首页相关** | | | |
| 栏目列表 | `v5/index/tab/list` | ✅ 可用 | wiki |
| 栏目详情 | `v5/index/tab/{tabId}` | ✅ 可用 | wiki |
| 发现更多 (v7) | `v7/index/tab/discovery` | ✅ 可用 | wanandroid |
| 每日推荐 | `v5/index/tab/allRec` | ✅ 可用 | wanandroid |
| 日报精选 | `v5/index/tab/feed` | ✅ 可用 | wanandroid |
| 全部分类 (v4) | `v4/categories/all` | ✅ 可用 | 图片 |
| **排行相关** | | | |
| 排行榜 | `v4/rankList/videos?strategy={type}` | ✅ 可用 | 图片 |
| **分类/标签相关** | | | |
| 分类列表 | `v5/category/list` | ✅ 可用 | wiki |
| 分类接口 (v6) | `v6/tag/index?id={id}` | ✅ 可用 | 图片 |
| 主题列表 (v7) | `v7/tag/tabList` | ✅ 可用 | wanandroid |
| 主题详情广场 (v6) | `v6/tag/dynamics?id={id}` | ✅ 可用 | 图片 |
| 详情推荐 (v1) | `v1/tag/videos?id={id}` | ✅ 可用 | 图片 |
| **专题相关** | | | |
| 热门专题 | `v3/specialTopics` | ✅ 可用 | 图片 |
| 专题详情 | `v3/lightTopics/internal/{id}` | ✅ 可用 | 图片 |
| **社区相关** | | | |
| 社区推荐 (v7) | `v7/community/tab/rec` | ✅ 可用 | wanandroid |
| 关注列表 | `v5/community/tab/list` | ✅ 可用 | wiki |
| 关注详情 (v6) | `v6/community/tab/follow` | ✅ 可用 | wanandroid |
| 动态详情 | `v5/community/tab/dynamicFeeds` | ✅ 可用 | wiki |
| **通知相关** | | | |
| 通知列表 | `v3/messages/tabList` | ✅ 可用 | wiki |
| 通知详情 | `v3/messages` | ✅ 可用 | wiki |
| 互动列表 (v7) | `v7/topic/list` | ✅ 可用 | wanandroid |
| **视频相关** | | | |
| 视频相关 | `v4/video/related?id={id}` | ✅ 可用 | wiki |
| 视频评论 (v2) | `v2/replies/video?videoId={id}` | ✅ 可用 | wanandroid |
| 播放URL | `v1/playUrl?vid={vid}` | ⚠️ 需参数 | wiki |
| **搜索相关** | | | |
| 热搜关键词 | `v3/queries/hot` | ✅ 可用 | wiki |
| 搜索 | `v3/search?query={keyword}` | ⚠️ 空结果 | wiki |
| **日历相关** | | | |
| 日历 (v7) | `v7/roamingCalendar/index?date={date}` | ✅ 可用 | 图片 |

---

## API 基础信息

- **Base URL**: `http://baobab.kaiyanapp.com/api/`
- **协议**: HTTP (需配置网络安全策略允许明文)
- **认证**: 无需登录，但部分接口需要 `udid` 参数

---

## 一、首页相关 API

### 1.0 全部分类 (v4)

```
GET /v4/categories/all
```

**说明:** 返回所有分类的卡片列表，包含排行榜、专题等入口

**响应示例:**
```json
{
  "itemList": [
    {
      "type": "squareCard",
      "data": {
        "dataType": "SquareCard",
        "id": -1,
        "title": "",
        "image": "http://img.kaiyanapp.com/...",
        "actionUrl": "eyepetizer://ranklist/"
      }
    }
  ]
}
```

### 1.1 栏目列表 (首页Tab)

```
GET /v5/index/tab/list
```

**响应示例:**
```json
{
  "tabInfo": {
    "tabList": [
      {"id": -1, "name": "发现", "apiUrl": ".../v5/index/tab/discovery"},
      {"id": -2, "name": "推荐", "apiUrl": ".../v5/index/tab/allRec?page=0"},
      {"id": -3, "name": "日报", "apiUrl": ".../v5/index/tab/feed"},
      {"id": -4, "name": "社区", "apiUrl": ".../v5/index/tab/ugcSelected"},
      {"id": 14, "name": "广告", "apiUrl": ".../v5/index/tab/category/14"},
      {"id": 36, "name": "生活", "apiUrl": ".../v5/index/tab/category/36"},
      {"id": 10, "name": "动画", "apiUrl": ".../v5/index/tab/category/10"},
      {"id": 28, "name": "搞笑", "apiUrl": ".../v5/index/tab/category/28"}
    ],
    "defaultIdx": 1
  }
}
```

---

### 1.2 栏目详情

```
GET /v5/index/tab/{tabId}?page={page}
```

**参数:**
- `tabId`: Tab英文名称(discovery/allRec/feed/ugcSelected) 或数字ID(其他分类)
- `page`: 页码 (默认0)

**示例:**
```
/v5/index/tab/discovery?page=0
/v5/index/tab/allRec?page=0
/v5/index/tab/category/36?page=0
```

---

### 1.3 发现更多 (v7)

```
GET /v7/index/tab/discovery
```

**说明:** 返回发现页面的横幅广告和推荐内容

**响应结构:** 包含 `horizontalScrollCard` 类型的横幅轮播

---

### 1.4 每日推荐

```
GET /v5/index/tab/allRec?page={page}
```

---

### 1.5 日报精选

```
GET /v5/index/tab/feed?page={page}
```

---

### 1.6 分类列表

```
GET /v5/category/list
```

**响应示例:**
```json
{
  "itemList": [
    {
      "type": "briefCard",
      "data": {
        "dataType": "BriefCard",
        "id": 36,
        "icon": "http://img.kaiyanapp.com/...",
        "title": "#生活",
        "description": "匠心、健康、生活感悟"
      }
    }
  ],
  "count": 18
}
```

---

## 二、排行榜 API

### 2.1 排行榜

```
GET /v4/rankList/videos?strategy={strategy}
```

**参数:**
- `strategy`: 排行策略
  - `historical` - 历史总榜
  - `weekly` - 周榜
  - `monthly` - 月榜

**响应:** 包含排行榜视频列表，结构同栏目详情

---

## 三、分类/标签 API

### 3.1 分类接口 (v6)

```
GET /v6/tag/index?id={tagId}
```

**参数:**
- `id`: 标签ID (如 16=广告)

**响应示例:**
```json
{
  "tabInfo": {
    "tabList": [
      {"id": -1, "name": "推荐", "apiUrl": ".../v1/tag/videos?id=16"},
      {"id": -2, "name": "广场", "apiUrl": ".../v6/tag/dynamics?id=16"}
    ]
  },
  "tagInfo": {
    "dataType": "TagInfo",
    "id": 16,
    "name": "广告",
    "description": "为广告人的精彩创意点赞",
    "headerImage": "http://img.kaiyanapp.com/...",
    "bgPicture": "http://img.kaiyanapp.com/...",
    "tagFollowCount": 71019,
    "tagVideoCount": 51714,
    "tagDynamicCount": 4611
  }
}
```

---

### 3.2 主题详情广场 (v6)

```
GET /v6/tag/dynamics?id={tagId}&page={page}
```

**参数:**
- `id`: 标签ID
- `page`: 页码 (默认0)

**说明:** 返回该标签下的动态/帖子列表

---

### 3.3 详情推荐 (v1)

```
GET /v1/tag/videos?id={tagId}&page={page}
```

**参数:**
- `id`: 标签ID
- `page`: 页码 (默认0)

**说明:** 返回该标签下的推荐视频列表

---

## 四、专题 API

### 4.1 热门专题

```
GET /v3/specialTopics
```

**说明:** 返回热门专题列表，包含横幅广告

**响应示例:**
```json
{
  "itemList": [
    {
      "type": "banner2",
      "data": {
        "dataType": "Banner",
        "id": 819,
        "image": "http://img.kaiyanapp.com/...",
        "actionUrl": "eyepetizer://webview/?title=..."
      }
    }
  ]
}
```

---

### 4.2 专题详情

```
GET /v3/lightTopics/internal/{topicId}
```

**参数:**
- `topicId`: 专题ID

**说明:** 返回专题详情和相关内容

---

## 五、社区相关 API

### 2.1 社区推荐 (v7)

```
GET /v7/community/tab/rec
```

**说明:** 返回社区推荐内容

---

### 2.2 关注列表

```
GET /v5/community/tab/list
```

**响应:**
```json
{
  "tabInfo": {
    "tabList": [
      {"id": 0, "name": "作品", "apiUrl": ".../v5/community/tab/follow"},
      {"id": 1, "name": "动态", "apiUrl": ".../v5/community/tab/dynamicFeeds"}
    ]
  }
}
```

---

### 2.3 关注详情 (v6)

```
GET /v6/community/tab/follow?page={page}
```

---

### 2.4 动态详情

```
GET /v5/community/tab/dynamicFeeds?page={page}
```

---

## 三、通知相关 API

### 3.1 通知列表

```
GET /v3/messages/tabList
```

**响应:**
```json
{
  "tabInfo": {
    "tabList": [
      {"id": 0, "name": "互动", "apiUrl": ".../v5/discovery/myMessage"},
      {"id": 1, "name": "官方", "apiUrl": ".../v3/messages"}
    ]
  }
}
```

---

### 3.2 通知详情

```
GET /v3/messages?start={start}&num={num}
```

---

### 3.3 主题列表 (v7)

```
GET /v7/tag/tabList
```

**说明:** 返回主题标签列表

---

### 3.4 互动列表 (v7)

```
GET /v7/topic/list?page={page}
```

**说明:** 返回互动话题列表

---

## 四、视频相关 API

### 4.1 视频相关推荐

```
GET /v4/video/related?id={videoId}
```

**参数:**
- `id`: 视频ID

**响应:** 包含相关视频列表，结构同栏目详情

---

### 4.2 视频评论 (v2)

```
GET /v2/replies/video?videoId={videoId}&start={start}&num={num}
```

**参数:**
- `videoId`: 视频ID
- `start`: 起始位置 (默认0)
- `num`: 数量 (默认10)

**响应示例:**
```json
{
  "itemList": [
    {
      "type": "reply",
      "data": {
        "dataType": "ReplyBeanForClient",
        "id": 1230296904789655552,
        "videoId": 186856,
        "message": "评论内容",
        "createTime": 1582160615000,
        "user": {
          "uid": 303533138,
          "nickname": "用户名",
          "avatar": "头像URL"
        },
        "likeCount": 5
      }
    }
  ],
  "count": 10,
  "total": 0
}
```

---

### 4.3 播放URL

```
GET /v1/playUrl?vid={videoId}&resourceType=video&editionType={type}&source={source}
```

**必填参数:**
- `vid`: 视频ID
- `resourceType`: 资源类型 (video)
- `editionType`: 清晰度 (default/normal/high)
- `source`: 来源 (aliyun/qcloud/ucloud)

**响应:** 302重定向到实际视频URL

---

### 4.4 热搜关键词

```
GET /v3/queries/hot
```

**响应示例:**
```json
["艺术","街头","摄影日常","创意广告","美食","旅行","健身","汽车","黑科技"]
```

---

### 4.5 搜索 (需udid)

```
GET /v3/search?query={keyword}&start={start}&num={num}
```

**参数:**
- `query`: 搜索关键词
- `start`: 起始位置 (默认0)
- `num`: 数量 (默认10)

**注意:** 当前测试返回空结果，可能需要 `udid` 参数

---

## 九、日历 API

### 9.1 日历 (v7)

```
GET /v7/roamingCalendar/index?date={date}
```

**参数:**
- `date`: 日期 (格式: yyyy-MM-dd，如 2020-07-20)

**说明:** 返回指定日期的历史上的今天内容

---

## 十、视频数据结构

### VideoBeanForClient

```json
{
  "id": 4296,
  "dataType": "VideoBeanForClient",
  "title": "视频标题",
  "description": "视频描述",
  "category": "分类",
  "library": "DAILY",
  "duration": 98,
  "releaseTime": 1453392000000,
  "playUrl": "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=4296&...",
  "cover": {
    "feed": "封面图URL",
    "detail": "详情页封面URL",
    "blurred": "模糊背景URL"
  },
  "author": {
    "id": 58,
    "name": "作者名",
    "icon": "作者头像URL",
    "description": "作者描述"
  },
  "consumption": {
    "collectionCount": 2831,
    "shareCount": 3190,
    "replyCount": 69
  },
  "tags": [
    {"id": 20, "name": "标签名"}
  ],
  "playInfo": [
    {
      "height": 720,
      "width": 1280,
      "name": "高清",
      "type": "high",
      "url": "播放URL"
    }
  ]
}
```

---

### ReplyBeanForClient (评论)

```json
{
  "id": 1230296904789655552,
  "videoId": 186856,
  "videoTitle": "视频标题",
  "message": "评论内容",
  "createTime": 1582160615000,
  "likeCount": 5,
  "user": {
    "uid": 303533138,
    "nickname": "用户名",
    "avatar": "头像URL",
    "userType": "NORMAL"
  }
}
```

---

## 十一、item.type 类型汇总

| type | 说明 |
|------|------|
| `video` | 视频详情 |
| `videoSmallCard` | 小型视频卡片 |
| `textCard` | 文本卡片 |
| `followCard` | 详情卡片 |
| `horizontalScrollCard` | 横向滚动列表 |
| `squareCardCollection` | 方形卡片集合 |
| `squareCard` | 方形卡片 |
| `rectangleCard` | 矩形卡片 |
| `videoCollectionWithBrief` | 视频集合带简介 |
| `briefCard` | 简介卡片 |
| `banner2` | 横幅广告 |
| `reply` | 评论 |
| `leftAlignTextHeader` | 左对齐文本标题 |

---

## 十二、注意事项

1. **网络安全**: API使用HTTP，需在 `network_security_config.xml` 允许明文流量
2. **udid参数**: 部分接口(如搜索)可能需要 `udid` 参数才能正常返回数据
3. **播放URL**: 返回302重定向，实际播放地址在重定向后的URL
4. **图片URL**: 封面图使用七牛云CDN，支持 `?imageMogr2/quality/60` 等参数调整质量
5. **版本差异**: v5/v6/v7 为不同版本API，功能略有差异，优先使用最新版本

---

## 十三、推荐使用的API (MVP)

| 功能 | API | 版本 |
|------|-----|------|
| 首页Tab | `v5/index/tab/list` | v5 |
| Tab内容 | `v5/index/tab/{tabId}?page={page}` | v5 |
| 发现页面 | `v7/index/tab/discovery` | v7 |
| 分类浏览 | `v5/category/list` | v5 |
| 社区推荐 | `v7/community/tab/rec` | v7 |
| 视频详情 | `v4/video/related?id={videoId}` | v4 |
| 视频评论 | `v2/replies/video?videoId={videoId}` | v2 |
| 视频播放 | `v1/playUrl?vid={vid}&editionType=default&source=aliyun` | v1 |
| 热搜词 | `v3/queries/hot` | v3 |
| 通知列表 | `v3/messages/tabList` | v3 |
| 主题列表 | `v7/tag/tabList` | v7 |

---

## 十四、数据来源

| 来源 | 链接 | 说明 |
|------|------|------|
| 开眼API Wiki | https://github.com/yjq-dev/Eyepetizer/wiki/开眼-API-接口分析 | 原始API文档 |
| wanandroid博客 | https://www.wanandroid.com/blog/show/2718 | 补充v6/v7版本API |
| Jetpack_Kotlin_Eyepetizer | https://github.com/fmtjava/Jetpack_Kotlin_Eyepetizer | 项目结构参考 |
| 图片资料 | 用户提供的API截图 | 补充v4/v6标签、排行、专题API |
