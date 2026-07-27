<img width="296" height="640" alt="日报" src="https://github.com/user-attachments/assets/7aff6371-e35b-49ad-9fc8-faea774d122c" /><img width="296" height="640" alt="首页和通知" src="https://github.com/user-attachments/assets/cd03ab23-d8c0-49c7-aa37-bbbec2175ae0" />REDROCK移动部门暑假考核项目

开发者：SCDOMM,CHrainsound

# 一.简要介绍

这是REDROCK移动部门的暑假考核项目，要求是从网易云、github、开眼APP中任选其一进行仿写，本项目选择仿写开眼APP，为避嫌将项目名改为了"eyepetater"，即“豆眼“。

由于时间问题，本项目很多布局UI界面没有优化，虽然能跑，但滑动冲突等还是使得使用体验较为难受。可谓集百语言之短，聚古今天下之糟粕。

话虽如此，其仍然是一个功能较为完整的APP。已经实现的功能/模块有：

首页，日报，发现，分类，搜索，通知，个人中心，视频播放页，视频周榜等。注：由于API问题，暂时无法实现登录功能。

其中，首页，日报，搜索，通知，个人中心由本人SCDOMM实现；发现，分类，视频播放页，主题播单，话题广场以及所有模块的联立由CHrainsound实现。

本文档将介绍本人SCDOMM实现的所有功能和模块部分，您可以查看README-CHrainsound查看CHrainsound实现的所有功能和模块部分。

注意：项目中的大部分API皆由两位开发者采取非正常手段获取，获取与共享之行为或有侵犯 **开眼** 权益的嫌疑。若被告知需停止共享与使用，我们会及时隐藏或者删除整个项目。请您暸解相关情况，并遵守相关协议

**1.技术栈**

- 安卓8.0(API26)和它的动物朋友们
- Androidx和它的动物朋友们
- kotlin和它的动物朋友们
- gradle9.4版本，AGP9.2.1版本
- gson 用于网络请求
- glide 用于展示图片
- OKhttp 用于网络请求
- retrofit 用于网络请求
- gsvy 用于播放视频
- MVVM架构及其必备的依赖库(ViewModel等)
- 协程，Flow和它的动物朋友们

**2.页面和功能**

本项目包含有一个可用的短视频app应有的一切功能(除了登录)，可以正常播放视频，进行搜索，滑动刷新，查看话题，查看标签，打开个人主页等。

由于登录API的问题和鉴权的问题，本项目不包含一切和登录相关的功能，如点赞，评论，收藏，转发，关注，上传视频等等。

在"首页"中，您可以查看开眼app为您推荐的视频，不论你怎样刷新或者加载，第一个视频将永远是可以当面播放的，不需要点进视频播放页。

"首页"的右上角是“通知”页面，由于“通知”中的“互动”功能需要登录实现，因此其暂时没有内容。除此之外，“通知”中的“消息”功能正常实现了，您可以随时查看开眼官方为您推送的链接和视频。

在“日报”中，您可以查看开眼app为您推荐的固定顺序的视频，不论您怎样刷新和加载，加载的视频都将是固定的，这些视频每天会刷新一次。同时，这些视频需要进入播放页才能播放。

“日报”的右上角是“通知”页面，这里不再过多介绍。

“日报”的左上角是“搜索”页面。

在“搜索”页面中，您可以随时查询包括视频，作者，图文，话题，用户的所有信息，同时您还能看到“搜索历史”和“推荐搜索”，以及“视频周榜”。点击视频周榜将进入“周排行”“月排行”和“总排行”三个页面。

在“搜索”得到的结果页面中包含有视频，作者，图文，话题和用户。其中点击“作者”和”用户“将进入个人中心页面。

在”个人中心“中，你将能看到对应用户的用户名，头像，空间封面，粉丝关注徽章，以及其它互动情况。

如果该用户拥有视频/图文发布，那么个人中心将可能展示”个人主页“，”作品“和”专辑“三个页面。

如果该用户是作者(pgc)而非普通用户(ugc)，并且有投稿，那么其将必然展示”个人主页“和”作品“；如果作者发布有专辑，那么其将必然展示”专辑“

如果该用户是普通用户(ugc)，并且有投稿，其将必然展示”作品“页面。普通用户不可能展示”个人主页“和”专辑“页面

