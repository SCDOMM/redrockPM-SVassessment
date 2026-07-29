# Eyepetater  豆眼
开发者：SCDOMM,CHrainsound
REDROCK移动部门暑假考核项目

开眼（Eyepetizer）Android 客户端克隆，基于开眼非官方 API 打包实现的视频发现应用。
---
## 一.简要介绍

这是REDROCK移动部门的暑假考核项目，要求是从网易云、github、开眼APP中任选其一进行仿写，本项目选择仿写开眼APP，为避嫌将项目名改为了"eyepetater"，即“豆眼“。

由于时间问题，本项目很多布局UI界面没有优化，虽然能跑，但滑动冲突等还是使得使用体验较为难受。可谓集百语言之短，聚古今天下之糟粕。

话虽如此，其仍然是一个功能较为完整的APP。已经实现的功能/模块有：

首页，日报，发现，分类，搜索，通知，个人中心，视频播放页，视频周榜等。注：由于API问题，暂时无法实现登录功能。

其中，首页，日报，搜索，通知，个人中心由SCDOMM实现；发现，分类，视频播放页，主题播单，话题广场以及所有模块的联立由CHrainsound实现。

本文档将介绍本人Chrainsound实现的所有功能和模块部分，您可以查看README-SCDOMM查看SCDOMM实现的所有功能和模块部分。

注意：项目中的大部分API皆由两位开发者采取非正常手段获取，获取与共享之行为或有侵犯 **开眼** 权益的嫌疑。若被告知需停止共享与使用，我们会及时隐藏或者删除整个项目。请您暸解相关情况，并遵守相关协议
ps:直接套用好搭档的介绍了

## 二、页面介绍

**1.发现页**

顶部toolbar和日报页相同，左侧搜索右侧通知

接下来是分类板块，3x6分类表格，每个分类有对应的图标和名字，可以在这里找到感兴趣的分类，点击可以进入“分类详情页”

随后是主题播单模块，右侧更多点击进入主题播单列表页，下面的横向展示推荐的主题播单，展示播单图片和标题，点击推荐的卡片进入对应的“主题播单详情页”

最下面是话题广场，右侧更多点击进入话题列表页
下方是一个循环堆叠卡片，拖拽左右滑动切换最上边卡片，点击进入“话题详情页”
上方是卡片指示器显示目前是第几张卡片

**2.分类详情页**

分为两部分，上部展示该分类相关信息、分类名、头图、描述以及关注和浏览人数

下半部分可以点击或滑动切换热门推荐和最新发布，下面展示对应分类的图片视频部分，你可以看到视频预览图和标题、作者名和头像、以及分享数、点赞数、评论数、和视频时长等信息。

点击对应视频跳转“视频详情页”

**3.主题播单列表页**

顶部有返回键和页面名

下方内容展示每个播单的名字和部分描述部分视频预览，点击视频直接跳转“视频详情页”，点击卡片进入“主题播单详情页”

**4.主题播单详情页**

分为上下两部分，上部展示播单图片、标题、描述等信息

下部展示播单内的视频，每个视频包括作者头像和名字、视频预览图、视频标题与部分描述、视频时长

点击卡片跳转“视频详情页”可以查看完整描述以及，播放视频

**5.话题广场列表页**

顶部有返回键和页面名

tab栏可点击或滑动页面切换不同tab，展示不同话题

每个话题展示话题图、话题名、话题描述、参与人数

点击对应卡片进入“话题详情页”

**6.话题详情页**

页面类似分类详情页

上部展示话题图、标题、描述、参与人数。

下部tab可以切换热门推荐和最新发布

展示不同用户发布的相关话题的视频图片，同时显示点赞数、收藏数、评论数、作者头像和名字、发帖日期、对应图片或者视频

视频可以跳转到“视频详情页”

**7.视频详情页**

从上到下依次是

视频播放器，可控制播放暂停，播放器右下角点击全屏，全屏状态左右上下滑动分别控制亮度和音量

标题，作者头像和名字，点击可跳转“用户页”

分享可以将视频链接分享给好友，以及喜欢数和评论数

相关推荐展示有关联的视频信息，包括预览图，名字，作者，点击对应卡片跳转。





## 技术栈
| 开发语言 | Kotlin |

| 架构模式 | MVVM + LiveData + Sealed State |

| 网络层 | Retrofit  + OkHttp  + Gson  |

| 视频播放 | GSY Video Player  |

| 图片加载 | Glide |


