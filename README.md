# Eyepetater

> 基于 Android 多模块架构实现的视频浏览客户端，逆向还原开眼 (Eyepetizer) 核心体验

一款 Kotlin 多模块 Android 项目，通过逆向分析开眼 App 移动端 API，完整复现了首页视频流、每日精选、发现与分类、视频播放、创作者主页、搜索、排行榜和通知等核心功能。采用 Fragment + MVVM + LiveData 架构，搭配 Compose + Material3 构建应用壳，模块间通过 TheRouter 解耦路由。

---

## 项目概述

| 条目 | 内容 |
|------|------|
| 项目名称 | Eyepetater（开眼看世界） |
| 开发语言 | Kotlin 2.2.10 |
| 最低 SDK | Android 8.0 (API 26) |
| 目标 SDK | Android 16 (API 36) |
| 构建系统 | Gradle 9.4.1 + AGP 9.2.1 + KSP 2.3.10 |
| 架构模式 | MVVM + LiveData + Sealed State |
| UI 方案 | Fragment 主导 + Compose/Material3 壳 |
| 网络层 | Retrofit 2.11 + OkHttp 4.12 + Gson 2.11 |
| 视频播放 | GSY Video Player 13.1.0 |
| 图片加载 | Glide 4.16 |
| 模块路由 | TheRouter 1.3.1 (KSP 注解处理) |
| 依赖管理 | Version Catalog (libs.versions.toml) |
| 包名 | com.example.eyepetater |

---

## 模块架构

### 模块划分

项目由 14 个 Gradle 模块组成，按功能边界拆分为核心层与业务层：

```
+------------------------------------------------------+
|                        :app                          |
|    Compose + Material3 壳  |  ViewPager2 + BottomNav |
|              4 Tab: 首页 -> 每日 -> 发现 -> 我的       |
+----------+----------+----------+---------------------+
| :ept_home|:ept_daily|:ept_dico |:ept_person          |
| 首页      | 每日精选  | 发现页    | 个人/创作者中心      |
+----------+----------+----------+---------------------+
| :ept_sear| :ept_hot |:ept_cate | :ept_notify         |
| 搜索      | 排行榜    | 分类详情  | 通知中心             |
+----------+----------+----------+---------------------+
|  :core:core_media    视频播放 (GSY Video Player)      |
|  :core:core_network  Retrofit + OkHttp + 3 组 API     |
|  :core:core_model    数据模型 + 自定义 Gson 反序列化   |
|  :core:core_common   通用工具 + 路由 + 偏好存储         |
|  :core               基础库（空壳，子模块容器）          |
+------------------------------------------------------+
```

### 编译依赖图

```
app -> ept_home -> ept_notify -> {core_model, core_network, core_common}
app -> ept_daily -> {ept_search, ept_notify, core_media, core_common}
app -> ept_dicover -> ept_category -> {core_model, core_network, core_media}
app -> ept_search -> ept_person -> {core_model, core_network, core_common, core_media}
app -> ept_search -> ept_hot -> {core_model, core_network, core_media}
app -> ept_notify -> {core_model, core_network, core_common}
core_network -> core_model
core_media -> {core_model, core_network}
core_common -> {core_model, core_network}
```

### 各模块职责