”个人主页“包含有”最近更新“(1-5个视频)，”最受欢迎“(1-5个视频)，以及”专辑“(展示0-2个专辑)。如果该用户没有专辑，那么个人主页将不会展示任何专辑。”个人主页“不会展示任何图文。

”作品“页面将展示视频和图文两种类型的投稿。

”专辑“页面将展示专辑，每个专辑都包含有1-n个视频，专辑会同时展现其封面和专辑中包含的1-3个视频

”个人中心“页面原本计划可以通过点击”粉丝“和”关注“来查看相应的粉丝和被关注账户的信息，由于登录问题，该功能无限期作废。

# 二.项目结构

项目采取MVVM架构和gradle多模块开发，大致分为如下：

├─ app 应用入口模块

│ └─ MainActivity 主Activity，通过VP2绑定"首页""日报""发现""个人"四个Fragment

├─ core 核心模块

│ ├─ core_common 通用模块

│ │ ├─ parseXXXUtils不同模块的通用数据解析类

│ │ └─ SharedPreferenceUtils SP快速访问工具类

│ ├─ core_model 数据模型模块

│ │ └─ 各种数据类

│ ├─ core_network 网络模块

│ │ ├─ 开眼API相关接口

│ │ └─ RetrofitClient Retrofit网络客户端

│ └─ core_media 媒体模块

│ └─ 视频详细播放页相关程序

├─ ept_daily 日报模块

│ └─ 日报页面相关程序

├─ ept_discover 发现模块

│ └─ 发现页面相关程序

├─ ept_home 主页模块

│ └─ 主页相关程序

├─ ept_notify 通知模块

│ └─ 通知页面相关程序

├─ ept_person 个人中心模块

│ └─ 个人中心相关程序

└─ ept_search 搜索模块

└─ 搜索页面相关程序


# 三.功能展示

由于是仿写APP，可以使用开眼的服务器，不需要自造后端。

### 首页和通知

<img width="296" height="640" alt="首页和通知" src="https://github.com/user-attachments/assets/78585d01-142b-4fc6-9688-6bf9910b8361" />

Q：如何做到同一个RV有不同的item，其中一个甚至能放视频？

A：给同一个RV绑定多个ViewHolder，重写getItemViewType()方法，根据位置的不同绑定不同的标识符。

~~~kotlin
const val VIEW_TYPE_HEADER = 0
const val VIEW_TYPE_SELECTED = 1
const val VIEW_TYPE_NORMAL = 2

override fun getItemViewType(position: Int): Int {
    if (position == 0) {
        return VIEW_TYPE_HEADER
    } else if (position in 1..<6) {
        return VIEW_TYPE_SELECTED
    }
    return VIEW_TYPE_NORMAL
}
override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
): RecyclerView.ViewHolder {
    return when (viewType) {
        VIEW_TYPE_HEADER -> {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview1_home, parent, false)
            HeaderViewHolder(view)
        }
        VIEW_TYPE_SELECTED -> {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview2_home, parent, false)                SelectedViewHolder(view)
        }
        else -> {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview3_home, parent, false)
            NormalViewHolder(view)
        }
    }
}
~~~

### 日报

<img width="296" height="640" alt="日报" src="https://github.com/user-attachments/assets/672292db-dd49-4289-a7d6-1fd494e945f1" />

### 搜索

<img width="296" height="640" alt="搜索" src="https://github.com/user-attachments/assets/cfabe649-6367-4aeb-a40f-0f4006b68c30" />

Q：如何做到同一个搜索框，能展示初始页面，推荐词页面和结果页面？

A：SearchView摆在最上面，下方全部放FrameLayout，用FragmentManager对FrameLayout的状态进行管理。

同时，给SearchView所在的Activity的ViewModel绑定多个LiveData，初始页面、推荐词页面、结果页面等通过在ViewModelProvider()的参数中填写requireActivity()来共享ViewModel，随后即可动态获取搜索框的状态。

### 个人中心

<img width="296" height="640" alt="个人中心" src="https://github.com/user-attachments/assets/8dd1e8b9-aca3-492f-a788-69c669f075b5" />

Q：个人中心是怎么做到“最近更新”“最受欢迎”“专辑”等放在同一个RV的？

A：同首页部分一致，但是略有差异：

使用一个封装类将四种数据类(最近更新，最受欢迎，专辑和标题)封装一起，随后根据内容逐一判断类型

