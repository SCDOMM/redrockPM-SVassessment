# Eyepetater

Multi-module Android app (开眼/Eyepetizer clone) using Jetpack Compose + Material3 + Fragments. Video-centric app with network layer, data models, and feature modules.

## Build

Standard Gradle wrapper (Gradle 9.4.1, AGP 9.2.1, Kotlin 2.2.10). All commands run from project root:

- Build: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`)
- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`
- Clean: `./gradlew clean`

Version catalog at `gradle/libs.versions.toml`.

## Module structure

| Module | Namespace | Purpose |
|--------|-----------|---------|
| `app` | `com.example.eyepetater` | Main application (only real code lives here) |
| `core` | `com.example.core` | Base library module |
| `core:core_model` | `com.example.core.model` | Data models (video, tag, navigation, etc.) |
| `core:core_network` | `com.example.core.network` | Network layer (Retrofit + KaiyanApi) |
| `core:core_common` | `com.example.core.common` | Common utilities |
| `core:core_media` | `com.example.core.media` | Media utilities (empty) |
| `ept_home` | `com.example.ept.home` | Home feature (Fragment + ViewModel) |
| `ept_daily` | `com.example.ept.daily` | Daily feature (empty) |
| `ept_dicover` | `com.example.ept.dicover` | Discover feature (empty) |
| `ept_person` | `com.example.ept.person` | Person/profile feature (empty) |
| `ept_hot` | `com.example.ept.hot` | Hot/rankings feature (Fragment + ViewPager2) |

Feature modules use the `ept_` prefix. `core` is a parent module with `:core:*` sub-modules.

## Architecture

- **Pattern**: MVVM with ViewModels + Fragments (not Compose Navigation yet)
- **UI**: Mix of Compose (app module) and XML layouts (feature modules use ViewBinding)
- **Network**: Retrofit 2.11.0 + OkHttp 4.12.0 + Gson 2.11.0
- **Video**: GSYVideoPlayer v13.1.0 (io.github.carguo:gsyvideoplayer)
- **API**: 开眼 (Eyepetizer) unofficial API at http://baobab.kaiyanapp.com/api/

## Key facts

- compileSdk 36, minSdk 26, targetSdk 36, Java 11 source compat
- Configuration cache enabled (`org.gradle.configuration-cache=true`)
- No DI framework, no navigation graph, no `.gitignore` at root
- `compileSdk` uses AGP 9.x `release(36) { minorApiLevel = 1 }` syntax (not the older integer form)
- `ept_dicover` is intentionally misspelled — match the existing name

## AGP 9.x Gotcha

In AGP 9.2.1, applying `id("org.jetbrains.kotlin.android")` to an `android.library` module causes "Cannot add extension with name 'kotlin'" error. The Android library plugin already registers the Kotlin extension. **Do NOT add explicit Kotlin plugin to library modules.**

## API Reference

Full API documentation at `docs/KAIYAN_API.md`. Key endpoints:
- Home: v5/index/tab/list, v5/index/tab/{tabId}, v7/index/tab/discovery
- Rankings: v4/rankList (returns tabInfo), v4/rankList/videos?strategy=weekly|monthly|historical
- Tags: v6/tag/index?id={id}, v1/tag/videos?id={id}, v6/tag/dynamics?id={id}
- Video: v4/video/related?id={id}, v1/playUrl?vid={id}&editionType={type}&source={source}
- Search: v3/queries/hot, v3/search?query={keyword}

## Data Models

All in `core:core_model` under `com.example.core.model`:
- `EyepetizerResponse` — generic response wrapper
- `RankListResponse` — rank list with tabInfo
- `Item`, `TitleItem` — list items
- `videoEntity/` — VideoData, VideoSmallCardData, UgcSelectedData
- `tag/` — Tag, TagInfoCollectionData, Label, ColorData
- `navigation/` — TabInfo, NavigationData, TextHeaderData, TextFooterData
- `media/` — PlayInfo, Cover, Provider, UrlItem, WebUrl
- `card/` — FollowCardData, BannerData, AtmosphericData, InformationCardData
- `author/` — Author, Follow, Shield, ReplyData, AuthorRecommendationCardData
- `interact/` — Consumption
