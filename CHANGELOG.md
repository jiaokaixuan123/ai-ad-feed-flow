# Changelog

本项目遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 格式。
版本号对应 `app/build.gradle.kts` 中的 `versionName`。

---

## [Unreleased]

---

## [0.2.0] — 2026-06-05（Day 11）乙

### Added

**数据模型**
- `AdType` 新增 `IMAGE_TEXT` 枚举值，与设计文档四种卡片类型对齐
- `AdItem` 新增 `images: List<String>` 字段（图文卡片的多图 URL）
- `InteractionState` 新增 `impressionCount: Int` 字段（曝光次数）

**曝光埋点（Day 11 核心）**
- `InteractionStore.recordImpression()` — 累加曝光次数
- `FeedRepository.recordImpression()` — 透传到 Store
- `FeedViewModel.recordImpression()` — 暴露给视图层；甲在判断"可见 ≥50% 且 ≥500ms"后调用

**依赖库**
- Glide 4.16.0（图片加载）
- Retrofit 2.11.0 + OkHttp 4.12.0（网络层）
- Moshi 1.15.2（JSON 解析，反射模式）
- Media3 ExoPlayer 1.4.1（视频播放 + SimpleCache）

**网络层**
- `data/network/FeedApi.kt` — Retrofit suspend 接口（`GET /feed`、`POST /search`）
- `data/network/FeedResponse.kt` — 响应 DTO（FeedResponseDto、AdItemDto、SearchResponseDto 等）
- `data/source/RetrofitFeedDataSource.kt` — 实现 `FeedDataSource`，含 DTO→Domain 映射

**图片加载封装**
- `util/ImageLoader.kt` — Glide 统一入口（`loadCover` / `loadFitCenter` / `load`）
  甲在 ViewHolder 中只调此入口，禁止散写 Glide 配置

**ExoPlayer 播放器池**
- `player/PlayerPool.kt` — 维护最多 2 个 ExoPlayer 实例，内置 100MB SimpleCache
  接口：`bindPlayer(holderKey, videoUrl)` / `unbindPlayer(holderKey)` / `release()`

**装配层**
- `App.kt` — 新增 `Application` 子类，`onCreate` 中调 `AppGraph.init(context)`
- `AppGraph.kt` 重构：
  - `USE_MOCK` 常量（`true` = Mock，`false` = 真实网络，一行切换）
  - Retrofit + OkHttp（10s/15s 超时，Debug 全量日志）+ Moshi 完整装配
  - `playerPool` 单例（由 `App.init` 初始化）
  - 后端地址 `BASE_URL = "http://10.0.2.2:8000/"`（模拟器访问宿主机）
- `AndroidManifest.xml` — 注册 `App`，添加 `INTERNET` 权限

**Python FastAPI 后端**（`backend/`）
- `main.py` — `GET /feed`（分页）、`POST /search`（对话搜索，调 Qwen）、`GET /health`
- `data.py` — 18 条静态广告数据（精选/电商/本地各 6 条，含 picsum 封面 + 公开 mp4）
- `generate_ai.py` — 离线 AI 预生成脚本：
  - 调通义千问批量生成摘要 + 结构化标签（品类/风格/受众/场景）
  - JSON schema 校验，格式不对则最多重试 3 次
  - 幂等缓存（已生成的 ad_id 跳过），进度实时写入 `ai_cache.json`
  - 完成后注入 `data.py` 的 `AI_CACHE`，随 `/feed` 接口下发
- `requirements.txt`、`README.md`

**单元测试新增用例**
- `MockFeedDataSourceTest`：`containsAllFourAdTypes`、`imageTextItemsHaveImages`、`getByIdReturnsCorrectItem`
- `InteractionStoreTest`：`recordsImpressionCount`、`togglingLikeTwiceRestoresOriginalState`、`stateFlowValueReflectsLatestChanges`
- `FeedRepositoryTest`：`recordImpressionUpdatesState`、`getCardReturnsMergedCardById`、`loadNextPageAppendsToPreviousPage`
- `FeedViewModelTest`：`refreshReloadsFromFirstPage`、`loadNextPageIgnoredWhenNoMore`、`interactionStateChangeSyncsToUiState`、`recordImpressionDelegatesToRepository`、`initialUiStateHasNoError`

### Changed

**数据层异步化**
- `FeedDataSource` 接口：`getPage` / `getById` 改为 `suspend fun`
- `FeedRepository`：`loadPage` / `getCard` 改为 `suspend fun`，内部加 `withContext(Dispatchers.IO)`
- `FeedViewModel`：`loadFirstPage` / `refresh` / `loadNextPage` 改为 `viewModelScope.launch`，新增 try/catch，错误写入 `FeedUiState.errorMessage`
- `DetailViewModel`：`init` 块改为单一协程，`refreshCard` 改为 `suspend fun`
- `MockFeedDataSource`：`getPage` / `getById` 加 `suspend` 修饰符
- `MockFeedDataSource`：类型循环从 `index % 3` 改为 `index % 4`，覆盖四种类型

**FeedAdapter**
- `getItemViewType` 补全 `IMAGE_TEXT` case（`VIEW_TYPE_IMAGE_TEXT = 3`）
- `onCreateViewHolder` 添加 `IMAGE_TEXT` 分支，临时 fallback 到 `BigImageAdViewHolder`（留 TODO 给甲实现 `ImageTextAdViewHolder`）
- 常量改为 `companion object`（`private` → `internal`，供甲引用）

**单元测试**
- `MockFeedDataSourceTest` / `FeedRepositoryTest`：全部测试方法改为 `runTest`（适配 suspend）

### Fixed

- `FeedViewModel` 原在主线程同步调用 `repository.loadPage()`，切网络后会 ANR；改为协程后修复

---

## [0.1.0] — 2026-06-04（Day 1–10）甲+乙

### Added

**客户端 MVP**（`app/`）
- MVVM 架构骨架：`MainActivity` + `TabLayout` + `ViewPager2` + 3 个频道 `FeedFragment`
- `FeedAdapter`（`ListAdapter` + `DiffUtil`）支持大图 / 小图 / 视频三种卡片
- `BigImageAdViewHolder` / `SmallImageAdViewHolder` / `VideoAdViewHolder` + 对应 XML
- `FeedViewModel`（首页加载 / 下拉刷新 / 上拉加载更多 / 三态 UI）
- `DetailActivity` + `DetailViewModel`（进入记录点击、互动状态订阅）
- `FeedRepository`（内存分页缓存，合并内容数据与互动状态）
- `InteractionStore`（StateFlow 驱动的点赞/收藏/分享/点击状态，跨页同步）
- `MockFeedDataSource`（三种类型 × 三个频道 × 12 条，无需后端即可运行）
- `AppGraph`（全局单例容器，`feedRepository` 跨页共享）
- `FeedUiState` / `DetailUiState`（完整 UI 状态结构）
- `AdCardFormatter`（ViewHolder 公用格式化工具）
- 数据模型：`AdItem` / `AdType` / `FeedChannel` / `InteractionState` / `FeedCardUiModel` / `PageResult`
- 基础单元测试：`MockFeedDataSourceTest` / `InteractionStoreTest` / `FeedRepositoryTest` / `FeedViewModelTest`
- 工程文档：`docs/技术设计文档.md`、`docs/工程框架说明.md`、`docs/分工和职责边界.md`

---

[Unreleased]: https://github.com/your-repo/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/your-repo/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/your-repo/releases/tag/v0.1.0