# 三.项目结构

```
redrockPM-SVassessment/
├── app/                                          # 应用主模块
│   └── src/main/
│       ├── AndroidManifest.xml                   # Activity 注册 + network_security_config
│       ├── java/.../eyepetater/
│       │   ├── MainActivity.kt                   # ViewPager2 + BottomNav 四 Tab 导航壳
│       │   └── ui/theme/
│       │       ├── Color.kt                      # Compose 颜色定义
│       └── res/
│           ├── layout/activity_main.xml          # ViewPager2 + BottomNavigationView
│           ├── menu/bottom_nav_menu.xml          # 底栏四 Tab 菜单
│           ├── drawable/                         # 图标
│           ├── values/colors.xml                 # 浅色模式颜色
│           ├── values-night/colors.xml           # 深色模式颜色
│           ├── values/themes.xml                 # 日间主题
│           ├── values-night/themes.xml           # 夜间主题
│           └── xml/network_security_config.xml   # 安全配置
│
├── core/                                         # 核心层（子模块容器）
│   ├── core_model/                               # API 数据模型 + 自定义反序列化
│   │
│   ├── core_network/                             # Retrofit 客户端 
│   │
│   ├── core_common/                              # 通用工具 + JSON 解析辅助
│   │
│   └── core_media/                               # 视频播放（GSY Video Player）
│       └── src/main/
│           ├── java/.../core/media/
│              ├── VideoPlayerActivity.kt        # 播放页入口 + 视频详情获取
│              ├── VideoPlayerFragment.kt        # GSY Player 渲染 + 相关推荐
│              └── RelatedVideoAdapter.kt        # 相关视频列表适配器
│
├── ept_dicover/                                  # 发现页
│   └── src/main/java/.../ept/dicover/
│       ├── discovery/
│       │   ├── DiscoveryFragment.kt              # 发现主页
│       │   ├── DiscoveryViewModel.kt             # 数据解析
│       │   ├── CardStackView.kt                  # 自定义 Tinder 风格卡片堆叠组件
│       │   ├── CategoryAdapter.kt                # 分类网格适配器
│       │   └── TopicAdapter.kt                   # 主题播单适配器
│       ├── adapter/VideoCardAdapter.kt           # 视频卡片适配器（播单详情用）
│       ├── lightTopic/
│       │   ├── LightTopicsActivity.kt            # 话题播单详情页
│       │   ├── LightTopicsViewModel.kt           # 播单数据加载
│       │   ├── LightTopicListActivity.kt         # 播单列表页
│       │   ├── LightTopicListViewModel.kt        # 播单列表数据加载
│       │   ├── LightTopicListAdapter.kt          # 播单列表适配器
│       │   └── PreviewVideoAdapter.kt            # 预览视频缩略图适配器
│       ├── topicdetail/
│       │   ├── TopicDetailActivity.kt            # 话题详情（折叠标题栏 + Tab）
│       │   ├── TopicDetailViewModel.kt           # 话题信息 + Tab 结构解析
│       │   ├── TopicDetailTabAdapter.kt          # ViewPager2 Tab 适配器
│       │   ├── TopicDetailFeedFragment.kt        # Tab 内容流
│       │   ├── TopicDetailFeedViewModel.kt       # 内容流数据加载
│       │   └── TopicDetailFeedAdapter.kt         # 多类型适配器（视频 + 图文）
│       └── topicsquare/
│           ├── TopicSquareListActivity.kt        # 话题广场列表
│           ├── TopicSquareViewModel.kt           # Tab 数据加载
│           ├── TopicSquareListFragment.kt        # 单 Tab 列表
│           ├── TopicSquareListViewModel.kt       # 列表数据加载
│           └── TopicSquareListAdapter.kt         # 广场列表适配器
│
├── ept_category/                                 # 分类详情
│   └── src/main/java/.../ept/category/
│       ├── CategoryDetailActivity.kt             # 分类详情页
│       ├── CategoryDetailViewModel.kt            # 分类信息 + Tab 结构解析
│       ├── CategoryFeedFragment.kt               # 单 Tab 内容流
│       ├── CategoryFeedViewModel.kt              # 内容加载 + 分页（video / image 两种类型）
│       └── CategoryDetailAdapter.kt              # 多类型适配器（header / video / image）
│
├── ept_search/                                   # 搜索中心
│
├── ept_hot/                                      # 排行榜
│   └── src/main/java/.../ept/hot/
│       ├── fragment/
│       │   ├── HotActivity.kt                    # 排行榜入口
│       │   ├── HotListFragment.kt                # 单 Tab 列表（下拉刷新 + 无限滚动）
│       │   └── HotViewModel.kt                   # 数据加载 + 分页
│       └── adapter/
│           ├── HotPagerAdapter.kt                # ViewPager2 Tab 适配器
│           └── HotVideoAdapter.kt                # 视频卡片适配器
│
├── ept_person/                                   # 作者主页
│
├── ept_notify/                                   # 通知中心


```
## 依赖关系