~~~kotlin
override fun getItemViewType(position: Int): Int {
    val item = getItem(position)
    return when (item) {
        is UserHomeItem.SectionTitle -> TYPE_SECTION_TITLE
        is UserHomeItem.VideoRecent -> TYPE_VIDEO_RECENT
        is UserHomeItem.VideoPopular -> TYPE_VIDEO_POPULAR
        is UserHomeItem.Album -> TYPE_ALBUM
    }
}
override fun onCreateViewHolder(
     parent: ViewGroup,
    viewType: Int
    ): RecyclerView.ViewHolder {
     val inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
        TYPE_SECTION_TITLE -> {
            val view = inflater.inflate(R.layout.item_item_title, parent, false)
            SectionTitleViewHolder(view)
        }
        TYPE_VIDEO_RECENT -> {
            val view =
                inflater.inflate(R.layout.item_new, parent, false)
            RecentVideoViewHolder(view)
        }
        TYPE_VIDEO_POPULAR -> {
            val view = inflater
                .inflate(R.layout.item_video, parent, false)
            PopularVideoViewHolder(view)
        }
        TYPE_ALBUM -> {
            val view =
                inflater.inflate(R.layout.item_album, parent, false)
            AlbumViewHolder(view)
        }
        else -> {
            error("Unknown viewType: $viewType")
        }
    }
}
override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int
) {
    val item = getItem(position)
    when (holder) {
        is SectionTitleViewHolder -> holder.bindData(item as UserHomeItem.SectionTitle)
        is RecentVideoViewHolder -> holder.bindData(item as UserHomeItem.VideoRecent)
        is PopularVideoViewHolder -> holder.bindData(item as UserHomeItem.VideoPopular)
        is AlbumViewHolder -> holder.bindData(item as UserHomeItem.Album)
    }
}
~~~

# 四.心得体会

1.千万别熬夜！！

2.协作劳动好啊，有人帮你分担其它模块，没必要所有东西都自己一个个敲，只要开发者都会用github，效率就会大大提高。

# 五.待优化

1. 个人主页，首页等都还没绑定点击事件，交给CHrainsound做了。
2. UI和很多细节没有优化，相信后人的智慧。
3. 因为网络问题可能出现一些恶性bug。

# 六.注意事项

由于API是从开眼app官方API抓包的，因为不知道Auth请求头的算法，所以每隔一段时间都需要手动获取一遍Auth请求头，否则所有网络请求都会失效(对于已经relese的apk文件就没办法了)。

请通过HttpCanary或者神秘蓝色小鲨鱼等软件进行抓包，并将抓包结果放于./core/core_network/src/main/java/com/example/core/network的networkConfig文件中：

~~~kotlin
object NetworkConfig {
    const val BASE_URL = "https://api.eyepetizer.net/"
    const val X_API_KEY = "0530ee4341324ce2b26c23fcece80ea2"
    const val X_THEFAIR_APPID = "ahpagrcrf2p7m6rg"
    const val X_THEFAIR_AUTH = "uHkwPEjMsV9UKJZpLT1nWjdmiBnNMr9FTqll6Foa5WUQ9sidzNXqwNpRx2t4Xb5IbX4zkYvaTVIb2HuP1My7l0fh0u8bMwrUQOxd6B6yPTzdRsw2QA0n1uCOyqO8vyFBZQPjLgvyf7RjVplheFSbAhvMrDeyHejkkFHWSQgpHTTjb9+to9Z9yzDqJ6dqbuKbe0d6m3GtIY4/nAiPZt9dYSgHqeUlMAMEo4f8a8qqf/JD2kSAPl2a8JPPgTi0egnoOSpi+tHf8dVnZSl8zd0y1A=="
    const val X_THEFAIR_CID = "12a50409f39708370d69ee9951505c2c"
    const val X_THEFAIR_UA = "EYEPETIZER/7090000 (V2410A;android;15;zh_CN;android;7.9.0;cn-bj;huawei;12a50409f39708370d69ee9951505c2c;NONE;1080*2163) native/1.0"
    const val USER_AGENT = "EYEPETIZER/7090000 (V2410A;android;15;zh_CN;android;7.9.0;cn-bj;huawei;12a50409f39708370d69ee9951505c2c;NONE;1080*2163) native/1.0"
    const val COOKIE = "ky_udid=58d1cf919db5480fbf33d4e306642a4e;ky_auth=;APPID=ahpagrcrf2p7m6rg;PHPSESSID=a8ee7bee9cce9d3c1f8bdb0602d17781"
}
~~~
