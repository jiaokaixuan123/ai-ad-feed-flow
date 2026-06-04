# AI 广告信息流 Android MVP

这是训练营课题“AI 广告推荐信息流”的 Android 端最小工程单位。当前版本先用本地 mock 数据跑通单列信息流、频道切换、分页、详情页和互动状态同步，后续可以在同一套 MVVM 边界内接入 Retrofit、Glide、Media3 ExoPlayer 和后端 AI 接口。

## 技术选型

- Kotlin
- View 体系 + XML + ViewBinding
- MVVM
- TabLayout + ViewPager2
- RecyclerView + ListAdapter + DiffUtil
- StateFlow
- 本地 Mock 数据源

## 运行方式

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

Android Studio 打开工程后，直接运行 `app` 即可。

## 当前功能

- 精选 / 电商 / 本地 三个频道 Tab。
- 单列 RecyclerView 信息流。
- 大图、小图、视频封面三种广告卡片。
- 下拉刷新和上拉加载更多。
- 点击卡片进入详情页。
- 详情页与列表共享点赞、收藏、分享、点击状态。
- 卡片展示 mock AI 摘要和标签。

## 新手阅读指南

如果你刚开始学 Android，建议先读：

- [工程框架新手说明](docs/工程框架说明.md)

## 模块分工建议

同学 A：信息流 UI 与交互

- `feed/FeedFragment.kt`
- `feed/FeedPagerAdapter.kt`
- `feed/adapter/*`
- `detail/DetailActivity.kt`
- `res/layout/*`

同学 B：数据层、状态层与后续 AI 接口

- `data/model/*`
- `data/source/*`
- `data/repository/*`
- `data/store/*`
- `feed/FeedViewModel.kt`
- 单元测试与后续 Retrofit 接口

共同维护：

- `AppGraph.kt`
- `README.md`
- `docs/`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

## AI 辅助声明

本工程当前 MVP 由 AI 辅助完成工程阅读、方案拆分、MVVM 骨架设计、代码生成和测试编写。验证方式包括：

- 使用单元测试验证 mock 分页、互动状态、Repository 合并状态、FeedViewModel 加载状态。
- 使用 Gradle 构建验证 Android 资源、ViewBinding 和 Kotlin 编译。
- mock AI 摘要和标签均为示例数据，不代表真实大模型输出。

后续接入真实大模型时，需要在后端对 AI 输出做结构化 JSON 约束、schema 校验、失败重试和缓存。

## 后续扩展顺序

1. 接入 Glide 展示真实远程图片。
2. 接入 Retrofit + OkHttp + Moshi 替换 `MockFeedDataSource`。
3. 接入 Media3 ExoPlayer，实现视频卡片和详情页播放。
4. 增加标签点击过滤。
5. 增加曝光 / 点击统计和可视化。
6. 接入对话式搜索。