```
app → ept_home → ept_notify → {core_model, core_network, core_common}
app → ept_daily → {ept_search, ept_notify, core_media}
app → ept_dicover → ept_category → {core_model, core_network, core_media}
app → ept_search → ept_person → {core_model, core_network, core_common, core_media}
app → ept_search → ept_hot → {core_model, core_network, core_media}

core_network → core_model
core_media → {core_model, core_network}
core_common → {core_model, core_network}
```
# 四、功能展示

## 1.发现
<img width="504" height="896" alt="discovery" src="https://github.com/user-attachments/assets/e2ce21f4-b00e-4875-bae8-c5cca6cdc293" />

发现页包含三大区域：分类网格（3x6）、主题播单（横向滑动列表）、话题广场（卡片堆叠）。负责各子activity的跳转。
- 调用 `SpecficApi.getPageRaw(pageLabel="discover_v2")` 获取原始 JSON
- 解析 `card_list` 分为三类数据：`categories`（分类网格）、`topics`（主题播单）、`squareItems`（话题广场卡片）
- 分类网格使用 `GridLayoutManager` + `CategoryAdapter`
- 主题播单使用水平 `LinearLayoutManager` + `TopicAdapter`
- 话题广场使用自定义 `CardStackView`
- 支持下拉刷新

**CardStackView — 自定义卡片堆叠组件**
- 继承 `FrameLayout`，实现最多 3 张可见卡片的堆叠效果
- 支持左右滑动手势（`GestureDetector` 检测 fling）
- 滑动完成后循环回收卡片

## 2.分类和话题详情
<img width="504" height="896" alt="categoryDetail" src="https://github.com/user-attachments/assets/5f8b74bc-e7c4-46f0-8b62-a4e6feb87c25" />
<img width="576" height="1024" alt="TopicDetail" src="https://github.com/user-attachments/assets/0ebe16dc-67ff-4fb3-8084-3856a85e0355" />

两个详情页实现类似，布局也类似
- 展示内容流（视频 + 图文），使用 CoordinatorLayout 实现折叠标题栏效果。
- 使用多类型适配器，来分别加载（视频帖子/图文帖子）
- 下滑加载更多
- 下拉拉刷新页面
- 视频帖子支持跳转详情页

## 3.主题播单列表
<img width="504" height="896" alt="lightTopicsList" src="https://github.com/user-attachments/assets/5857d4c4-a802-4985-afb3-a0c99985e506" />

- 展示内容流（播单信息、预览的两个视频卡片）
- 下滑加载更多
- 点击卡片跳转播单详情页
- 点击视频进入播放详情页
- 下拉刷新

## 4.话题列表
<img width="720" height="1280" alt="topicsList   " src="https://github.com/user-attachments/assets/92ad2519-45ae-4e25-80f6-7ce9d7d7bf71" />

- 展示对应tag的内容流（话题头图、话题、话题描述等）
- 部分tag支持下滑加载
- 下拉刷新

## 5.主题播单详情页
<img width="648" height="1152" alt="lightTopicsDetail" src="https://github.com/user-attachments/assets/2c834bef-fd12-45df-8443-1c0ea209167f" />

- 展示内容流（播单头图、标题、描述等）
- 支持下拉刷新

## 6.视频播放页
<img width="684" height="1216" alt="VideoPlayer" src="https://github.com/user-attachments/assets/52762eac-145f-40d6-9d68-1f964c02988a" />

- 内容展示
- 调用系统分享功能，分享视频链接
- 相关推荐视频点击跳转对应视频

## 7.所有页面均做了深色模式适配

# 五、心得体会

- 熟悉了使用多模块+git合作开发的流程

# 六、待优化

- 没有使用本地缓存机制，导致有时切换页面会重新调用api加载页面
- 合并不完全，待提高复用率
- 发现模块拼错了dicovery
  
# 七、api
使用非正常渠道获取