| 模块 | 类型 | 职责描述 |
|------|------|----------|
| :app | application | Compose + Material3 壳，ViewPager2 + BottomNavigationView 四 Tab 切换（禁用水平滑动），TheRouter 路由注册 |
| :ept_home | library | 首页视频流：下拉刷新 + 上滑预加载（阈值 6 项），CardStackView 卡片布局 |
| :ept_daily | library | 每日精选视频推荐，跨模块集成搜索与通知 |
| :ept_dicover | library | 发现页：分类网格 + Banner 轮播 (ViewPager2) + 轻话题预览 + 话题广场 + 话题详情（Tab 分页），21 个源文件为最大业务模块 |
| :ept_category | library | 分类详情：CoordinatorLayout 折叠标题栏 + 分页内容加载 |
| :ept_search | library | 搜索中心：5 类结果 Fragment（视频/用户/话题/创作者/图文）、预搜索建议、热门搜索、每周排行 |
| :ept_person | library | 创作者主页：3 Tab（简介/作品/专辑），TheRouter @Route 路由注入 |
| :ept_hot | library | 排行榜：多 Tab（周榜/月榜等），ViewPager2 + TabLayout |
| :ept_notify | library | 通知中心：2 Tab（系统消息/互动通知），TheRouter @Route 路由注入 |
| :core:core_model | library | API 响应数据类体系：ApiResponse<T> / PaginatedResult<T> / Item / VideoData / MetroData / Card 等 + 自定义 ItemDeserializer（按 type 字段分发）+ ParamsUtils（Map 安全取值扩展） |
| :core:core_network | library | Retrofit 单例 + 3 组 API 接口（UniversalApi 内容 / SearchApi 搜索 / SpecficApi 遗留）+ OkHttp 拦截器注入认证头 + Call<T>.await() 协程扩展 |
| :core:core_common | library | 通用工具：FragmentInterface 导航合约、JSON 解析辅助（parseUniversalUtils / parseSearchUtils / parseUserUtil）、SharedPreferences 封装 |
| :core:core_media | library | 视频播放：VideoPlayerActivity + VideoPlayerFragment（GSY Player 集成）+ RelatedVideoAdapter 相关推荐 |
| :core | library | 空壳基础库，仅作为 :core:* 子模块的结构根 |

---

## 架构设计要点

### MVVM + Sealed State 统一状态管理

所有业务模块统一采用 AndroidViewModel + LiveData + Sealed Class 状态管理：

```kotlin
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _liveData = MutableLiveData<HomeState>()
    val liveData: LiveData<HomeState> get() = _liveData

    fun refreshLiveData() {
        viewModelScope.launch {
            runCatching {
                val response = appService.getPage("recommend", "card").await()
                parseVideosFromCardList(response)
            }.onSuccess { list ->
                _liveData.postValue(HomeState.RefreshState(list))
            }.onFailure { e ->
                _liveData.postValue(HomeState.ErrorState(e.message ?: "未知错误"))
            }
        }
    }
}

sealed class HomeState {
    data class RefreshState(val videoList: List<MetroData>) : HomeState()
    data class LoadingMoreState(val videoList: List<MetroData>) : HomeState()
    data class ErrorState(val errorMsg: String) : HomeState()
}
```

设计意图：将 UI 状态显式建模为封闭类型，消除了业务代码中不可控的布尔组合（isLoading / isError / isEmpty 混用）。Fragment 通过 observe(viewLifecycleOwner) 监听状态变化，单一数据源驱动 UI 更新，状态与渲染解耦。

### 网络层设计

采用三层分离的 API 接口设计，按业务领域正交划分：

- **UniversalApi** — 核心内容接口：首页卡片流 (getPage)、导航 (getNav)、视频详情 (getItemDetail)、相关推荐 (getRelatedRecommend)、分页加载 (getMorePage / callMetroListV2)、排行榜 (getRankListTabs / getRankListByUrl)、用户信息 (getUserInfo)、推送列表 (getPushList)、专辑加载 (loadMoreAlbum)、主题卡片列表 (callCardList)
- **SearchApi** — 搜索接口：全文搜索 (search / searchLoad)、预搜索 (getPreSearch)、热门查询 (getHotQueries)、每周排行 (getWeeklyRank)
- **SpecficApi** — 遗留接口：baobab.kaiyanapp.com 域下的主题详情、原始页面拉取

网络调用方式：Retrofit 返回 Call<T> 而非 Kotlin suspend，提供三种调用通道适配不同场景：

| 方式 | 适用场景 | 实现 |
|------|---------|------|
| .execute() | 后台线程（Dispatchers.IO）同步调用 | 原生 Retrofit 同步方法 |
| .enqueue() | 传统回调风格 | 原生 Retrofit 异步回调 |
| .await() | 协程挂起风格 | 自定义 suspendCancellableCoroutine 扩展 |

### 视频播放流程

```
用户点击卡片 -> VideoPlayerActivity.start(context, videoId)
    -> Dispatchers.IO 调用 getItemDetail(videoId)
    -> Gson 手动解析 JSON（Map 穿透，处理非规范响应结构）
    -> 清洗播放地址（\u003d -> =, \u0026 -> &）
    -> 创建 VideoPlayerFragment 注入视频元数据
    -> GSY Player 渲染 + Glide 加载封面
    -> resolvePlayUrl() 302 跟随获取真实 CDN 地址
    -> 并行加载相关推荐列表
```

