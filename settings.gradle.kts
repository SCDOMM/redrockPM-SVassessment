pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Eyepetater"
include(":app")
include(":ept_home")
include(":ept_daily")
include(":ept_dicover")
include(":ept_person")
include(":core")
include(":core:core_model")
include(":core:core_network")
include(":core:core_common")
include(":core:core_media")
include(":ept_hot")
include(":ept_search")
include(":ept_category")
