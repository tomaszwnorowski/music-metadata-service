plugins {
    id("mms.build.spring-boot-application-conventions")
    id("mms.build.test-conventions")
}

dependencies {
    // main
    implementation(libs.spring.boot.autoconfigure)
    runtimeOnly(project(":rest"))
    runtimeOnly(libs.spring.boot.starter.actuator)

    // local execution
    testImplementation(project(":artist-api"))
    testRuntimeOnly(project(":artist-embedded"))
    testImplementation(project(":track-api"))
    testRuntimeOnly(project(":track-embedded"))
    testImplementation(project(":engine-api"))
    testRuntimeOnly(project(":engine-embedded"))
    testImplementation(libs.test.springboot.testcontainers)
    testImplementation(libs.test.testcontainers.core)
    testImplementation(libs.test.testcontainers.postgresql)
}