特别说明：Eyepetizer API 视频详情接口的 JSON 格式与传统 REST 不同，部分字段动态嵌套。设计上采用 Gson 的 Map<String, Any> 手动穿透解析，逐层安全提取，而非依赖固定数据类映射，以应对 API 结构的偶发变动。

### 模块间导航

采用 TheRouter 1.3.1 注解路由框架（KSP 编译期代码生成），各模块声明式注册路由：

```kotlin
// 注册路由
@Route("http://therouter.com/notify")
class NotifyActivity : AppCompatActivity()

// 导航调用
TheRouter.build("http://therouter.com/notify").navigation(context)
```

设计意图：多模块架构下，若模块 A 需要跳转模块 B 的 Activity，直接 import 将引入编译期依赖。TheRouter 通过字符串 URL 解耦，各模块无需直接依赖对方——只需依赖核心层即可完成跨模块导航。

### 数据模型层级

core_model 定义了完整的 API 类型系统，适应 Eyepetizer 多层嵌套的响应结构：

- ApiResponse<T> — 通用外壳：code + message + result
- PaginatedResult<T> — 分页包裹：lastItemId + itemList + count
- PageResult — 页面布局：cardList + pageLabel + pageType
- MetroData / MetroItem — 地铁流视频项：video / author / cover / consumption
- Item — 多态容器：type（鉴别器）+ data，配合自定义 ItemDeserializer 按 type 分发到对应 Kotlin 类型
- VideoData / FollowCardData / Card / NoticeItem / UserInfo — 领域模型
- ParamsUtils — safeString / safeInt / safeBool Map 安全取值扩展

---

## 实现功能

### 首页视频流 (:ept_home)
- 上拉无限加载（RecyclerView 滚动监听 + 预加载阈值 6）
- 下拉刷新（SwipeRefreshLayout）
- 视频卡片点击跳转播放页
- 通知角标入口

### 每日精选 (:ept_daily)
- 每日推荐视频列表
- 跨模块集成：搜索入口、通知入口

### 发现页 (:ept_dicover)
- 分类网格（带图标 + 标题）
- Banner 轮播（ViewPager2，自动轮播）
- 轻话题预览（支持横向滑动 + 视频预览播放）
- 话题广场（网格列表）
- 话题详情（Tab 分页：推荐/最新/热门）

### 分类详情 (:ept_category)
- CoordinatorLayout 折叠标题栏（CollapsingToolbarLayout）
- 视频列表分页加载

### 搜索 (:ept_search)
- 全文搜索：5 类结果 Tab（视频/用户/话题/创作者/图文）
- 输入预搜索（实时建议）
- 热门搜索展示
- 每周排行榜

### 视频播放 (:core:core_media)
- GSY Video Player 全屏播放
- 手势控制（亮度/音量/进度）
- 封面图 Glide 加载
- 相关推荐视频列表
- 横竖屏自动适配
- 生命周期感知（后台暂停、销毁释放）

### 创作者主页 (:ept_person)
- 3 Tab 内容页：简介 / 作品 / 专辑
- 创作者信息展示
- 视频列表与专辑切换

### 排行榜 (:ept_hot)
- 多 Tab 分页（周榜、月榜等）

### 通知中心 (:ept_notify)
- 系统消息 / 互动通知双 Tab

---

## API（逆向分析）

### 端点基础

```
Base URL: https://api.eyepetizer.net/
```

所有认证常量硬编码于 NetworkConfig.kt，通过 OkHttp 拦截器全局注入。

### 认证请求头

| 请求头 | 类型 | 说明 |
|--------|------|------|
| x-api-key | 静态 | 0530ee4341324ce2b26c23fcece80ea2 |
| X-THEFAIR-APPID | 静态 | 固定应用实例 ID |
| X-THEFAIR-AUTH | 动态 (Base64) | 会话令牌，有过期时间 |
| X-THEFAIR-CID | 静态 | 32 位十六进制客户端 ID |
| X-THEFAIR-UA | 静态 | 设备完整指纹 UA |
| User-Agent | 静态 | 同 X-THEFAIR-UA |
| Cookie | 静态 | ky_udid / ky_auth / APPID / PHPSESSID |

### 视频播放地址解析

视频流请求不经过上述鉴权头，而是通过 RetrofitClient.resolvePlayUrl() 的 302 重定向跟随机制获取真实 CDN 地址。明文 HTTP 通过 network_security_config.xml 对 *.kaiyanapp.com 放行。

