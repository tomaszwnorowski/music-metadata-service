plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

    id("io.spring.dependency-management")
    id("org.springframework.boot")

    id("mms.build.main-conventions")
    id("mms.build.static-code-analysis-conventions")
    id("mms.build.jib-conventions")
}
