pluginManagement {
    includeBuild("gradle/plugins")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "music-metadata-service"
include("app")
include("core")
include("db")
include("artist-api")
include("artist-embedded")
include("track-api")
include("track-embedded")
include("engine-api")
include("engine-embedded")
include("rest")
include("test")