详细 API 文档见 [新api.md](新api.md)（中文）。

---

## 构建与运行

```bash
# 增量构建
./gradlew assembleDebug

# 完整清理构建（模块变更后出现 "Unresolved reference" 时使用）
./gradlew clean assembleDebug

# 构建 + 安装到连接的设备
./gradlew :app:installDebug
```

配置缓存已启用 (org.gradle.configuration-cache=true)。模块依赖变更后如增量构建失败，需先执行 clean。无需额外配置，所有 API 认证信息已内置，同步后即可运行。

---

## 开发规范与注意事项

### 代码规范

- Kotlin 代码风格：official（gradle.properties 中设置）
- 包命名：Java 风格点分 (com.example.core.network)；Gradle 模块名用下划线 (:core:core_network)
- View 绑定：findViewById 优先于 ViewBinding（项目既有约定）
- 布局颜色：使用 @color/ 资源引用 + values-night/ 暗色主题；仅 ?attr/selectableItemBackgroundBorderless 用于水波纹
- 类注释头：
  ```kotlin
  /**
   * description ： 功能描述
   * email : 3014386984@qq.com
   * date : 2026/7/14 17:57
   */
  ```

### 已知陷阱

- 模块名拼写：ept_dicover 缺少字母 s（正确应为 ept_discover），重命名需同步 settings.gradle.kts、导入引用和构建依赖
- AGP 9.x compileSdk：必须使用 compileSdk { version = release(36) { minorApiLevel = 1 } } 形式，旧版 compileSdk = 36 不兼容
- Kotlin 插件冲突：android.library 模块无需显式添加 org.jetbrains.kotlin.android 插件，否则报 "Cannot add extension with name 'kotlin'"
- TheRouter KSP 配置：每个使用 TheRouter 的模块必须在 build.gradle.kts 中添加 ksp { arg("therouter.moduleName", ":模块名") }
- Glide 注解处理器：仅 :ept_dicover、:ept_category、:ept_hot 启用 annotationProcessor(libs.glide.compiler)
- lifecycle-runtime-compose-android：根 build.gradle.kts 中通过 resolutionStrategy 锁定 2.8.7 版本

---

## 项目结构

```
redrockPM-SVassessment/
├── app/                          # 应用主模块 (Compose 壳 + AndroidManifest)
├── core/
│   ├── core_model/               # API 数据模型 + 自定义反序列化器
│   ├── core_network/             # Retrofit/OkHttp 网络层
│   │   └── api/                  # UniversalApi, SearchApi, SpecficApi
│   ├── core_common/              # 通用工具 + 解析辅助 + 偏好存储
│   └── core_media/               # GSY Video Player 视频播放
├── ept_home/                     # 首页视频流
├── ept_daily/                    # 每日精选
├── ept_dicover/                  # 发现页 (分类/Banner/话题)
│   ├── discovery/                # 发现主页面
│   ├── lightTopic/               # 轻话题 + 视频预览
│   ├── topicdetail/              # 话题详情 (Tab 分页)
│   └── topicsquare/              # 话题广场
├── ept_category/                 # 分类详情 (CoordinatorLayout)
├── ept_search/                   # 搜索 (5 类结果 + 预搜索)
├── ept_hot/                      # 排行榜
├── ept_person/                   # 创作者主页 (3 Tab)
├── ept_notify/                   # 通知中心 (2 Tab)
├── gradle/
│   └── libs.versions.toml        # 版本目录
├── 新api.md                      # API 逆向文档 (中文)
├── build.gradle.kts              # 根构建脚本
├── settings.gradle.kts           # 项目配置 & 模块注册
├── gradle.properties             # Gradle 全局配置
└── AGENTS.md                     # 开发者代理指南
```

---

## 运行方式

1. 使用 Android Studio 打开项目根目录
2. 等待 Gradle Sync 完成（AGP 9.2.1 / Gradle 9.4.1）
3. 选择 API 26+ 的模拟器或真机
4. 运行 :app 配置

无需配置 API Key 或第三方服务，全部认证信息已内置。

---

## 声明

本项目仅用于学习与技术研究目的，与开眼 (Beijing Kaiyan Technology Co., Ltd.) 无任何关联或背书关系。
